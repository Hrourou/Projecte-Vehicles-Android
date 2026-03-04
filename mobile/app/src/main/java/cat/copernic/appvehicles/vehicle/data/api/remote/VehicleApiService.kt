package cat.copernic.appvehicles.vehicle.data.api.remote

import cat.copernic.appvehicles.model.VehicleResponse
import retrofit2.Response
import retrofit2.http.GET

interface VehicleApiService {

    // Backend: @RequestMapping("/api/vehicles")
    @GET("vehicles")
    suspend fun getVehicles(): Response<List<VehicleResponse>>
}