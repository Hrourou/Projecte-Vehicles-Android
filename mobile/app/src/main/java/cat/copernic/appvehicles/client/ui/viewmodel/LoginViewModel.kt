package cat.copernic.appvehicles.client.ui.viewmodel // Ajusta el paquete si lo mueves

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.appvehicles.usuariAnonim.data.repository.AuthRepository // Ajusta el import
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository // Inyectamos el repositorio aquí
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChanged(newValue: String) {
        _uiState.update { it.copy(email = newValue) }
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
        // 1️⃣ Limpiar errores anteriores
        _uiState.update { it.copy(emailError = null, passwordError = null, generalError = null) }

        val state = _uiState.value
        val llistaErrors = mutableListOf<String>()

        // Validación de email
        if (state.email.isBlank()) {
            llistaErrors.add("email_required")
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            llistaErrors.add("email_invalid")
        }

        // Validación de password
        if (state.password.isBlank()) {
            llistaErrors.add("password_required")
        }

        // Asignar errores si hay
        if (llistaErrors.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    emailError = if (llistaErrors.contains("email_required") || llistaErrors.contains("email_invalid")) llistaErrors.first { it.startsWith("email") } else null,
                    passwordError = if (llistaErrors.contains("password_required")) "password_required" else null
                )
            }
            return
        }

        // Si todo está bien, lanzar login
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = authRepository.login(state.email, state.password)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isLoading = false, isLoggedIn = true, generalError = null)
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, isLoggedIn = false, generalError = exception.message)
                    }
                }
            )
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