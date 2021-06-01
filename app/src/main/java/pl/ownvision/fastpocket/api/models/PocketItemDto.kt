package pl.ownvision.fastpocket.api.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class PocketItemsRequestDto(
    @SerialName("consumer_key")
    val consumerKey: String,
    @SerialName("access_token")
    val accessToken: String,
    // TODO : add rest of the fields
)

@Serializable
data class PocketItemsResponseDto(
    val list: Map<String, PocketItemDto>,
    // TODO : add rest of the fields
)

@Serializable
data class PocketItemDto(
    @SerialName("item_id")
    val itemId: String,
    @SerialName("resolved_id")
    val resolvedId: String,
    @SerialName("given_url")
    val givenUrl: String,
    @SerialName("resolved_url")
    val resolvedUrl: String,
    @SerialName("given_title")
    val givenTitle: String,
    @SerialName("resolved_title")
    val resolvedTitle: String,
    @Serializable(with = BooleanAsIntSerializer::class)
    val favorite: Boolean,
    @SerialName("top_image_url")
    val topImageUrl: String? = null,
    // TODO : add rest of the fields
)

@Serializable
enum class FavoriteState {
    @SerialName("1")
    Favorited,

    @SerialName("0")
    NotFavorited
}

object BooleanAsIntSerializer : KSerializer<Boolean> {
    override fun deserialize(decoder: Decoder): Boolean {
        val originalValue = decoder.decodeString()
        return originalValue == "1"
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BooleanAsInt", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeString(
            if (value) {
                "1"
            } else {
                "0"
            }
        )
    }

}