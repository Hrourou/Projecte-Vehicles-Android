package cat.copernic.appvehicles.client.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChanged(newValue: String) {
        _uiState.update { current ->
            current.copy(
                email = newValue,
                emailError = validateEmail(newValue),
                generalError = null
            )
        }
    }

    fun onPasswordChanged(newValue: String) {
        _uiState.update { current ->
            current.copy(
                password = newValue,
                passwordError = validatePassword(newValue),
                generalError = null
            )
        }
    }

    fun onLoginClick() {
        val state = _uiState.value

        val emailError = validateEmail(state.email)
        val passError = validatePassword(state.password)

        if (emailError != null || passError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            // Simulación temporal
            delay(900)

            // Mock (cuando conectes API, elimina esto)
            val ok = (state.email == "client@test.com" && state.password == "1234")

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isLoggedIn = ok,
                    generalError = if (!ok) "invalid_credentials" else null
                )
            }
        }
    }

    private fun validateEmail(value: String): String? {
        if (value.isBlank()) return "email_required"
        if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) return "email_invalid"
        return null
    }

    private fun validatePassword(value: String): String? {
        if (value.isBlank()) return "password_required"
        return null
    }
}