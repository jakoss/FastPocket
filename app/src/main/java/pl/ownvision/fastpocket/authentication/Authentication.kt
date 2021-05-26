package pl.ownvision.fastpocket.authentication

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.paulrybitskyi.hiltbinder.BindType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import pl.ownvision.fastpocket.infrastructure.mavericks.*
import pl.ownvision.fastpocket.infrastructure.settings.AuthorizationSettings

data class AuthenticationState(
    val requestCode: Async<String> = Uninitialized,
    val authorizationState: Async<String> = Uninitialized,
) : MavericksState

class AuthenticationViewModel @AssistedInject constructor(
    @Assisted state: AuthenticationState,
    private val authenticationRepository: AuthenticationRepository,
    private val authorizationSettings: AuthorizationSettings,
) : BaseMavericksViewModel<AuthenticationState>(state) {

    init {
        suspend {
            authenticationRepository.getRequestToken().code
        }.execute { copy(requestCode = it) }
    }

    fun authorize() {
        suspend {
            val code = requireNotNull(awaitState().requestCode()) { "Missing request code" }
            val response = authenticationRepository.authorize(code)

            // let's save the user data. This basically marks user as logged in
            authorizationSettings.accessToken.write(response.accessToken)
            authorizationSettings.username.write(response.username)
            response.username
        }.execute { copy(authorizationState = it) }
    }

    @BindType(
        installIn = BindType.Component.CUSTOM,
        customComponent = MavericksViewModelComponent::class,
        to = AssistedViewModelFactory::class,
        contributesTo = BindType.Collection.MAP
    )
    @ViewModelKey(AuthenticationViewModel::class)
    @AssistedFactory
    interface Factory : AssistedViewModelFactory<AuthenticationViewModel, AuthenticationState> {
        override fun create(state: AuthenticationState): AuthenticationViewModel
    }

    companion object :
        MavericksViewModelFactory<AuthenticationViewModel, AuthenticationState> by hiltMavericksViewModelFactory()
}
