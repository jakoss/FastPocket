package pl.ownvision.fastpocket.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorizeRequest(
    @SerialName("consumer_key")
    val consumerKey: String,
    val code: String,
)

@Serializable
data class AuthorizeResponse(
    @SerialName("access_token")
    val accessToken: String,
    val username: String,
)