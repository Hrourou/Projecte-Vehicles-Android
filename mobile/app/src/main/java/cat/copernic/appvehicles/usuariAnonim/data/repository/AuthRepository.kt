package cat.copernic.appvehicles.usuariAnonim.data.repository

import cat.copernic.appvehicles.usuariAnonim.data.api.remote.AuthApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthRepository(private val api: AuthApiService) {

    /**
     * Realiza la llamada de registro al backend enviando textos e imágenes.
     * Retorna un Result<Boolean> que encapsula éxito o fracaso.
     */
    suspend fun register(
        clientData: RequestBody,
        fotoIdentificacio: MultipartBody.Part,
        fotoLlicencia: MultipartBody.Part
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // Pasamos las tres partes a la API
                val response = api.register(clientData, fotoIdentificacio, fotoLlicencia)

                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        errorBody
                    } else {
                        "Error en el registre: Codi ${response.code()}" // Aquí saltará el famoso "Codi 409" si el email está duplicado
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}