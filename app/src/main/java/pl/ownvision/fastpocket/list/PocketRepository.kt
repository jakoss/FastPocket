package pl.ownvision.fastpocket.list

import pl.ownvision.fastpocket.BuildConfig
import pl.ownvision.fastpocket.api.PocketApi
import pl.ownvision.fastpocket.api.models.PocketItemDto
import pl.ownvision.fastpocket.api.models.PocketItemsRequestDto
import pl.ownvision.fastpocket.infrastructure.settings.AuthorizationSettings
import javax.inject.Inject

class PocketRepository @Inject constructor(
    private val pocketApi: PocketApi,
    private val authorizationSettings: AuthorizationSettings
) {
    suspend fun getPocketItems(): List<PocketItemDto> {
        return pocketApi.getPocketItems(
            PocketItemsRequestDto(
                BuildConfig.CONSUMER_KEY,
                requireNotNull(authorizationSettings.accessToken.read()) {
                    "Access token not provided"
                }
            )
        ).list.values.toList()
    }
}