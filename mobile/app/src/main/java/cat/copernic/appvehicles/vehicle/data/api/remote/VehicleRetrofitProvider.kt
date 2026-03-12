package cat.copernic.appvehicles.vehicle.data.api.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object VehicleRetrofitProvider {

    // IP actualizada según tu ipconfig (Wi-Fi)
    private const val BASE_URL = "http://192.168.1.17:8080/"

    val vehicleApi: VehicleApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VehicleApiService::class.java)
    }
}
