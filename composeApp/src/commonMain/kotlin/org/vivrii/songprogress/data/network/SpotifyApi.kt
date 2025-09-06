@file:OptIn(ExperimentalTime::class)

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
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.vivrii.songprogress.BuildConfig
import org.vivrii.songprogress.domain.models.SpotifyAlbum
import org.vivrii.songprogress.domain.models.SpotifyArtist
import org.vivrii.songprogress.domain.models.SpotifyTrack
import org.vivrii.songprogress.util.KmpFile
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SpotifyApi() {
    private var token: Token? = null
    private val tokenFile = KmpFile.cacheDir() / "spotify" / "token"
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

    internal suspend inline fun <reified T> authGet(
        url: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): T {
        checkRefreshToken()

        // TODO: a not duplication-y way of retrying once...
        // TODO: check the reason was actually due to invalid access token
        return try {
            client.get(url) {
                header(HttpHeaders.Authorization, "Bearer ${token!!.secret}")
                block()
            }.also { response ->
                println(response.bodyAsText())
            }.body()

        } catch (e: Exception) {
            println("exception occurred: ${e.message}")
            checkRefreshToken(force = true)

            client.get(url) {
                header(HttpHeaders.Authorization, "Bearer ${token!!.secret}")
                block()
            }.also { response ->
                println(response.bodyAsText())
            }.body()
        }

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

        // store token + expiry and cache them
        token = Token(response.accessToken, Clock.System.now().plus(response.expiresIn.seconds))
        token?.let { cacheToken(it) }
    }

    private suspend fun checkRefreshToken(force: Boolean = false) {
        if (force) {
            println("forcing token refresh")
            token = null
        } else if (token == null) {
            loadTokenFromCache()
        }

        token?.let { token ->
            if (Clock.System.now() >= token.expiry) {
                println("refreshing token")
            } else {
                println("using current token")
                return
            }
        }

        getClientToken(BuildConfig.SPOTIFY_CLIENT_ID, BuildConfig.SPOTIFY_CLIENT_SECRET)
    }

    private fun loadTokenFromCache() {
        if (tokenFile.exists()) {
            token = Json.decodeFromString(tokenFile.readText())
            println("loaded cached token: ${token?.secret}, ${token?.expiry}. from $tokenFile")
        } else {
            println("no token in: $tokenFile")
        }
    }

    private fun cacheToken(token: Token) {
        if (!tokenFile.exists()) {
            tokenFile.create()
        }
        println("caching token")
        tokenFile.writeText(Json.encodeToString(token))
    }
}

// TODO: should these go here or somewhere else?
@Serializable
data class AlbumListResponse(
    val items: List<SpotifyAlbum>,
    val next: String?,
)

@Serializable
data class TrackListResponse(
    val items: List<SpotifyTrack>,
    val next: String?,
)

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
)

@Serializable
@OptIn(InternalSerializationApi::class)
@Suppress("SERIALIZER_NOT_FOUND")
data class Token(
    val secret: String,
    val expiry: Instant,
)
