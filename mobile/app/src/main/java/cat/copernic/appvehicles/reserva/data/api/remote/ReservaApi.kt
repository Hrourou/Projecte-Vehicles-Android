package cat.copernic.appvehicles.reserva.data.api.remote

import cat.copernic.appvehicles.reserva.data.model.ReservaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ReservaApi {

    @GET("api/reserves")
    suspend fun getReservas(
        @Query("email") email: String,
        @Query("order") order: String = "desc"
    ): List<ReservaResponse>
}