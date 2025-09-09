package com.appdemo.coreui.mvi

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface AppContractEvent<EVENT> {
    fun event(event: EVENT)
}
interface AppContract<STATE, EFFECT, EVENT> : AppContractEvent<EVENT> {
    val state: StateFlow<STATE>
    val effect: SharedFlow<EFFECT>
}
