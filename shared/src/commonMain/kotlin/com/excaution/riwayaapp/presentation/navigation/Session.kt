package com.excaution.riwayaapp.presentation.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Single source of truth for whether the user is authenticated.
 *
 * RootNavGraph observes this and switches the whole app between the
 * Auth graph and the Main graph. Nothing else should navigate between
 * those two graphs directly — screens just call Session.login() /
 * Session.logout() and the root reacts.
 *
 * Swap the body of this object for your real token/repository-backed
 * auth manager (e.g. backed by DataStore/Keychain + your backend).
 */
object Session {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun login() {
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }
}
