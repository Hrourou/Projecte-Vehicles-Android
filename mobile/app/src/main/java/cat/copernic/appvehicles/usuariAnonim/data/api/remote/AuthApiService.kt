package cat.copernic.appvehicles.usuariAnonim.data.api.remote

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

    @Multipart
    @POST("auth/register")
    suspend fun register(
        @Part("clientData") clientData: RequestBody,
        @Part fotoIdentificacio: MultipartBody.Part,
        @Part fotoLlicencia: MultipartBody.Part
    ): Response<Unit>

    // --- NUEVO ENDPOINT PARA LOGIN ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}