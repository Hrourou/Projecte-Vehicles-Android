package cat.copernic.appvehicles.reserva.data.api.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    // IP actualizada según tu ipconfig (Wi-Fi)
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val reservaApi: ReservaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReservaApi::class.java)
    }
}
