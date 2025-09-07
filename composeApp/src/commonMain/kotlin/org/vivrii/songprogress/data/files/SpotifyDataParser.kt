package org.vivrii.songprogress.data.files

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames
import org.vivrii.songprogress.domain.models.Album
import org.vivrii.songprogress.domain.models.Artist
import org.vivrii.songprogress.domain.models.SpotifyDataImport
import org.vivrii.songprogress.domain.models.Track
import org.vivrii.songprogress.util.InstantSerializer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class SpotifyDataParser(private val json: Json = Json { ignoreUnknownKeys = true }) {
    fun parse(jsonString: String): SpotifyDataImport {
        val spotifyEntries = json.decodeFromString<List<SpotifyDataEntry>>(jsonString)
            .sortedBy { it.timestamp } // TODO: assess performance impact on large datasets

        if (spotifyEntries.isEmpty()) {
            return SpotifyDataImport()
        }

        return SpotifyDataImport(
            start = spotifyEntries.first().timestamp,
            end = spotifyEntries.last().timestamp,
        ).apply {

            spotifyEntries.forEach { element ->
                // create or get fields
                val currentArtist = artists.getOrPut(element.artist) { Artist(name = element.artist) }
                val currentTrack = currentArtist.tracks.getOrPut(element.track) { Track(title = element.track) }

                // update play count
                currentTrack.listenCount += 1

                // check for album information
                element.album?.let { albumName ->
                    val currentAlbum = currentArtist.albums.getOrPut(albumName) { Album(title = albumName) }
                    // shared track between artists song list and album entry
                    // allows exact song matches between singles/albums to share play count
                    currentAlbum.tracks.getOrPut(currentTrack.title) { currentTrack }
                }
            }
        }
    }
}

@Serializable
@OptIn(ExperimentalTime::class, ExperimentalSerializationApi::class)
data class SpotifyDataEntry(
    @Serializable(with = InstantSerializer::class)
    @JsonNames("endTime", "ts") val timestamp: Instant,
    @JsonNames("artistName", "master_metadata_album_artist_name") val artist: String,
    @JsonNames("master_metadata_album_album_name") val album: String? = null,
    @JsonNames("trackName", "master_metadata_track_name") val track: String,
)
