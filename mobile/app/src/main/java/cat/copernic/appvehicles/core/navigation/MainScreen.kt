package cat.copernic.appvehicles.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cat.copernic.appvehicles.reserva.ui.view.ReserveListScreen
import cat.copernic.appvehicles.usuariAnonim.data.repository.AuthRepository
import cat.copernic.appvehicles.usuariAnonim.ui.view.HomeScreen
import cat.copernic.appvehicles.usuariAnonim.ui.view.RegisterScreen
import cat.copernic.appvehicles.usuariAnonim.ui.viewmodel.RegisterViewModel
import cat.copernic.appvehicles.usuariAnonim.ui.viewmodel.RegisterViewModelFactory

@Composable
fun MainScreen(repository: AuthRepository) { // 1. Recibimos el repositorio desde MainActivity
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomNavigation(navController) }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = AppRoutes.Inici.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(AppRoutes.Inici.route) {
                HomeScreen(onVehicleClick = { vehicleId ->
                    // Navegación al detalle
                })
            }

            composable(AppRoutes.Reserves.route) {
                ReserveListScreen()
            }

            // RUTA DE REGISTRO (Actualmente en Perfil según tu código)
            composable(AppRoutes.Perfil.route) {

                // 2. Creamos el ViewModel usando la Factory e inyectando el Repositorio
                // Esto asegura que el ViewModel sobreviva a rotaciones de pantalla.
                val registerViewModel: RegisterViewModel = viewModel(
                    factory = RegisterViewModelFactory(repository)
                )

                // 3. Pasamos el ViewModel a la pantalla
                RegisterScreen(
                    viewModel = registerViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSuccess = {
                        // Al terminar registro, vamos al inicio (o login)
                        navController.navigate(AppRoutes.Inici.route) {
                            // Limpiamos la pila para que no pueda volver atrás al registro
                            popUpTo(AppRoutes.Inici.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}