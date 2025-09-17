package org.example
import netscape.javascript.JSObject
import java.io.File
import java.lang.StringBuilder
import java.util.*
import kotlin.text.iterator

//import kotlinx.serialization.*
//import kotlinx.serialization.json.*
//import kotlinx.serialization.descriptors.*
//import kotlinx.serialization.encoding.*


//@Serializable
data class Playlist(
    val playlistName: String,
    val createdAt: String,
    val totalSongs: Int,
    val totalDuration: Int,
    val songs: List<Song>
){
}

data class Song(
    val title: String,
    val artist: String,
    val album: String,
    val duration: Int
)

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
            if(letters != "") result.add(letters)
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



fun main() {
    val file = File("./src/main/resources/playlist.json") // 현재 디렉토리

    val fileResult = file.readText().jsonToker()

    var isOpen: Boolean = false
    var letters: String = ""
    val title: MutableList<String> = mutableListOf()
    val artist: MutableList<String> = mutableListOf()
    val album: MutableList<String> = mutableListOf()
    val duration: MutableList<Int> = mutableListOf()
    var playlistName: String = ""
    var createdAt: String = ""
    var totalSongs: Int = 0
    var totalDuration: Int = 0



    for(i in 0 until fileResult.size)
    {
        if("title" in fileResult[i])
        {
            title.add(fileResult[i+2])
        }
        else if("artist" in fileResult[i])
        {
            artist.add(fileResult[i+2])
        }
        else if("album" in fileResult[i])
        {
            album.add(fileResult[i+2])
        }
        else if("duration" in fileResult[i])
        {
            duration.add(fileResult[i+2].toInt())
        }
        else if("playlistName" in fileResult[i])
        {
            playlistName = fileResult[i + 2]
        }
        else if("createdAt" in fileResult[i])
        {
            createdAt = fileResult[i + 2]
        }
        else if("totalSongs" in fileResult[i])
        {
            totalSongs = fileResult[i + 2].toInt()
        }
        else if("totalDuration" in fileResult[i])
        {
            totalDuration = fileResult[i + 2].toInt()
        }
    }
  for(i in 0 until 5)
  {
      println(title[i])
      println(artist[i])
      println(album[i])
      println(duration[i])
  }
    println(playlistName)
    println(createdAt)
    println(totalSongs)
    println(totalDuration)

    val instsong = Song(title[0],artist[0],album[0],duration[0])
    println(instsong)
}

// 렉서 시도
// 타이틀, 아티스트, 앨범, 듀래이션을 배열로 만들고 추가한다
// 플래이리스트네임이나 크레이티드앳 토탈 송스 토탈 듀래이션 은 변수로 만든다
// 그리고 이것을 song에 모두 합친다
// song을 songs에 모두 합친다.\