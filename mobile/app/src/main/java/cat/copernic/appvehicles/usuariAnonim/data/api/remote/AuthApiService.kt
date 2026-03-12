package cat.copernic.appvehicles.usuariAnonim.data.api.remote

import cat.copernic.appvehicles.model.ClientRegisterRequest
import cat.copernic.appvehicles.usuariAnonim.data.model.PasswordRecoveryRequest
import cat.copernic.appvehicles.usuariAnonim.data.model.PasswordRecoveryResponse
import cat.copernic.appvehicles.usuariAnonim.data.model.ResetPasswordRequest
import cat.copernic.appvehicles.model.LoginRequest
import cat.copernic.appvehicles.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    // AFEGIT "api/" a totes les rutes perquè coincideixin amb el Spring Boot

    @POST("api/auth/register")
    suspend fun register(@Body request: ClientRegisterRequest): Response<Unit>

    @POST("api/auth/recover-password")
    suspend fun recoverPassword(
        @Body request: PasswordRecoveryRequest
    ): Response<PasswordRecoveryResponse>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<PasswordRecoveryResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}