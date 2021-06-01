package pl.ownvision.fastpocket.authentication

import pl.ownvision.fastpocket.BuildConfig
import pl.ownvision.fastpocket.api.PocketApi
import pl.ownvision.fastpocket.api.models.AuthorizeRequest
import pl.ownvision.fastpocket.api.models.AuthorizeResponse
import pl.ownvision.fastpocket.api.models.GetRequestTokenRequestDto
import pl.ownvision.fastpocket.api.models.GetRequestTokenResponseDto
import timber.log.Timber
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(private val pocketApi: PocketApi) {
    suspend fun getRequestToken(): GetRequestTokenResponseDto {
        return pocketApi.getRequestToken(
            GetRequestTokenRequestDto(
                consumerKey = BuildConfig.CONSUMER_KEY,
                redirectUri = BuildConfig.REDIRECT_URL
            )
        )
    }

    suspend fun authorize(code: String): AuthorizeResponse {
        return pocketApi.authorize(
            AuthorizeRequest(
                consumerKey = BuildConfig.CONSUMER_KEY,
                code = code
            )
        )
    }
}