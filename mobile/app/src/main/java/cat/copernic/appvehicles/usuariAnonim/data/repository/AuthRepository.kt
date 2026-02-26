package cat.copernic.appvehicles.usuariAnonim.data.repository

import cat.copernic.appvehicles.usuariAnonim.data.api.remote.AuthApiService
import cat.copernic.appvehicles.model.ClientRegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val api: AuthApiService) {

    /**
     * Realiza la llamada de registro al backend.
     * Retorna un Result<Boolean> que encapsula éxito o fracaso.
     */
    suspend fun register(request: ClientRegisterRequest): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // Llamada síncrona a la API (bloqueante dentro de la coroutine IO)
                val response = api.register(request)

                if (response.isSuccessful) {
                    // HTTP 200-299
                    Result.success(true)
                } else {
                    // HTTP 400-599 (Ej: 409 Conflict por email duplicado)
                    // Intentamos leer el mensaje de error del cuerpo
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        errorBody // Aquí podrías parsear el JSON de error si el backend devuelve {"error": "..."}
                    } else {
                        "Error en el registre: Codi ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                // Errores de red (sin internet, timeout, etc.)
                Result.failure(e)
            }
        }
    }
}