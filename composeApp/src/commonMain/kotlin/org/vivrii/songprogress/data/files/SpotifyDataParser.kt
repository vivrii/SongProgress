package org.vivrii.songprogress.data.files

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.vivrii.songprogress.domain.models.Album
import org.vivrii.songprogress.domain.models.Artist
import org.vivrii.songprogress.domain.models.Track

class SpotifyDataParser(private val json: Json = Json { ignoreUnknownKeys = true }) {
    fun parse(jsonString: String): HashMap<String, Artist> {
        val jsonObj = json.decodeFromString<List<SpotifyDataEntry>>(jsonString)
        return HashMap<String, Artist>().apply {
            jsonObj.map { element ->
                getOrPut(element.artist) { Artist(name = element.artist) }
                    .albums.getOrPut(element.album) { Album(title = element.album) }
                    .tracks.getOrPut(element.track) { Track(title = element.track) }
                    .listenCount += 1
            }
        }
    }
}

@Serializable
data class SpotifyDataEntry(
    @SerialName("master_metadata_album_artist_name") val artist: String,
    @SerialName("master_metadata_album_album_name") val album: String,
    @SerialName("master_metadata_track_name") val track: String,
)
