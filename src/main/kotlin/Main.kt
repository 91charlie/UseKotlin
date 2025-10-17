import java.io.File
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

enum class L3xer() {
    k3y,
    valu3,
    KeyValueSeperator,
    c0mma,
    OMidBracket,
    CMidBracket,
    OListBracket,
    CListBracket
}
fun tokenizer(file: String): MutableList<String>{
    val tokenList: MutableList<String> = mutableListOf()
    var letters: String = ""
    var letterOpen: Boolean = false
    for(L in file)
    {
        if(L == '{'||L == '['||L == ']')
        {
            tokenList.add(L.toString())
        }
        else if(L == '"')
        {
            letters += L
            letterOpen = !letterOpen
        }
        else if(L == ':' || L == ',')
        {
            if(letters != "") {
                tokenList.add(letters)
            }
            tokenList.add(L.toString())
            letters =  ""
        }
        else if(L == '}')
        {
            tokenList.add(letters)
            tokenList.add(L.toString())
            letters = ""
        }
        else if(L == '\n' || L == '\r'|| L == ' ')
        {
            if(letterOpen && L == ' ')
            {
                letters += L
            }
            else
            {
            continue
            }
        }
        else
        {
            letters += L
        }
    }
    return tokenList
}
fun l3xer(tokened: MutableList<String>): MutableList<L3xer> {
    val reversedList = tokened.reversed()
    val l3xerList: MutableList<L3xer> = mutableListOf()
    var keyOrValue: Boolean = false
    for(token in reversedList)
    {
        if(token == "{")
        {
            l3xerList.add(L3xer.OMidBracket)
        }
        else if(token == "}")
        {
            l3xerList.add(L3xer.CMidBracket)
        }
        else if(token == ":")
        {
            l3xerList.add(L3xer.KeyValueSeperator)
            keyOrValue = true
        }
        else if(token == ",")
        {
            l3xerList.add(L3xer.c0mma)
        }
        else if(token == "[")
        {
            l3xerList.add(L3xer.OListBracket)
        }
        else if(token == "]")
        {
            l3xerList.add(L3xer.CListBracket)
        }
        else{
            if(keyOrValue)
            {
                l3xerList.add(L3xer.k3y)
                keyOrValue = false
            }
            else{
                l3xerList.add(L3xer.valu3)
            }

        }
    }
    val result = l3xerList.reversed().toMutableList()
    return result
}
class Parserr() {
    fun pars3r(tokened: MutableList<String>, lexed: MutableList<L3xer>) {
        for (data in lexed.indices) {
            when (lexed[data]) {
                L3xer.k3y -> TODO()
                L3xer.valu3 -> TODO()
                L3xer.KeyValueSeperator -> TODO()
                L3xer.c0mma -> TODO()
                L3xer.OMidBracket -> TODO()
                L3xer.CMidBracket -> TODO()
                L3xer.OListBracket -> TODO()
                L3xer.CListBracket -> TODO()
            }
        }
    }
}
fun main()
{
    val file = File("./src/main/resources/playlist.json").readText()
    val a = tokenizer(file)
    val b = l3xer(a)
}

// 파서 만들기
// 반복문으로 토크나이저와 렉서를 읽는다 .indices 사용
// 렉서 key 가 나올 경우 key 이름 저장 추후 setter의 경로가 된다
// 렉서 value 가 나올 경우 키 이름 setter를 사용해서 값을 할당한다.

// song 과 playlist를 구분해야 하기 때문에 fun song을 따로 만든다

// 또는 key value를 종합한뒤 따로 할당하는 함수를 만든다.
// playlist에 포함하는지 검사한뒤 없으면 전부 song으로 만든다