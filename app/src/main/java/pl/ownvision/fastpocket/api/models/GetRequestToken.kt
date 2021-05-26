package pl.ownvision.fastpocket.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetRequestTokenRequestDto(
    @SerialName("consumer_key")
    val consumerKey: String,
    @SerialName("redirect_uri")
    val redirectUri: String,
)

@Serializable
data class GetRequestTokenResponseDto(
    val code: String
)