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
            // Ponemos la pantalla en modo carga y limpiamos errores anteriores
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            // Llamada REAL a la API a través del Repositorio
            val result = authRepository.login(state.email, state.password)

            result.fold(
                onSuccess = {
                    // Si va bien, el repositorio ya guardó la sesión. Solo actualizamos la UI.
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            generalError = null
                        )
                    }
                },
                onFailure = { exception ->
                    // Si falla (credenciales incorrectas, error de red), mostramos el error
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = false,
                            generalError = exception.message
                        )
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