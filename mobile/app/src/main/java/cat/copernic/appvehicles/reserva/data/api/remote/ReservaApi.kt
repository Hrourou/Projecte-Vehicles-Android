package cat.copernic.appvehicles.reserva.data.api.remote

import cat.copernic.appvehicles.reserva.data.model.CancelReservaResponse
import cat.copernic.appvehicles.reserva.data.model.CreateReservaRequest
import cat.copernic.appvehicles.reserva.data.model.ReservaResponse
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReservaApi {
    @DELETE("api/reserves/{id}")
    suspend fun cancelReserva(
        @Path("id") id: Long,
        @Query("userName") userName: String
    ): CancelReservaResponse
    @GET("api/reserves/{id}")
    suspend fun getReservaById(@Path("id") id: Long): ReservaResponse
    @POST("api/reserves")
    suspend fun crearReserva(
        @Body request: CreateReservaRequest
    ): ReservaResponse
    @GET("api/reserves")
    suspend fun getReservas(
        @Query("email") email: String,
        @Query("order") order: String = "desc"
    ): List<ReservaResponse>

}