package cat.copernic.appvehicles.usuariAnonim.data.repository

import cat.copernic.appvehicles.core.auth.SessionManager
import cat.copernic.appvehicles.model.LoginRequest
import cat.copernic.appvehicles.usuariAnonim.data.api.remote.AuthApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import cat.copernic.appvehicles.model.ClientRegisterRequest
import org.json.JSONObject

class AuthRepository(
    private val api: AuthApiService,
    private val sessionManager: SessionManager // <-- Añadimos el SessionManager aquí
) {

    /**
     * Realiza la llamada de registro al backend enviando textos e imágenes.
     * Retorna un Result<Boolean> que encapsula éxito o fracaso.
     */
    /**
     * Realiza la llamada de registro al backend enviando el JSON con las imágenes en Base64.
     * Retorna un Result<Boolean> que encapsula éxito o fracaso.
     */
    suspend fun register(request: ClientRegisterRequest): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // Pasamos directamente el objeto request a la API
                val response = api.register(request)

                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        try {
                            JSONObject(errorBody).getString("error")
                        } catch (e: Exception) {
                            errorBody
                        }
                    } else {
                        "Error en el registre: Codi ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Realiza la llamada de login al backend.
     * Si es exitoso, guarda la sesión localmente (sin la contraseña).
     */
    suspend fun login(email: String, contrasenya: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // Preparamos el DTO para el backend
                val request = LoginRequest(email, contrasenya)

                // Llamamos a la API
                val response = api.login(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // ¡ÉXITO! Guardamos la sesión en el DataStore local
                        sessionManager.saveSession(
                            email = body.email,
                            name = body.nomComplet,
                            token = body.token
                        )
                        Result.success(true)
                    } else {
                        Result.failure(Exception("Resposta buida del servidor"))
                    }
                } else {
                    // Capturamos el error 401 u otros
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        try {
                            // Intentamos extraer el mensaje del JSON del backend
                            JSONObject(errorBody).getString("error")
                        } catch (e: Exception) {
                            "Error en iniciar sessió"
                        }
                    } else {
                        "Error en iniciar sessió: Codi ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                // Errores de red, timeout, etc.
                Result.failure(Exception("Error de connexió: ${e.message}"))
            }
        }
    }

}