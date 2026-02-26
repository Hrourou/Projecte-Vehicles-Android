package cat.copernic.appvehicles.usuariAnonim.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cat.copernic.appvehicles.model.ClientRegisterRequest
import cat.copernic.appvehicles.usuariAnonim.data.repository.AuthRepository
import cat.copernic.appvehicles.usuariAnonim.ui.view.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    // RN22: Gestión del estado con MutableStateFlow
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * Actualiza el estado cuando el usuario escribe en los campos.
     */
    fun updateState(newState: RegisterUiState) {
        _uiState.update { newState }
    }

    /**
     * Lanza el proceso de registro.
     * Se ejecuta en una Coroutine (RN27) para no bloquear la UI.
     */
    fun register() {
        val currentState = _uiState.value

        // 1. Validación básica (RN26 Usabilidad)
        if (currentState.email.isBlank() || currentState.password.isBlank() || currentState.numeroIdentificacio.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Falten camps obligatoris") }
            return
        }

        // 2. Preparar datos para el backend
        // Mapeamos del Estado de UI -> Request de API
        // Nota: Para las imágenes enviamos strings vacíos por ahora hasta implementar subida de ficheros.
        val request = ClientRegisterRequest(
            email = currentState.email,
            password = currentState.password,
            nomComplet = currentState.nomComplet,
            dni = currentState.numeroIdentificacio, // Mapeo UI -> Backend
            dataCaducitatDni = currentState.dataCaducitatId.ifBlank { "2025-01-01" }, // Valor por defecto o real
            imatgeDni = "pending_url",
            nacionalitat = currentState.nacionalitat,
            adreca = currentState.adreca,
            tipusCarnetConduir = currentState.tipusLlicencia,
            dataCaducitatCarnet = currentState.dataCaducitatLlicencia.ifBlank { "2030-01-01" },
            imatgeCarnet = "pending_url",
            numeroTargetaCredit = currentState.numeroTargetaCredit
        )

        // 3. Llamada asíncrona
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.register(request)

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Error desconegut"
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
            }
        }
    }
}

/**
 * Factory para poder pasar el Repositorio al ViewModel (necesario si no usas Hilt/Dagger).
 */
class RegisterViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}