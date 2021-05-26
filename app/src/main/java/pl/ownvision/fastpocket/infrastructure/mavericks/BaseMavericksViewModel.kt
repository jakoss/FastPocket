package pl.ownvision.fastpocket.infrastructure.mavericks

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel

abstract class BaseMavericksViewModel<S : MavericksState>(
    initialState: S,
) :
    MavericksViewModel<S>(initialState)
