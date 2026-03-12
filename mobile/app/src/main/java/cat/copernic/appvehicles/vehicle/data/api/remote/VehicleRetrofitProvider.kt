package cat.copernic.appvehicles.vehicle.data.api.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object VehicleRetrofitProvider {

    // URL del emulador a tu PC
    private const val BASE_URL = "http://10.16.28.239:8080/"

    val vehicleApi: VehicleApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VehicleApiService::class.java)
    }
}