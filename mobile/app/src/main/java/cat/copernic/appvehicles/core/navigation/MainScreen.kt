package cat.copernic.appvehicles.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

@Composable
fun MainScreen(repository: AuthRepository) {
    val navController = rememberNavController()

    val reservaViewModel: ReservaViewModel = viewModel(
        factory = ReservaViewModelFactory(ReservaRepository(RetrofitProvider.reservaApi))
    )

    Scaffold(
        bottomBar = { AppBottomNavigation(navController) }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = AppRoutes.Inici.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(AppRoutes.Inici.route) {
                HomeScreen(onVehicleClick = { vehicleId -> })
            }

            composable(AppRoutes.Reserves.route) {
                ReserveListScreen(
                    onBackClick = { navController.popBackStack() },
                    onReservaSelected = { idReserva ->
                        navController.navigate("reserva_detail/$idReserva")
                    }
                )
            }

            composable("reserva_detail/{idReserva}") { backStackEntry ->
                val idReserva = backStackEntry.arguments?.getString("idReserva")?.toLongOrNull() ?: 0L
                ReservationDetailScreen(
                    reservaId = idReserva,
                    viewModel = reservaViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onCancelReservation = { /* Puedes llamar método anular en ViewModel y luego popBackStack() */ }
                )
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
        }
    }
}