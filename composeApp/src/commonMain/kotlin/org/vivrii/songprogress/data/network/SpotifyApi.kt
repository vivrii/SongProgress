package org.vivrii.songprogress.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.encodeBase64
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.vivrii.songprogress.domain.models.SpotifyAlbum
import org.vivrii.songprogress.domain.models.SpotifyArtist
import org.vivrii.songprogress.domain.models.SpotifyTrack

class SpotifyApi(private var token: String) {
    private val baseUrl = "https://api.spotify.com/v1"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // TODO: work out why this wasn't working
//    suspend fun getClientToken(clientId: String, clientSecret: String) {
//        val auth = "$clientId:$clientSecret".encodeBase64()
//        val response: TokenResponse = client.post("https://accounts.spotify.com/api/token") {
//            header("Authorization", "Basic $auth")
//            parameter("grant_type", "client_credentials")
//        }.body()
//        token = response.accessToken
//    }

    // TODO: generic way to handle responses that are NOT ok for reasons
    suspend fun getArtist(artistId: String): SpotifyArtist =
        client.get("$baseUrl/artists/$artistId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()

    suspend fun getArtistAlbums(artistId: String): List<SpotifyAlbum> {
        val response: AlbumListResponse = client.get("$baseUrl/artists/$artistId/albums") {
            header(HttpHeaders.Authorization, "Bearer $token")
            parameter("include_groups", "album,single") // there is also appears_on and compilation
            parameter("limit", 50)
        }.body()
        return response.items
    }

    suspend fun getAlbumTracks(albumId: String): List<SpotifyTrack> {
        val response: TrackListResponse = client.get("$baseUrl/albums/$albumId/tracks") {
            header(HttpHeaders.Authorization, "Bearer $token")
            parameter("limit", 50)
        }.body()
        return response.items
    }
}

// TODO: should these go here or somewhere else?
@Serializable
data class AlbumListResponse(
    val items: List<SpotifyAlbum>,
    val next: String?
)

@Serializable
data class TrackListResponse(
    val items: List<SpotifyTrack>,
    val next: String?
)

//@Serializable
//data class TokenResponse(
//    @SerialName("access_token") val accessToken: String,
//    @SerialName("token_type") val tokenType: String,
//    @SerialName("expires_in") val expiresIn: Int
//)
