package org.vivrii.songprogress.domain

import org.vivrii.songprogress.domain.models.Album
import org.vivrii.songprogress.domain.models.Artist
import org.vivrii.songprogress.domain.models.Track
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ModelsUnitTest {
    @Test
    fun check_equals_are_equal() {

    }

    @Test
    fun check_hash_mapping() {
        val artistHashMap = HashMap<String, Artist>()
        artistHashMap["a"] = Artist("a")
        artistHashMap["b"] = Artist("b")
        assertNotEquals(artistHashMap["a"], artistHashMap["b"])

        artistHashMap["a"]!!.albums["1"] = Album("a1")
        artistHashMap["b"]!!.albums["1"] = artistHashMap["a"]?.albums["1"]!!
        assertEquals(artistHashMap["a"]!!.albums["1"], artistHashMap["b"]!!.albums["1"])

        artistHashMap["a"]!!.albums["1"]!!.tracks["t1"] = Track("t1")
        artistHashMap["b"]!!.albums["1"]!!.tracks["t1"] = artistHashMap["a"]!!.albums["1"]!!.tracks["t1"]!!
        artistHashMap["b"]!!.albums["1"]!!.tracks["t1"] = Track("t2")
        artistHashMap["a"]!!.albums["1"]!!.tracks["t1"]!!.listenCount += 1
        assertEquals(
            artistHashMap["a"]!!.albums["1"]!!.tracks["t1"]!!.listenCount,
            artistHashMap["b"]!!.albums["1"]!!.tracks["t1"]!!.listenCount
        )
        assertEquals(
            artistHashMap["b"]!!.albums["1"]!!.tracks["t1"]!!.listenCount,
            1
        )
    }
}
