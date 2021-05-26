package pl.ownvision.fastpocket.infrastructure.networking

import com.github.michaelbull.retry.policy.constantDelay
import com.github.michaelbull.retry.policy.limitAttempts
import com.github.michaelbull.retry.policy.plus
import com.github.michaelbull.retry.policy.retryIf
import com.github.michaelbull.retry.retry
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Retries all failed requests with exponential backoff
 */
object RetryInterceptor : Interceptor {
    private val exceptionFilter = retryIf<Throwable> {
        reason is IOException
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null

        return runBlocking {
            retry(
                exceptionFilter +
                        limitAttempts(1) + constantDelay(1000L)
            ) {
                // close any possible previous responses (fixes a leak exception)
                response?.close()
                @Suppress("BlockingMethodInNonBlockingContext")
                val localResponse = chain.proceed(request)
                response = localResponse
                localResponse
            }
        }
    }
}