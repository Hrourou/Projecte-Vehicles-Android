package cat.copernic.appvehicles.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Imports dels teus companys
import cat.copernic.appvehicles.client.ui.view.ProfileEntryScreen
import cat.copernic.appvehicles.usuariAnonim.data.repository.AuthRepository
import cat.copernic.appvehicles.usuariAnonim.ui.view.HomeScreen

// Imports de les teves funcionalitats de Reserva i Vehicles
import cat.copernic.appvehicles.reserva.data.api.remote.RetrofitProvider
import cat.copernic.appvehicles.reserva.data.repository.ReservaRepository
import cat.copernic.appvehicles.reserva.ui.view.ReserveListScreen
import cat.copernic.appvehicles.reserva.ui.view.ReservationDetailScreen
import cat.copernic.appvehicles.reserva.ui.view.CreateReservationScreen // <-- PANTALLA NOVA
import cat.copernic.appvehicles.reserva.viewmodel.ReservaViewModel
import cat.copernic.appvehicles.reserva.viewmodel.ReservaViewModelFactory
import cat.copernic.appvehicles.usuariAnonim.ui.view.RegisterScreen
import cat.copernic.appvehicles.usuariAnonim.ui.viewmodel.RegisterViewModel
import cat.copernic.appvehicles.usuariAnonim.ui.viewmodel.RegisterViewModelFactory

import cat.copernic.appvehicles.vehicle.data.api.remote.VehicleRetrofitProvider
import cat.copernic.appvehicles.vehicle.data.repository.VehicleRepository
import cat.copernic.appvehicles.vehicle.ui.view.VehicleDetailScreen
import cat.copernic.appvehicles.vehicle.ui.view.VehicleLlistarScreen
import cat.copernic.appvehicles.vehicle.ui.viewmodel.VehicleViewModel
import cat.copernic.appvehicles.vehicle.ui.viewmodel.VehicleViewModelFactory

@Composable
fun MainScreen(
    repository: AuthRepository
) {
    val navController = rememberNavController()

    // 1. Instanciar el ViewModel de Reserves
    val reservaViewModel: ReservaViewModel = viewModel(
        factory = ReservaViewModelFactory(
            ReservaRepository(RetrofitProvider.reservaApi)
        )
    )

    // 2. Instanciar el ViewModel de Vehicles (Necessari pel desplegable de crear reserva)
    val vehicleViewModel: VehicleViewModel = viewModel(
        factory = VehicleViewModelFactory(
            VehicleRepository(VehicleRetrofitProvider.vehicleApi)
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
                    viewModel = vehicleViewModel, // <-- Li passem el ViewModel real!
                    onVehicleClick = { matricula -> // <-- Canviem vehicleId per matricula
                        navController.navigate("${AppRoutes.VehicleDetail.route}/$matricula")
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
                val idReserva = backStackEntry.arguments?.getLong("idReserva") ?: 0L
                ReservationDetailScreen(
                    reservaId = idReserva,
                    viewModel = reservaViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // -----------------------------
            // RESERVA CREATE (La teva pantalla!)
            // -----------------------------
            composable("reserva_create") {
                // Usuari fixat (hardcodejat) tal com has demanat, ja que a la BD existeix
                val userEmail = "maria@test.com"

                CreateReservationScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = reservaViewModel,
                    vehicleViewModel = vehicleViewModel, // Passem els vehicles reals
                    userEmail = userEmail,
                    onReservaCreada = { idReserva ->
                        // Quan es crea, anem al detall i netegem la navegació
                        navController.navigate("reserva_detail/$idReserva") {
                            popUpTo(AppRoutes.Vehicles.route)
                        }
                    }
                )
            }

            // -----------------------------
            // PERFIL -> RF04 GATE
            // -----------------------------
            composable(AppRoutes.Perfil.route) {
                ProfileEntryScreen(authRepository = repository)
            }

            // -----------------------------
            // VEHICLES LIST
            // -----------------------------
            composable(AppRoutes.Vehicles.route) {
                VehicleLlistarScreen(
                    viewModel = vehicleViewModel,
                    onVehicleClick = { matricula: String -> // Passem la matricula real!
                        navController.navigate("${AppRoutes.VehicleDetail.route}/$matricula")
                    }
                )
            }

            // -----------------------------
            // VEHICLE DETAIL
            // -----------------------------
            composable(
                route = "${AppRoutes.VehicleDetail.route}/{matricula}",
                arguments = listOf(
                    navArgument("matricula") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val matricula = backStackEntry.arguments?.getString("matricula") ?: ""

                VehicleDetailScreen(
                    matricula = matricula,
                    viewModel = vehicleViewModel,
                    onBackClick = { navController.popBackStack() },
                    // Connectem el botó de reservar amb la ruta de reserva_create!
                    onReservarClick = { navController.navigate("reserva_create") }
                )
            }

            // -----------------------------
// REGISTER
// -----------------------------
            composable(AppRoutes.Register.route) {

                val registerViewModel: RegisterViewModel = viewModel(
                    factory = RegisterViewModelFactory(repository)
                )

                RegisterScreen(
                    viewModel = registerViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSuccess = {
                        // Cuando el registro se complete
                        navController.navigate(AppRoutes.Inici.route) {
                            popUpTo(AppRoutes.Register.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}