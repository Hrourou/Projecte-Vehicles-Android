package cat.copernic.appvehicles.vehicle.data.api.remote

import cat.copernic.appvehicles.model.VehicleResponse
import retrofit2.Response
import retrofit2.http.GET

interface VehicleApiService {

    // Llamada real al backend
    @GET("api/vehicles")
    suspend fun getVehicles(): Response<List<VehicleResponse>>
}