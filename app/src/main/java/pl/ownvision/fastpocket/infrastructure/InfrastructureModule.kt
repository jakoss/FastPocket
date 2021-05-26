package pl.ownvision.fastpocket.infrastructure

import android.app.Application
import android.content.Context
import com.chimerapps.niddler.core.AndroidNiddler
import com.chimerapps.niddler.interceptor.okhttp.NiddlerOkHttpInterceptor
import com.chimerapps.niddler.retrofit.NiddlerRetrofitCallInjector
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.brotli.BrotliInterceptor
import pl.ownvision.fastpocket.api.PocketApi
import pl.ownvision.fastpocket.infrastructure.networking.RetryInterceptor
import retrofit2.Retrofit
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InfrastructureModule {

    @Provides
    @Singleton
    fun provideNiddler(@ApplicationContext applicationContext: Context): AndroidNiddler =
        AndroidNiddler.Builder()
            .setPort(0)
            .setNiddlerInformation(AndroidNiddler.fromApplication(applicationContext as Application))
            .setMaxStackTraceSize(50)
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(niddler: AndroidNiddler) = OkHttpClient.Builder()
        .connectTimeout(Duration.ofSeconds(15))
        .readTimeout(Duration.ofSeconds(15))
        .writeTimeout(Duration.ofSeconds(30))
        .addInterceptor(
            NiddlerOkHttpInterceptor(
                niddler,
                "Niddler",
                true
            )
        )
        .addInterceptor(BrotliInterceptor)
        .addInterceptor(RetryInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideSerializationFormat() = Json {
        ignoreUnknownKeys = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun providePocketApi(
        okHttpClient: OkHttpClient,
        format: Json,
        niddler: AndroidNiddler
    ): PocketApi {
        val retrofitBuilder = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(format.asConverterFactory("application/json".toMediaType()))
            .baseUrl("https://getpocket.com/")

        NiddlerRetrofitCallInjector.inject(
            retrofitBuilder,
            niddler,
            okHttpClient
        )
        return retrofitBuilder
            .build()
            .create(PocketApi::class.java)
    }
}