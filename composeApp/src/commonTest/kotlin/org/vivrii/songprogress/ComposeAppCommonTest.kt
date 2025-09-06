package org.vivrii.songprogress

import kotlinx.coroutines.test.runTest
import org.vivrii.songprogress.data.network.SpotifyApi
import kotlin.test.Test

class ComposeAppCommonTest {
    @Test
    fun spotifyApiTest() = runTest {
        val api = SpotifyApi()

        val artistId = "079cEjjSpv5dOLH5xsGdvN"
        val artist = api.getArtist(artistId)
        println("Artist: ${artist.name}")

        val albums = api.getArtistAlbums(artistId)
        println("Albums: ${albums.map { it.name }}")

        albums.firstOrNull()?.let { album ->
            val tracks = api.getAlbumTracks(album.id)
            println("Tracks: ${tracks.map { it.name }}")
        }
    }
}
