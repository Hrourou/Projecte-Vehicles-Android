package cat.copernic.appvehicles.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cat.copernic.appvehicles.reserva.ui.view.ReserveListScreen
import cat.copernic.appvehicles.usuariAnonim.data.repository.AuthRepository
import cat.copernic.appvehicles.usuariAnonim.ui.view.HomeScreen
import cat.copernic.appvehicles.usuariAnonim.ui.view.RegisterScreen
import cat.copernic.appvehicles.usuariAnonim.ui.viewmodel.RegisterViewModel
import cat.copernic.appvehicles.usuariAnonim.ui.viewmodel.RegisterViewModelFactory
import cat.copernic.appvehicles.vehicle.ui.view.VehicleLlistarScreen
import cat.copernic.appvehicles.vehicle.ui.view.VehicleDetailScreen
import cat.copernic.appvehicles.vehicle.ui.view.VehicleMock

@Composable
fun MainScreen(
    repository: AuthRepository
) {
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
                HomeScreen(
                    onVehicleClick = { vehicleId ->
                        // futura navegación si quieres
                    }
                )
            }

            composable(AppRoutes.Reserves.route) {
                ReserveListScreen()
            }

            composable(AppRoutes.Perfil.route) {

                val registerViewModel: RegisterViewModel = viewModel(
                    factory = RegisterViewModelFactory(repository)
                )

                RegisterScreen(
                    viewModel = registerViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(AppRoutes.Inici.route) {
                            popUpTo(AppRoutes.Inici.route) { inclusive = true }
                        }
                    }
                )
            }

            // -----------------------------
            // VEHICLES LIST
            // -----------------------------
            composable(AppRoutes.Vehicles.route) {
                VehicleLlistarScreen(
                    onVehicleClick = { vehicleId ->
                        navController.navigate("${AppRoutes.VehicleDetail.route}/$vehicleId")
                    }
                )
            }

            // -----------------------------
            // VEHICLE DETAIL
            // -----------------------------
            composable(
                route = "${AppRoutes.VehicleDetail.route}/{vehicleId}",
                arguments = listOf(
                    navArgument("vehicleId") { type = NavType.IntType }
                )
            ) { backStackEntry ->

                val vehicleId =
                    backStackEntry.arguments?.getInt("vehicleId") ?: 0

                // Mock temporal
                val vehicleMock = VehicleMock(
                    id = vehicleId,
                    marca = "Tesla",
                    model = "Model 3",
                    variant = "Elèctric",
                    preuHora = 25.0
                )

                VehicleDetailScreen(
                    vehicle = vehicleMock,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}