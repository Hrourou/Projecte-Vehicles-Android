package cat.copernic.appvehicles.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import cat.copernic.appvehicles.reserva.ui.view.CreateReservationScreen
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

// Import CLAU per llegir qui està loguejat
import cat.copernic.appvehicles.core.auth.SessionManager

@Composable
fun MainScreen(
    repository: AuthRepository
) {
    val navController = rememberNavController()

    // OBTENIR L'EMAIL REAL DEL MÒBIL
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userEmail by sessionManager.userEmailFlow.collectAsState(initial = "")

    // 1. Instanciar el ViewModel de Reserves
    val reservaViewModel: ReservaViewModel = viewModel(
        factory = ReservaViewModelFactory(
            ReservaRepository(RetrofitProvider.reservaApi)
        )
    )

    // 2. Instanciar el ViewModel de Vehicles
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
            startDestination = AppRoutes.Vehicles.route,
            modifier = Modifier.padding(paddingValues)
        ) {


            // RESERVES LIST
            composable(AppRoutes.Reserves.route) {
                ReserveListScreen(
                    userEmail = userEmail ?: "", // <-- Li passem l'email REAL loguejat!
                    viewModel = reservaViewModel,
                    onBackClick = { navController.popBackStack() },
                    onReservaSelected = { idReserva ->
                        navController.navigate("reserva_detail/$idReserva")
                    }
                )
            }

            // RESERVA DETAIL
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
                    userEmail = userEmail ?: "", // <-- Li passem l'email REAL loguejat!
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // RESERVA CREATE (CORREGIT AMB MATRÍCULA)
            composable(
                route = "reserva_create/{matricula}", // <-- Això arregla el bug del cotxe buit!
                arguments = listOf(navArgument("matricula") { type = NavType.StringType })
            ) { backStackEntry ->

                val matriculaEscollida = backStackEntry.arguments?.getString("matricula") ?: ""

                CreateReservationScreen(
                    matriculaFixa = matriculaEscollida,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = reservaViewModel,
                    vehicleViewModel = vehicleViewModel,
                    userEmail = userEmail ?: "", // <-- L'email real!
                    onReservaCreada = { idReserva ->
                        navController.navigate("reserva_detail/$idReserva") {
                            popUpTo(AppRoutes.Vehicles.route)
                        }
                    }
                )
            }

            // PERFIL
            // PERFIL
            composable(AppRoutes.Perfil.route) {
                ProfileEntryScreen(
                    authRepository = repository,
                    onLoginSuccessNavigate = {
                        // CORRECCIÓN: Ahora limpiamos hasta 'Vehicles' (nuestra nueva pantalla principal)
                        navController.navigate(AppRoutes.Vehicles.route) {
                            popUpTo(AppRoutes.Vehicles.route) { inclusive = true }
                        }
                    }
                )
            }

            // VEHICLES LIST
            composable(AppRoutes.Vehicles.route) {
                VehicleLlistarScreen(
                    viewModel = vehicleViewModel,
                    onVehicleClick = { matricula: String ->
                        navController.navigate("${AppRoutes.VehicleDetail.route}/$matricula")
                    },
                    // --- AÑADIMOS ESTAS DOS LÍNEAS ---
                    onLoginClick = {
                        navController.navigate(AppRoutes.Perfil.route) // Asumiendo que 'Inici' es tu pantalla de Login
                    },
                    onRegisterClick = {
                        navController.navigate(AppRoutes.Register.route)
                    }
                    // ---------------------------------
                )
            }

            // VEHICLE DETAIL
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
                    // CORREGIT: Passem la matricula a la següent pantalla!
                    onReservarClick = { navController.navigate("reserva_create/$matricula") }
                )
            }

            // REGISTER
            composable(AppRoutes.Register.route) {
                val registerViewModel: RegisterViewModel = viewModel(
                    factory = RegisterViewModelFactory(repository)
                )

                RegisterScreen(
                    viewModel = registerViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(AppRoutes.Vehicles.route) {
                            popUpTo(AppRoutes.Register.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}