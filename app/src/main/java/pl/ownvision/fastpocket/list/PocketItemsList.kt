package pl.ownvision.fastpocket.list

import com.airbnb.mvrx.*
import com.paulrybitskyi.hiltbinder.BindType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.ownvision.fastpocket.api.models.PocketItemDtoDto
import pl.ownvision.fastpocket.infrastructure.mavericks.*
import pl.ownvision.fastpocket.infrastructure.settings.ApplicationSettings
import pl.ownvision.fastpocket.infrastructure.settings.AuthorizationSettings
import timber.log.Timber

data class PocketItemsListState(
    val items: Async<List<PocketItemDtoDto>> = Uninitialized,
    val accessToken: Async<String?> = Uninitialized,
    val useExternalBrowser: Boolean = false,
    val manualRefresh: Boolean = false,
) : MavericksState {
    val userAuthorized: Boolean
        get() = accessToken() != null
}

class PocketItemsListViewModel @AssistedInject constructor(
    @Assisted state: PocketItemsListState,
    private val pocketRepository: PocketRepository,
    authorizationSettings: AuthorizationSettings,
    applicationSettings: ApplicationSettings,
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

        applicationSettings.useExternalBrowser.listen()
            .setOnEach { copy(useExternalBrowser = it) }
    }

    fun archivePocketItem(itemId: String) {
        viewModelScope.launch {
            try {
                val currentItems = awaitState().items() ?: return@launch
                setState { copy(items = Success(currentItems.filterNot { it.itemId == itemId })) }
                pocketRepository.archivePocketItem(itemId)
                loadPocketItems()
                // TODO : display success status
            } catch (ex: Throwable) {
                // TODO : handle error properly
                Timber.e(ex)
            }
        }
    }

    fun swapFavoriteStatus(itemId: String) {
        viewModelScope.launch {
            try {
                val currentItems = awaitState().items() ?: return@launch
                val item = currentItems.first { it.itemId == itemId }
                val newItems = currentItems.map {
                    if (it.itemId == itemId) {
                        it.copy(favorite = !it.favorite)
                    } else {
                        it
                    }
                }
                setState { copy(items = Success(newItems)) }

                if (item.favorite) {
                    pocketRepository.markItemAsNotFavorite(itemId)
                } else {
                    pocketRepository.markItemAsFavorite(itemId)
                }
                loadPocketItems()
                // TODO : display success status
            } catch (ex: Throwable) {
                // TODO : handle error properly
                Timber.e(ex)
            }
        }
    }

    fun loadPocketItems(manualRefresh: Boolean = false) {
        suspend {
            if (manualRefresh) {
                setState { copy(manualRefresh = true) }
            }
            try {
                pocketRepository.getPocketItems()
            } finally {
                if (manualRefresh) {
                    setState { copy(manualRefresh = false) }
                }
            }
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
