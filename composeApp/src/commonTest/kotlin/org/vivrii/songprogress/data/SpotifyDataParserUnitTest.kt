package org.vivrii.songprogress.data

import org.vivrii.songprogress.data.files.SpotifyDataParser
import org.vivrii.songprogress.loadResourceFile
import kotlin.test.Test
import kotlin.test.assertEquals

class SpotifyDataParserUnitTest {
    @Test
    fun extendedParseTest() {
        val jsonString = loadResourceFile("ExtendedHistorySample.json")
        val response = SpotifyDataParser().parse(jsonString)
        response.values.forEach { artist ->
            artist.albums.values.forEach { album ->
                album.tracks.values.forEach { track ->
                    println("${artist.name}, ${album.title}, ${track.title}: ${track.listenCount}")
                }
            }
        }
        assertEquals(response.size, 3)
        assertEquals(response["doan"]!!.albums["Preaching to the choir"]!!.tracks["Preaching to the choir"]!!.listenCount, 2)
    }
}
