package cat.copernic.appvehicles.usuariAnonim.data.api.remote

import cat.copernic.appvehicles.model.ClientRegisterRequest
import cat.copernic.appvehicles.usuariAnonim.data.model.PasswordRecoveryRequest
import cat.copernic.appvehicles.usuariAnonim.data.model.PasswordRecoveryResponse
import cat.copernic.appvehicles.usuariAnonim.data.model.ResetPasswordRequest
import cat.copernic.appvehicles.model.LoginRequest // Asegúrate de que la ruta importe tus nuevos DTOs
import cat.copernic.appvehicles.model.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body request: ClientRegisterRequest): Response<Unit>


    @POST("auth/recover-password")
    suspend fun recoverPassword(
        @Body request: PasswordRecoveryRequest
    ): Response<PasswordRecoveryResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<PasswordRecoveryResponse>

    // --- NUEVO ENDPOINT PARA LOGIN ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}