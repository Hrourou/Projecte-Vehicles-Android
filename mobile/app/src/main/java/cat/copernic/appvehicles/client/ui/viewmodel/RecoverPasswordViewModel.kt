package cat.copernic.appvehicles.client.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class RecoverPasswordUiState(
    val email: String = "",
    val errorKey: String? = null,
    val successKey: String? = null
)

class RecoverPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RecoverPasswordUiState())
    val uiState: StateFlow<RecoverPasswordUiState> = _uiState

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorKey = null, successKey = null) }
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
    }
}