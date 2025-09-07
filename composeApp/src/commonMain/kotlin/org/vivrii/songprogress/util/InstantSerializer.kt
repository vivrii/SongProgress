package org.vivrii.songprogress.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@ExperimentalTime
object InstantSerializer : KSerializer<Instant> {
    @OptIn(FormatStringsInDatetimeFormats::class)
    private val altFormatter = LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd HH:mm") }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString()) // ISO by default
    }

    override fun deserialize(decoder: Decoder): Instant {
        val str = decoder.decodeString()
        return try {
            Instant.parse(str) // first try ISO
        } catch (_: Exception) {
            altFormatter.parse(str).toInstant(UtcOffset.ZERO)
        }
    }
}
