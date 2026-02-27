package cat.copernic.appvehicles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cat.copernic.appvehicles.core.navigation.MainScreen
import cat.copernic.appvehicles.ui.theme.AppVehiclesTheme
import cat.copernic.appvehicles.usuariAnonim.data.api.remote.AuthApiService
import cat.copernic.appvehicles.usuariAnonim.data.repository.AuthRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Configuración de Red (Retrofit)
        // Nota: 10.0.2.2 es 'localhost' desde el emulador Android.
        // Si usas un móvil físico, necesitarás la IP local de tu PC (ej: 192.168.1.XX)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.51.59.239:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 2. Crear las instancias de la Capa de Datos
        val authService = retrofit.create(AuthApiService::class.java)
        val authRepository = AuthRepository(authService)

        setContent {
            AppVehiclesTheme {
                // 3. Inyectamos el repositorio en la navegación principal
                // (Dará error aquí hasta que me pases MainScreen.kt y lo actualicemos)
                MainScreen(authRepository)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppVehiclesTheme {
        Greeting("Android")
    }
}
