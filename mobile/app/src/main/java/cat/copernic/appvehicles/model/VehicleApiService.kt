package cat.copernic.appvehicles.vehicle.data.remote

import retrofit2.http.GET

interface VehicleApiService {

    @GET("vehicles") // AJUSTAR si tu endpoint es diferente
    suspend fun getVehicles(): List<VehicleDto>
}