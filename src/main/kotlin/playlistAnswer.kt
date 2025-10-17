import java.io.File
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
//import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.full.*
//import kotlinx.serialization.*
//import kotlinx.serialization.json.*

data class Song(
    var title: String = "null",
    var artist: String = "null" ,
    var album: String = "null" ,
    var duration: Int = 0
)

data class Playlist(
    var playlistName: String = "null",
    var createdAt: String = "null",
    var songs: MutableList<Song> = mutableListOf(),
    var totalSongs: Int = 0,
    var totalDuration: Int = 0
)

enum class Type {
    OPEN_BRACE,
    OPEN_ARRAY,
    CLOSE_BRACE,
    CLOSE_ARRAY,
    KEY,
    STRING_VALUE,
    INT_VALUE,
    KEY_VALUE_SEP,
    COMMA
}

sealed class MetaData(val type: Type) {
    data object OpenBrace  : MetaData(Type.OPEN_BRACE)
    data object OpenArray  : MetaData(Type.OPEN_ARRAY)
    data object CloseBrace : MetaData(Type.CLOSE_BRACE)
    data object CloseArray : MetaData(Type.CLOSE_ARRAY)
    data object Comma      : MetaData(Type.COMMA)
    data object KeyValueSep: MetaData(Type.KEY_VALUE_SEP)

    data class Key(val key: String) : MetaData(Type.KEY)
    data class StringValue(val value: String): MetaData(Type.STRING_VALUE)
    data class IntValue(val value: Int): MetaData(Type.INT_VALUE)
}

class Tokenizer {
    var result: MutableList<String> = mutableListOf()
    var isClosedQuotes: Boolean = true // "" 안에 있는 띄어쓰기는 문자열로 인식
    var store: MutableList<Char> = mutableListOf()

    //지금까지 담아놓은 값을 result에 저장
    fun flush() {
        if (store.isNotEmpty()) {
            result.add(store.joinToString(""))
            store.clear()
        }
    }

    fun convert(code: String): List<String> {

        for (i in 0..<code.length) {
            val char = code[i]

            if ( isClosedQuotes && char == ' '|| (char == '\n' || char == '\r')) { // ""안에 있지 않은 공백과 엔터(라인피드 + 캐리지 리턴)를 무시
                continue
            }

            if (char == '{' || char == '}' || char == '[' || char == ']') {

                if (char == ']' || char == '}') { // 닫힐 때 그동안 담고있던 값 flush
                    flush()
                }

                result.add(char.toString())
                continue
            }

            if (char == ':' || char == ',') {
                flush()
                result.add(char.toString())
                continue
            }

            if (char == '"') { // 탭을 포함 여부 플래그 갱신, " " 안에있는 탭은 포함, 그냥 있는 탭은 무시
                isClosedQuotes = !isClosedQuotes
            }

            store.add(char)
        }
        return result
    }
}

class Lexer {
    var isKeyRegisterd: Boolean = false
    var result: MutableList<MetaData> = mutableListOf()

    fun convert(tokens: List<String>): List<MetaData> {

        for (token in tokens) {
            when(token) {
                "{" -> { result.add(MetaData.OpenBrace)  }
                "[" -> { result.add(MetaData.OpenArray)
                    isKeyRegisterd = false
                }
                "}" -> { result.add(MetaData.CloseBrace) }
                "]" -> { result.add(MetaData.CloseArray) }
                "," -> { result.add(MetaData.Comma)      }
                ":" -> {
                    isKeyRegisterd = true
                    result.add(MetaData.KeyValueSep)
                }
                else -> {
                    if (isKeyRegisterd) {
                        if (token.startsWith("\"") && token.endsWith("\"")) {
                            // 앞뒤 "" 제거
                            val value = token.substring(1, token.length - 1)
                            result.add(MetaData.StringValue(value))

                        } else {
                            result.add(MetaData.IntValue(token.toInt()))
                        }

                        isKeyRegisterd = false
                    } else {
                        val key = token.substring(1, token.length - 1)
                        result.add(MetaData.Key(key))
                    }
                }
            }
        }

        return  result
    }
}

class Parser {
    var songs: MutableList<Song> = mutableListOf()
    var playlist: Playlist = Playlist()
    var song: Song = Song()
    var isArrayOpened: Boolean = false
    var keyArray: MutableList<String> = mutableListOf()
    var valueArray: MutableList<Any> = mutableListOf()

    fun convert(metadatas: List<MetaData>): Playlist {

        for (metadata in metadatas) {
            when(metadata) {
                MetaData.CloseArray -> { // 배열 끝나면
                    valueArray.add(songs) // valueArray에 song  넣기
                    isArrayOpened = false
                }
                MetaData.CloseBrace -> {
                    save()
                    if (isArrayOpened) { // songs끝나면 이제 생성 song 객체 사용 안함
                        songs.add(song)
                        song = Song() // 새로운 Song 저장을위해 객체 생성
                    }
                }
                MetaData.Comma -> {
                    save()
                }
                is MetaData.IntValue -> {
                    valueArray.add(metadata.value)
                }
                is MetaData.Key -> {
                    keyArray.add(metadata.key)
                }
                MetaData.KeyValueSep -> { }
                MetaData.OpenArray -> { isArrayOpened = true  }
                MetaData.OpenBrace -> {  }
                is MetaData.StringValue -> {
                    valueArray.add(metadata.value)
                }
            }
        }

        return playlist
    }

    fun save() {
        if (keyArray.isEmpty() || valueArray.isEmpty()) {
            return
        }

        val key = keyArray.removeLast()
        val value = valueArray.removeLast()
        setPropertyValue(key, value)
    }

    fun setPropertyValue(propertyName: String, value: Any) {
        val isPlaylistProps = playlist::class.memberProperties.firstOrNull{it.name == propertyName }  != null

        if (isPlaylistProps) {
            var prop = playlist::class.memberProperties.find { it.name == propertyName }
            if (prop is kotlin.reflect. KMutableProperty1<*, *>) {
                prop.isAccessible = true
                (prop as KMutableProperty1<Any, Any?>).set(playlist, value)
            } else {
                throw IllegalArgumentException("Property $propertyName is not mutable")
            }
        } else {
            var prop = song::class.memberProperties.find { it.name == propertyName }
            if (prop is kotlin.reflect. KMutableProperty1<*, *>) {
                prop.isAccessible = true
                (prop as KMutableProperty1<Any, Any?>).set(song, value)
            } else {
                throw IllegalArgumentException("Property $song is not mutable")
            }
        }
    }
}



fun main() {
    val file = File("./src/main/resources/playlist.json") // 현재 디렉토리

    val tokenizer = Tokenizer()
    val lexer = Lexer()
    val parser = Parser()
    val resultOfTokenizer = tokenizer.convert(file.readText())

    resultOfTokenizer.forEach {
        println(it)
    }

    val resultOfLexer = lexer.convert(resultOfTokenizer)
    val playlist = parser.convert(resultOfLexer)

    playlist::class.memberProperties.forEach { prop ->
        val p = prop as KProperty1<Playlist, Any?>
        println("🌟 ${p.name} = ${p.get(playlist)}")
    }
}