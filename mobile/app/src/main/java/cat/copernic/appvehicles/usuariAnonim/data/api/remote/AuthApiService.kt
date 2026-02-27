package cat.copernic.appvehicles.usuariAnonim.data.api.remote

import cat.copernic.appvehicles.model.ClientRegisterRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    // La ruta debe coincidir con tu Backend: @RequestMapping("/api/auth") + @PostMapping("/register")
    // Asumimos que la BaseURL de Retrofit termina en /api/
    @POST("auth/register")
    suspend fun register(@Body request: ClientRegisterRequest): Response<ResponseBody>
}