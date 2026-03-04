package cat.copernic.appvehicles.usuariAnonim.data.api.remote

import cat.copernic.appvehicles.model.ClientRegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.Body
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

}