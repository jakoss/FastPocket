package pl.ownvision.fastpocket.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModifyPocketItemsRequest(
    @SerialName("consumer_key")
    val consumerKey: String,
    @SerialName("access_token")
    val accessToken: String,
    val actions: List<ModifyAction>,
)

@Serializable
data class ModifyAction(
    val action: String,
    @SerialName("item_id")
    val itemId: String,
    // TODO : add optional fields
)