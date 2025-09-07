package org.vivrii.songprogress.data

import org.vivrii.songprogress.data.files.SpotifyDataParser
import org.vivrii.songprogress.loadResourceFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SpotifyDataParserUnitTest {
    @Test
    fun extendedParseTest() {
        val jsonString = loadResourceFile("ExtendedHistorySample.json")
        val response = SpotifyDataParser().parse(jsonString)
        response.artists.values.forEach { artist ->
            artist.albums.values.forEach { album ->
                album.tracks.values.forEach { track ->
                    println("${artist.name}, ${album.title}, ${track.title}: ${track.listenCount}")
                }
            }
        }
        println("imported date range: ${response.start}, ${response.end}")
        assertEquals(response.artists.size, 3)
        assertEquals(response.artists["doan"]!!.albums["Preaching to the choir"]!!.tracks["Preaching to the choir"]!!.listenCount, 2)
    }

    @Test
    fun parseTest() {
        val jsonString = loadResourceFile("HistorySample.json")
        val response = SpotifyDataParser().parse(jsonString)
        response.artists.values.forEach { artist ->
            artist.tracks.values.forEach { track ->
                println("${artist.name}, ${track.title}: ${track.listenCount}")
            }
            assertEquals(artist.albums.size, 0)
        }
        println("imported date range: ${response.start}, ${response.end}")
        assertEquals(response.artists.size, 5)
        assertEquals(response.artists["¡BangBang Watergun!"]!!.tracks["Such A Loser"]!!.listenCount, 2)
    }
}
