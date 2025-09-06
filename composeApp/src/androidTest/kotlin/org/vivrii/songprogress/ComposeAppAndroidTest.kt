package org.vivrii.songprogress

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.vivrii.songprogress.data.network.SpotifyApi
import org.vivrii.songprogress.util.KmpFile

@RunWith(AndroidJUnit4::class)
class ComposeAppAndroidTest {
    @Test
    fun cacheDirTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        KmpFile.init(context)
        println(KmpFile.cacheDir())
    }

    @Test
    fun spotifyTest() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        KmpFile.init(context)

        val api = SpotifyApi()

        val artistId = "079cEjjSpv5dOLH5xsGdvN"
        val artist = api.getArtist(artistId)
        println("Artist: ${artist.name}")

        val albums = api.getArtistAlbums(artistId)
        println("Albums: ${albums.map { it.name }}")

        albums.firstOrNull()?.let { album ->
            val apiSecond = SpotifyApi() // allows testing for token ache success (check print)
            val tracks = apiSecond.getAlbumTracks(album.id)
            println("Tracks: ${tracks.map { it.name }}")
        }
    }
}
