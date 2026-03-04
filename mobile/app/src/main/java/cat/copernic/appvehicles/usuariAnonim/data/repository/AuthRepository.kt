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
                val response = api.register(request)

                if (response.isSuccessful) {
                    Result.success(true)
                } else {

                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        errorBody
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
}