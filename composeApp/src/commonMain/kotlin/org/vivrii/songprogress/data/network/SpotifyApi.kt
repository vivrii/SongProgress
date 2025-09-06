package org.vivrii.songprogress.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.encodeBase64
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.vivrii.songprogress.BuildConfig
import org.vivrii.songprogress.domain.models.SpotifyAlbum
import org.vivrii.songprogress.domain.models.SpotifyArtist
import org.vivrii.songprogress.domain.models.SpotifyTrack
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class SpotifyApi() {
    private var token: String? = null
    private var tokenExpiry: Instant? = null
    private val baseUrl = "https://api.spotify.com/v1"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // TODO: generic way to handle responses that are NOT ok for reasons
    suspend fun getArtist(artistId: String): SpotifyArtist = authGet("$baseUrl/artists/$artistId")

    suspend fun getArtistAlbums(artistId: String): List<SpotifyAlbum> {
        val response: AlbumListResponse = authGet("$baseUrl/artists/$artistId/albums") {
            parameter("include_groups", "album,single") // there is also appears_on and compilation
            parameter("limit", 50)
        }
        return response.items
    }

    suspend fun getAlbumTracks(albumId: String): List<SpotifyTrack> {
        val response: TrackListResponse = authGet("$baseUrl/albums/$albumId/tracks") {
            parameter("limit", 50)
        }
        return response.items
    }

    internal suspend inline fun <reified T> authGet(url: String, block: HttpRequestBuilder.() -> Unit = {}): T {
        ensureCurrentToken()
        return client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $token")
            block()
        }.body()
    }

    private suspend fun getClientToken(clientId: String, clientSecret: String) {
        val auth = "$clientId:$clientSecret".encodeBase64()
        val response: TokenResponse = client.post("https://accounts.spotify.com/api/token") {
            header("Authorization", "Basic $auth")
            header("Content-Type", "application/x-www-form-urlencoded")
            setBody("grant_type=client_credentials")
        }.also {
            val text = it.bodyAsText()
            println(text)
        }.body()

        // store token end expiry TODO: cache these
        token = response.accessToken
        tokenExpiry = Clock.System.now().plus(response.expiresIn.seconds)
    }

    private suspend fun ensureCurrentToken() {
        tokenExpiry.let { expiry ->
            if (token == null || expiry == null || Clock.System.now() >= expiry) {
                println("refreshing token")
                getClientToken(BuildConfig.SPOTIFY_CLIENT_ID, BuildConfig.SPOTIFY_CLIENT_SECRET)
            } else {
                println("using current token")
            }
        }
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

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int
)
