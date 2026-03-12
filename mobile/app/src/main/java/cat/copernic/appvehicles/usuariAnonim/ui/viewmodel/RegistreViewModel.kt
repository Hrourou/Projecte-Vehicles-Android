package cat.copernic.appvehicles.usuariAnonim.ui.viewmodel

import android.content.Context
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
import android.net.Uri
import android.util.Base64
import java.io.InputStream


import cat.copernic.appvehicles.core.composables.uriToFile
import com.google.gson.Gson


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
    fun register(context: Context) {
        val currentState = _uiState.value

        // --- TUS VALIDACIONES ORIGINALES ---
        val regexData = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()

        if (!currentState.dataCaducitatLlicencia.matches(regexData)) {
            _uiState.update { it.copy(errorMessage = "Format de data de llicència incorrecte (YYYY-MM-DD).") }
            return
        }

        try {
            val dataParsed = java.time.LocalDate.parse(currentState.dataCaducitatLlicencia)
            if (dataParsed.isBefore(java.time.LocalDate.now())) {
                _uiState.update { it.copy(errorMessage = "La llicència de conduir no pot estar caducada.") }
                return
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "Data de llicència invàlida.") }
            return
        }

        if (currentState.nomComplet.isBlank() || currentState.email.isBlank() || currentState.password.isBlank() || currentState.numeroIdentificacio.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Falten camps obligatoris") }
            return
        }

        // --- INICIO DE LA PREPARACIÓN PARA ENVIAR AL BACKEND ---

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // 1. Convertir las imágenes (URIs) a Strings en Base64
                val imatgeDniBase64 = uriToBase64(context, currentState.fotoIdentificacioUri)
                val imatgeCarnetBase64 = uriToBase64(context, currentState.fotoLlicenciaUri)
                val imatgePerfilBase64 = uriToBase64(context, currentState.fotoPerfilUri)

                if (imatgeDniBase64.isBlank() || imatgeCarnetBase64.isBlank()) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error en llegir les imatges. Torna a seleccionar-les.")
                    }
                    return@launch
                }

                // 2. Crear el DTO exacto que espera el Repositorio
                val request = ClientRegisterRequest(
                    email = currentState.email,
                    password = currentState.password,
                    nomComplet = currentState.nomComplet,
                    dni = currentState.numeroIdentificacio,
                    dataCaducitatDni = currentState.dataCaducitatId.ifBlank { "2025-01-01" },
                    imatgeDni = imatgeDniBase64, // ¡Aquí pasamos el Base64 directamente!
                    nacionalitat = currentState.nacionalitat,
                    fotoPerfil = imatgePerfilBase64,
                    adreca = currentState.adreca,
                    tipusCarnetConduir = currentState.tipusLlicencia,
                    dataCaducitatCarnet = currentState.dataCaducitatLlicencia.ifBlank { "2030-01-01" },
                    imatgeCarnet = imatgeCarnetBase64, // ¡Y aquí también!
                    numeroTargetaCredit = currentState.numeroTargetaCredit
                )

                // 3. LLAMADA AL REPOSITORIO (Limpio y directo)
                val result = repository.register(request)

                // 4. GESTIÓN DE LA RESPUESTA
                if (result.isSuccess) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error desconegut"

                    if (errorMsg.contains("409")) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "Aquest email o DNI ja estan registrats.")
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Error de connexió: ${e.message}")
                }
            }
        }
    }
}

private fun uriToBase64(context: Context, uriString: String?): String {
    if (uriString.isNullOrBlank()) return ""
    return try {
        val uri = Uri.parse(uriString)
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()

        if (bytes != null) {
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
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