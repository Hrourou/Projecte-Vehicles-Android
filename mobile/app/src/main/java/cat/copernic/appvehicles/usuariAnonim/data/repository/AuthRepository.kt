package cat.copernic.appvehicles.usuariAnonim.data.repository

import cat.copernic.appvehicles.usuariAnonim.data.api.remote.AuthApiService
import cat.copernic.appvehicles.usuariAnonim.data.model.PasswordRecoveryRequest
import cat.copernic.appvehicles.usuariAnonim.data.model.PasswordRecoveryResponse
import cat.copernic.appvehicles.usuariAnonim.data.model.ResetPasswordRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthRepository(private val api: AuthApiService) {

    suspend fun register(
        clientData: RequestBody,
        fotoIdentificacio: MultipartBody.Part,
        fotoLlicencia: MultipartBody.Part
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.register(clientData, fotoIdentificacio, fotoLlicencia)

                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        errorBody
                    } else {
                        "Register failed: HTTP ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun recoverPassword(email: String): Result<PasswordRecoveryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.recoverPassword(PasswordRecoveryRequest(email))

                if (response.isSuccessful) {
                    Result.success(
                        response.body() ?: PasswordRecoveryResponse(
                            code = "recover_sent",
                            message = "If the email exists, you will receive recovery instructions shortly."
                        )
                    )
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Recovery failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun resetPassword(token: String, newPassword: String): Result<PasswordRecoveryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.resetPassword(ResetPasswordRequest(token, newPassword))

                if (response.isSuccessful) {
                    Result.success(
                        response.body() ?: PasswordRecoveryResponse(
                            code = "password_reset_ok",
                            message = "Your password has been updated successfully."
                        )
                    )
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Reset password failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}