package pl.ownvision.fastpocket.api

import pl.ownvision.fastpocket.api.models.*
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PocketApi {
    @POST("/v3/oauth/request")
    @Headers("X-Accept: application/json")
    suspend fun getRequestToken(@Body request: GetRequestTokenRequestDto): GetRequestTokenResponseDto

    @POST("/v3/oauth/authorize")
    @Headers("X-Accept: application/json")
    suspend fun authorize(@Body request: AuthorizeRequest): AuthorizeResponse

    @POST("/v3/get")
    @Headers("X-Accept: application/json")
    suspend fun getPocketItems(@Body request: PocketItemsRequestDto): PocketItemsResponseDto
}