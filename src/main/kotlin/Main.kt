package org.example
import kotlin.reflect.*
import netscape.javascript.JSObject
import java.io.File
import java.lang.StringBuilder
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty0
import kotlin.reflect.full.memberProperties
import kotlin.text.iterator
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.primaryConstructor


//import kotlinx.serialization.*
//import kotlinx.serialization.json.*
//import kotlinx.serialization.descriptors.*
//import kotlinx.serialization.encoding.*


//@Serializable
data class Playlist(
    var playlistName: String = "",
    var createdAt: String = "",
    var totalSongs: Int = 0,
    var totalDuration: Int = 0,
    var songs: MutableList<Song> = mutableListOf()
){
}

data class Song(
    var title: String = "",
    var artist: String = "",
    var album: String = "",
    var duration: Int = 0
)

enum class Lexer()
{
    key,
    value,
    kvseperator,
    comma,
    midBoxOpen,
    midBoxClose,
    listBoxOpen,
    listBoxClose
}

fun String.jsonToker(): List<String>
{
    var isOpen: Boolean = false
    var letters: String = ""
    val result: MutableList<String> = mutableListOf()

    for(letter in this)
    {
        if(letter == '{'|| letter == '[' || letter == ']'){
            result.add(letter.toString())
        }
        else if(letter == '"') {
            isOpen = !isOpen
            letters += letter
        }
        else if(letter == ':' || letter == ',' || letter == '}')
        {
            if(!letters.isEmpty()){
                result.add(letters)
            }
            letters = ""
            result.add(letter.toString())
        }
        else if(letter == ' ')
        {
            if(isOpen) {
                letters += letter
            }
        }
        else if(letter == '\r' || letter == '\n')
        {
            continue
        }
        else
        {
            letters += letter
        }
    }
    return result
}
fun JsonLexer(tokenedFile: List<String>) : MutableList<Lexer>
{
    val fileResult = tokenedFile.reversed()
    val lexerlist: MutableList<Lexer> = mutableListOf()
    var read: Boolean = false
    for(i in fileResult)
    {

        if(i == "{")
        {
            lexerlist.add(Lexer.midBoxOpen)
        }
        else if(i == ":")
        {
            lexerlist.add(Lexer.kvseperator)
            read = true
        }
        else if(i == "}")
        {
            lexerlist.add(Lexer.midBoxClose)
        }
        else if(i == "[")
        {
            lexerlist.add(Lexer.listBoxOpen)
        }
        else if(i == "]")
        {
            lexerlist.add(Lexer.listBoxClose)
        }
        else if(i == ",")
        {
            lexerlist.add(Lexer.comma)
        }
        else
        {
            if(!read)
            {
                lexerlist.add(Lexer.value)
            }
            else
            {
                lexerlist.add(Lexer.key)
                read = false
            }
        }
    }
    return lexerlist.reversed().toMutableList()
}


fun main() {
    val file = File("./src/main/resources/playlist.json") // 현재 디렉토리

    val fileResult = file.readText().jsonToker()
    val lexerlist: MutableList<Lexer> = JsonLexer(fileResult)
    var playlist = Playlist()
    var song = Song()
    val songMirror = song::class.memberProperties.map { it.name }
    val playlistMirror = playlist::class

    val a1= Song::title.set(song,fileResult[15])
    println(songMirror)
    println(song.title)


    for(i in 0 until lexerlist.size)
    {
       var count: Int = 0
        var SongOrPlayList: Boolean = false
        var keyName: String = ""

       when(lexerlist[i])
       {
            Lexer.key -> keyName = fileResult[count]
            Lexer.value -> TODO()// 분기에 따른 Song or playList :: 키 이름. set(인스턴스, fileResult[count]) value 삽입
            Lexer.kvseperator -> TODO()
            Lexer.comma -> TODO()//playlist.songs.add(song)
            Lexer.midBoxOpen -> TODO()// song 초기화
            Lexer.midBoxClose -> TODO()
            Lexer.listBoxOpen -> TODO()// 분기 결정
            Lexer.listBoxClose ->TODO()// 분기 결정
       }
        count++
   }
    println("${fileResult.size},${lexerlist.size}")


}
// 리플렉션, 코드 복기, enum 클래스
// 렉서가 의미와 값을 모두 가지고 있어야한다.
// 파서의 결과 - AST abstract syntax tree
// 멤버 프로퍼티로 리스트 만들기 또는 키와 일치하는 이름으로 접근하여 밸류 삽입하기



// { == 클래스 결정
// key == 프로퍼티 결정
// value 클래스.프로퍼티 값 할당