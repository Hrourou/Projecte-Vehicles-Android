package cat.copernic.appvehicles.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

import cat.copernic.appvehicles.reserva.data.api.remote.RetrofitProvider
import cat.copernic.appvehicles.reserva.data.repository.ReservaRepository
import cat.copernic.appvehicles.reserva.ui.view.ReserveListScreen
import cat.copernic.appvehicles.reserva.ui.view.ReservationDetailScreen
import cat.copernic.appvehicles.reserva.viewmodel.ReservaViewModel
import cat.copernic.appvehicles.reserva.viewmodel.ReservaViewModelFactory

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

    // ViewModel para reservas
    val reservaViewModel: ReservaViewModel = viewModel(
        factory = ReservaViewModelFactory(
            ReservaRepository(RetrofitProvider.reservaApi)
        )
    )

    Scaffold(
        bottomBar = { AppBottomNavigation(navController) }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = AppRoutes.Inici.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            // -----------------------------
            // HOME
            // -----------------------------
            composable(AppRoutes.Inici.route) {
                HomeScreen(
                    onVehicleClick = { vehicleId ->
                        navController.navigate("${AppRoutes.VehicleDetail.route}/$vehicleId")
                    }
                )
            }

            // -----------------------------
            // RESERVES LIST
            // -----------------------------
            composable(AppRoutes.Reserves.route) {
                ReserveListScreen(
                    onBackClick = { navController.popBackStack() },
                    onReservaSelected = { idReserva ->
                        navController.navigate("reserva_detail/$idReserva")
                    }
                )
            }

            // -----------------------------
            // RESERVA DETAIL
            // -----------------------------
            composable(
                route = "reserva_detail/{idReserva}",
                arguments = listOf(
                    navArgument("idReserva") { type = NavType.LongType }
                )
            ) { backStackEntry ->

                val idReserva =
                    backStackEntry.arguments?.getLong("idReserva") ?: 0L

                ReservationDetailScreen(
                    reservaId = idReserva,
                    viewModel = reservaViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // -----------------------------
            // PERFIL (REGISTER)
            // -----------------------------
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