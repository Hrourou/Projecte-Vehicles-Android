package cat.copernic.appvehicles.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cat.copernic.appvehicles.client.ui.view.ProfileEntryScreen
import cat.copernic.appvehicles.reserva.ui.view.ReserveListScreen
import cat.copernic.appvehicles.usuariAnonim.ui.view.HomeScreen

@Composable
fun MainScreen() {
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
                HomeScreen(onVehicleClick = { /* TODO */ })
            }
            composable(AppRoutes.Reserves.route) {
                ReserveListScreen()
            }
            composable(AppRoutes.Perfil.route) {
                // Aquí ya no enseñamos registro siempre: mostramos “gate” (login/registro o editar perfil)
                ProfileEntryScreen()
            }
        }
    }
}