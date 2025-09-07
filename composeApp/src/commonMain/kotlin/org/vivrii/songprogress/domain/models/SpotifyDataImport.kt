package org.vivrii.songprogress.domain.models

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class SpotifyDataImport(
    val start: Instant? = null,
    val end: Instant? = null,
) {
    // TODO: consider a Map instead of HashMap after import is done. verify performance of converting from HashMap to a map, this change is about immutability.
    val artists = HashMap<String, Artist>()
}
