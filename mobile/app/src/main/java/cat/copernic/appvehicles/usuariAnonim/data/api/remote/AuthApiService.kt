package cat.copernic.appvehicles.usuariAnonim.data.api.remote

import cat.copernic.appvehicles.model.ClientRegisterRequest
import cat.copernic.appvehicles.model.LoginRequest // Asegúrate de que la ruta importe tus nuevos DTOs
import cat.copernic.appvehicles.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthApiService {

    // --- ENDPOINT PARA REGISTRO ACTUALIZADO ---
    @POST("auth/register")
    suspend fun register(@Body request: ClientRegisterRequest): Response<Unit>

    // --- NUEVO ENDPOINT PARA LOGIN ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}