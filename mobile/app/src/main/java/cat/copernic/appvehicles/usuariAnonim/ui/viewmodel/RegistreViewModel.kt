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
import androidx.lifecycle.viewModelScope
import cat.copernic.appvehicles.core.composables.uriToFile
import com.google.gson.Gson // Si no tienes Gson, puedes usar la librería JSON que utilices
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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
                // 1. El DTO original (Lo convertimos a JSON)
                val request = ClientRegisterRequest(
                    email = currentState.email,
                    password = currentState.password,
                    nomComplet = currentState.nomComplet,
                    dni = currentState.numeroIdentificacio,
                    dataCaducitatDni = currentState.dataCaducitatId.ifBlank { "2025-01-01" },
                    imatgeDni = "pending_url", // El backend lo ignorará y guardará la URL real
                    nacionalitat = currentState.nacionalitat,
                    adreca = currentState.adreca,
                    tipusCarnetConduir = currentState.tipusLlicencia,
                    dataCaducitatCarnet = currentState.dataCaducitatLlicencia.ifBlank { "2030-01-01" },
                    imatgeCarnet = "pending_url",
                    numeroTargetaCredit = currentState.numeroTargetaCredit
                )

                val jsonRequest = Gson().toJson(request)
                val clientDataPart =
                    jsonRequest.toRequestBody("application/json".toMediaTypeOrNull())

                // 2. Transformar la FOTO DEL DNI de URI a Archivo
                var dniPart: MultipartBody.Part? = null
                if (!currentState.fotoIdentificacioUri.isNullOrBlank()) {
                    val uriDni = Uri.parse(currentState.fotoIdentificacioUri)
                    val fileDni = uriToFile(context, uriDni, "dni_temp.jpg")
                    if (fileDni != null) {
                        val requestFileDni = fileDni.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        dniPart = MultipartBody.Part.createFormData(
                            "fotoIdentificacio",
                            fileDni.name,
                            requestFileDni
                        )
                    }
                }

                // 3. Transformar la FOTO DE LA LLICÈNCIA de URI a Archivo
                var llicenciaPart: MultipartBody.Part? = null
                if (!currentState.fotoLlicenciaUri.isNullOrBlank()) {
                    val uriLlicencia = Uri.parse(currentState.fotoLlicenciaUri)
                    val fileLlicencia = uriToFile(context, uriLlicencia, "llicencia_temp.jpg")
                    if (fileLlicencia != null) {
                        val requestFileLlicencia =
                            fileLlicencia.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        llicenciaPart = MultipartBody.Part.createFormData(
                            "fotoLlicencia",
                            fileLlicencia.name,
                            requestFileLlicencia
                        )
                    }
                }

                // Si por algún motivo falló la carga de las fotos, detenemos el proceso
                if (dniPart == null || llicenciaPart == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error en llegir les imatges. Torna a seleccionar-les."
                        )
                    }
                    return@launch
                }

                // 4. LLAMADA AL REPOSITORIO (Pasándole las 3 partes empaquetadas)
                val result = repository.register(clientDataPart, dniPart, llicenciaPart)

                // 5. GESTIÓN DE LA RESPUESTA
                if (result.isSuccess) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error desconegut"

                    // Comprobación rápida para el famoso error 409 (Email duplicado)
                    if (errorMsg.contains("409")) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Aquest email o DNI ja estan registrats."
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error de connexió: ${e.message}"
                    )
                }
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