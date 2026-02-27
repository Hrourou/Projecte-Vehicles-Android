// Archivo: core/navigation/AppBottomNavigation.kt
package cat.copernic.appvehicles.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    // Asociamos cada ruta con su etiqueta e icono
    val items = listOf(
        Triple(AppRoutes.Inici.route, "Inici", Icons.Default.Home),
        Triple(AppRoutes.Reserves.route, "Reserves", Icons.Default.ShoppingCart),
        Triple(AppRoutes.Perfil.route, "Perfil", Icons.Default.Person)
    )

    // Observamos la ruta actual para saber qué botón iluminar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        // Evita crear una pila infinita de pantallas al navegar
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Evita abrir la misma pantalla múltiples veces si clicas muy rápido
                        launchSingleTop = true
                        // Restaura el estado previo si vuelves a la pestaña
                        restoreState = true
                    }
                }
            )
        }
    }
}