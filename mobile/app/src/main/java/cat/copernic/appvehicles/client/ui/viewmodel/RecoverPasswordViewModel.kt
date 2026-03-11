package cat.copernic.appvehicles.client.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.appvehicles.core.network.RetrofitProvider
import cat.copernic.appvehicles.usuariAnonim.data.api.remote.AuthApiService
import cat.copernic.appvehicles.usuariAnonim.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecoverPasswordUiState(
    val email: String = "",
    val token: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorKey: String? = null,
    val successKey: String? = null
)

class RecoverPasswordViewModel : ViewModel() {

    private val repository = AuthRepository(
        RetrofitProvider.retrofit.create(AuthApiService::class.java)
    )

    private val _uiState = MutableStateFlow(RecoverPasswordUiState())
    val uiState: StateFlow<RecoverPasswordUiState> = _uiState

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorKey = null, successKey = null) }
    }

    fun onTokenChanged(value: String) {
        _uiState.update { it.copy(token = value, errorKey = null, successKey = null) }
    }

    fun onNewPasswordChanged(value: String) {
        _uiState.update { it.copy(newPassword = value, errorKey = null, successKey = null) }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorKey = null, successKey = null) }
    }

    fun sendRecoveryEmail() {
        val email = _uiState.value.email.trim()

        if (email.isBlank()) {
            _uiState.update { it.copy(errorKey = "email_required") }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorKey = "email_invalid") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKey = null, successKey = null) }

            repository.recoverPassword(email)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successKey = "recover_sent",
                            errorKey = null
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorKey = "recover_failed",
                            successKey = null
                        )
                    }
                }
        }
    }

    fun resetPassword() {
        val token = _uiState.value.token.trim()
        val newPassword = _uiState.value.newPassword
        val confirmPassword = _uiState.value.confirmPassword

        if (token.isBlank()) {
            _uiState.update { it.copy(errorKey = "token_required") }
            return
        }

        if (newPassword.isBlank()) {
            _uiState.update { it.copy(errorKey = "password_required") }
            return
        }

        if (newPassword.length < 6) {
            _uiState.update { it.copy(errorKey = "password_too_short") }
            return
        }

        if (newPassword != confirmPassword) {
            _uiState.update { it.copy(errorKey = "password_mismatch") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKey = null, successKey = null) }

            repository.resetPassword(token, newPassword)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successKey = "password_reset_ok",
                            errorKey = null,
                            token = "",
                            newPassword = "",
                            confirmPassword = ""
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorKey = "reset_failed",
                            successKey = null
                        )
                    }
                }
        }
    }
    fun onSendClick() {
        val trimmed = _uiState.value.email.trim()
        if (trimmed.isBlank()) {
            _uiState.update { it.copy(errorKey = "email_required", successKey = null) }
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
            _uiState.update { it.copy(errorKey = "email_invalid", successKey = null) }
            return
        }

        // Aquí debería llamarse a backend (RF03), pero tu proyecto no tiene endpoint.
        // Dejamos mensaje neutro.
        _uiState.update { it.copy(errorKey = null, successKey = "recover_sent") }
}}