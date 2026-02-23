package cat.copernic.appvehicles.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun AppBottomNavigation() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Inici", "Reserves", "Perfil")
    val icons = listOf(Icons.Default.Home, Icons.Default.ShoppingCart, Icons.Default.Person)

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}