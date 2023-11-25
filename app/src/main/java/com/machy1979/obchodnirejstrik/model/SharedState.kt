package com.machy1979.obchodnirejstrik.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SharedState { //vzhledem k návrhu celé aplikace jsem nevymyslel lepší způsob, než vytvořit tuto sdílenou proměnnou pro chceckování
    //zda uživatel kliknul na save to pdf a dalo se s tím v rámci aplikace pracovat
    private val _saveToPdfClicked = MutableStateFlow(false)
    val saveToPdfClicked: StateFlow<Boolean> get() = _saveToPdfClicked

    fun setSaveToPdfClicked(value: Boolean) {
        _saveToPdfClicked.value = value
    }
}