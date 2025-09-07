package org.vivrii.songprogress.data.files

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
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
        val jsonObj = json.decodeFromString<List<SpotifyDataEntry>>(jsonString)
        return SpotifyDataImport().apply {
            jsonObj.map { element ->
                // update artist info
                artists
                    // create or get artist
                    .getOrPut(element.artist) { Artist(name = element.artist) }
                    .apply {
                        tracks
                            // create or get track
                            .getOrPut(element.track) { Track(title = element.track) }
                            .also { track ->
                                // create or get album
                                element.album?.let { album ->
                                    albums.getOrPut(album) { Album(title = album) }
                                        // add this track to the album so that it is shared with the artist level tracks map
                                        .tracks.getOrPut(track.title) { track }
                                }
                            }
                            .listenCount += 1
                    }

                // update date range covered by input
                // todo: this assumes (I think correctly) that the entries are in increasing time order. check performance loss of actually ensuring it is
                //  that way during the import process.
                start?.let {
                    end = element.timestamp
                } ?: run {
                    start = element.timestamp
                }
            }
        }
    }
}

@Serializable
@Suppress("SERIALIZER_NOT_FOUND")
@OptIn(ExperimentalTime::class, InternalSerializationApi::class, ExperimentalSerializationApi::class)
data class SpotifyDataEntry(
    @Serializable(with = InstantSerializer::class)
    @JsonNames("endTime", "ts") val timestamp: Instant,
    @JsonNames("artistName", "master_metadata_album_artist_name")val artist: String,
    @JsonNames("master_metadata_album_album_name") val album: String? = null,
    @JsonNames("trackName", "master_metadata_track_name") val track: String,
)
