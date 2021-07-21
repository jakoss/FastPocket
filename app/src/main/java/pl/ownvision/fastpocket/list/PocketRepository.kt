package pl.ownvision.fastpocket.list

import pl.ownvision.fastpocket.BuildConfig
import pl.ownvision.fastpocket.api.PocketApi
import pl.ownvision.fastpocket.api.models.ModifyAction
import pl.ownvision.fastpocket.api.models.ModifyPocketItemsRequest
import pl.ownvision.fastpocket.api.models.PocketItemDtoDto
import pl.ownvision.fastpocket.api.models.PocketItemsRequestDto
import pl.ownvision.fastpocket.infrastructure.settings.AuthorizationSettings
import javax.inject.Inject

class PocketRepository @Inject constructor(
    private val pocketApi: PocketApi,
    private val authorizationSettings: AuthorizationSettings
) {
    suspend fun getPocketItems(): List<PocketItemDtoDto> {
        return pocketApi.getPocketItems(
            PocketItemsRequestDto(
                consumerKey = BuildConfig.CONSUMER_KEY,
                accessToken = requireNotNull(authorizationSettings.accessToken.read()) {
                    "Access token not provided"
                },
                since = null, // TODO : set that to get only changes in data
                detailType = "complete",
            )
        ).list.values.filterIsInstance<PocketItemDtoDto>().toList()
    }

    suspend fun archivePocketItem(itemId: String) {
        performModifyAction(ModifyAction("archive", itemId = itemId))
    }

    suspend fun markItemAsFavorite(itemId: String) {
        performModifyAction(ModifyAction("favorite", itemId = itemId))
    }

    suspend fun markItemAsNotFavorite(itemId: String) {
        performModifyAction(ModifyAction("unfavorite", itemId = itemId))
    }

    private suspend fun performModifyAction(vararg actions: ModifyAction) {
        pocketApi.modifyPocketItems(
            ModifyPocketItemsRequest(
                BuildConfig.CONSUMER_KEY,
                requireNotNull(authorizationSettings.accessToken.read()) {
                    "Access token not provided"
                },
                listOf(*actions)
            )
        )
    }
}