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
    val since: Long?,
    val detailType: String,
    // TODO : add rest of the fields
)

@Serializable
data class PocketItemsResponseDto(
    val list: Map<String, PocketItemWrapperDto>,
    val since: Long,
    // TODO : add rest of the fields
)

@Serializable
sealed class PocketItemWrapperDto

@Serializable
@SerialName("0")
data class PocketItemDtoDto(
    @SerialName("item_id")
    val itemId: String,
    @SerialName("given_url")
    val givenUrl: String,
    @SerialName("given_title")
    val givenTitle: String,
    @SerialName("resolved_title")
    val resolvedTitle: String,
    @Serializable(with = BooleanAsIntSerializer::class)
    val favorite: Boolean,
    @SerialName("top_image_url")
    val topImageUrl: String? = null,
    val excerpt: String? = null,
    @SerialName("time_to_read")
    val timeToRead: Int? = null,
    val image: ImageDto? = null,
    // TODO : add rest of the fields
) : PocketItemWrapperDto()

@Serializable
@SerialName("1")
data class PocketItemArchiveDto(
    @SerialName("item_id")
    val itemId: String,
)

@Serializable
@SerialName("2")
data class PocketItemDeleteDto(
    @SerialName("item_id")
    val itemId: String,
)

@Serializable
data class ImageDto(
    @SerialName("item_id")
    val itemId: String,
    val src: String,
)

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