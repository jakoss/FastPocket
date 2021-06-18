package pl.ownvision.fastpocket.list

import com.airbnb.mvrx.*
import pl.ownvision.fastpocket.infrastructure.mavericks.AssistedViewModelFactory
import pl.ownvision.fastpocket.infrastructure.mavericks.BaseMavericksViewModel
import pl.ownvision.fastpocket.infrastructure.mavericks.MavericksViewModelComponent
import pl.ownvision.fastpocket.infrastructure.mavericks.ViewModelKey
import pl.ownvision.fastpocket.infrastructure.mavericks.hiltMavericksViewModelFactory
import com.paulrybitskyi.hiltbinder.BindType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.ownvision.fastpocket.api.models.PocketItemDto
import pl.ownvision.fastpocket.infrastructure.settings.AuthorizationSettings
import timber.log.Timber

data class PocketItemsListState(
    val items: Async<List<PocketItemDto>> = Uninitialized,
    val accessToken: Async<String?> = Uninitialized,
) : MavericksState {
    val userAuthorized: Boolean
        get() = accessToken() != null
}

class PocketItemsListViewModel @AssistedInject constructor(
    @Assisted state: PocketItemsListState,
    private val pocketRepository: PocketRepository,
    authorizationSettings: AuthorizationSettings,
) :
    BaseMavericksViewModel<PocketItemsListState>(state) {

    init {
        authorizationSettings.accessToken.listen()
            .onEach {
                val currentAccessToken = awaitState().accessToken
                if (it == currentAccessToken()) {
                    setState { copy(accessToken = Success(it)) }
                    return@onEach
                }

                if (it != null) {
                    loadPocketItems()
                } else {
                    setState { copy(items = Uninitialized) }
                }
                setState { copy(accessToken = Success(it)) }
            }
            .catch {
                setState { copy(accessToken = Fail(it)) }
            }
            .launchIn(viewModelScope)
    }

    fun archivePocketItem(pocketItemDto: PocketItemDto) {
        viewModelScope.launch {
            try {
                val currentItems = awaitState().items() ?: return@launch
                setState { copy(items = Success(currentItems.filterNot { it.itemId == pocketItemDto.itemId })) }
                pocketRepository.archivePocketItem(pocketItemDto)
                loadPocketItems()
                // TODO : display success status
                // TODO : animate item dissmissal (is that possible?)
            } catch (ex: Throwable) {
                // TODO : handle error properly
                Timber.e(ex)
            }
        }
    }

    fun loadPocketItems() {
        suspend {
            pocketRepository.getPocketItems()
        }.execute(retainValue = PocketItemsListState::items) { copy(items = it) }
    }

    @BindType(
        installIn = BindType.Component.CUSTOM,
        customComponent = MavericksViewModelComponent::class,
        to = AssistedViewModelFactory::class,
        contributesTo = BindType.Collection.MAP
    )
    @ViewModelKey(PocketItemsListViewModel::class)
    @AssistedFactory
    interface Factory : AssistedViewModelFactory<PocketItemsListViewModel, PocketItemsListState> {
        override fun create(state: PocketItemsListState): PocketItemsListViewModel
    }

    companion object :
        MavericksViewModelFactory<PocketItemsListViewModel, PocketItemsListState> by hiltMavericksViewModelFactory()
}
