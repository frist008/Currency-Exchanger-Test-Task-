package ua.testtask.currencyexchanger.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import ua.testtask.currencyexchanger.ui.entity.base.UIState
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, cause ->
        runCatching { onFailure(cause) }
            .onSuccess { Timber.i(cause) }
            .onFailure { innerCause -> Timber.e(cause, innerCause.stackTraceToString()) }
    }

    protected val scope by lazy(LazyThreadSafetyMode.NONE) { viewModelScope }

    protected val alertDialogMutableState = MutableStateFlow<UIState>(UIState.Progress())
    val alertDialogState by lazy(LazyThreadSafetyMode.NONE) { alertDialogMutableState.asStateFlow() }
    protected val mutableState = MutableStateFlow<UIState>(UIState.Progress())
    val state by lazy(LazyThreadSafetyMode.NONE) { mutableState.asStateFlow() }

    protected fun launch(
        context: CoroutineContext = Dispatchers.Default,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit,
    ): Job =
        scope.launch(context + coroutineExceptionHandler, start, block)

    protected abstract fun onFailure(cause: Throwable)

    fun onDismissDialog() {
        alertDialogMutableState.tryEmit(UIState.Progress())
    }
}
