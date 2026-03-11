package cat.copernic.appvehicles.usuariAnonim.data.api.remote

import cat.copernic.appvehicles.usuariAnonim.data.model.PasswordRecoveryRequest
import cat.copernic.appvehicles.usuariAnonim.data.model.PasswordRecoveryResponse
import cat.copernic.appvehicles.usuariAnonim.data.model.ResetPasswordRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApiService {

    @Multipart
    @POST("auth/register")
    suspend fun register(
        @Part("clientData") clientData: RequestBody,
        @Part fotoIdentificacio: MultipartBody.Part,
        @Part fotoLlicencia: MultipartBody.Part
    ): Response<Unit>

    @POST("auth/recover-password")
    suspend fun recoverPassword(
        @Body request: PasswordRecoveryRequest
    ): Response<PasswordRecoveryResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<PasswordRecoveryResponse>
}