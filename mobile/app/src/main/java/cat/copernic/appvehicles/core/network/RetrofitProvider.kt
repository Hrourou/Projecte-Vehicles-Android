package cat.copernic.appvehicles.core.network

import cat.copernic.appvehicles.reserva.data.api.remote.ReservaApi
// CORREGIT: El nom real de l'arxiu és VehicleApiService, no VehicleApi
import cat.copernic.appvehicles.vehicle.data.api.remote.VehicleApiService
// Importem l'API dels companys correctament
import cat.copernic.appvehicles.usuariAnonim.data.api.remote.AuthApiService

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val client: OkHttpClient by lazy {
        val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        OkHttpClient.Builder()
            .addInterceptor(log)
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // --- LLISTA DE TOTES LES APIs DE L'APLICACIÓ ---

    val reservaApi: ReservaApi by lazy {
        retrofit.create(ReservaApi::class.java)
    }

    // CORREGIT: Utilitzem VehicleApiService
    val vehicleApi: VehicleApiService by lazy {
        retrofit.create(VehicleApiService::class.java)
    }

    // Aquesta és la que necessitava el MainActivity!
    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}