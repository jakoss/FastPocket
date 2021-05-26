#if (${PACKAGE_NAME} != "")package ${PACKAGE_NAME}#end

import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.MavericksState
import pl.ownvision.fastpocket.infrastructure.mavericks.AssistedViewModelFactory
import pl.ownvision.fastpocket.infrastructure.mavericks.BaseMavericksViewModel
import pl.ownvision.fastpocket.infrastructure.mavericks.MavericksViewModelComponent
import pl.ownvision.fastpocket.infrastructure.mavericks.ViewModelKey
import pl.ownvision.fastpocket.infrastructure.mavericks.hiltMavericksViewModelFactory
import com.paulrybitskyi.hiltbinder.BindType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

data class ${NAME}State(
    val initialValue: String,
) : MavericksState

class ${NAME}ViewModel @AssistedInject constructor(@Assisted state: ${NAME}State) :
    BaseMavericksViewModel<${NAME}State>(state) {
    
     @BindType(
        installIn = BindType.Component.CUSTOM,
        customComponent = MavericksViewModelComponent::class,
        to = AssistedViewModelFactory::class,
        contributesTo = BindType.Collection.MAP
    )
    @ViewModelKey(${NAME}ViewModel::class)
    @AssistedFactory
    interface Factory : AssistedViewModelFactory<${NAME}ViewModel, ${NAME}State> {
        override fun create(state: ${NAME}State): ${NAME}ViewModel
    }

    companion object :
        MavericksViewModelFactory<${NAME}ViewModel, ${NAME}State> by hiltMavericksViewModelFactory()
}
