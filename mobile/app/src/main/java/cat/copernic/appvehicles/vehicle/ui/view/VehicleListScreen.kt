package cat.copernic.androidretrofit3

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import cat.copernic.appvehicles.ui.theme.AppVehiclesTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

/**
 * RF90 - Llistar vehicles
 * Pantalla plantilla amb dades simulades (Mock)
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListScreen(
    onBackClick: () -> Unit = {}
) {

    // Dades temporals (més endavant vindran del ViewModel)
    val vehicles = listOf(
        VehicleMock(1, "Tesla", "Model 3", "Elèctric", 25.0),
        VehicleMock(2, "Toyota", "Corolla", "Híbrid", 18.5),
        VehicleMock(3, "BMW", "X1", "Dièsel", 30.0)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Vehicles disponibles")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Tornar enrere"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            items(vehicles) { vehicle ->

                VehicleCard(
                    vehicle = vehicle,
                    onClick = {
                        // RF91 (detall vehicle) s’implementarà més endavant
                    }
                )
            }
        }
    }
}


/**
 * Model temporal (Mock)
 * Més endavant vindrà de la capa Domain
 */
data class VehicleMock(
    val id: Int,
    val marca: String,
    val model: String,
    val variant: String,
    val preuHora: Double
)


/**
 * Component reutilitzable (RN25)
 */
@Composable
fun VehicleCard(
    vehicle: VehicleMock,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Placeholder imatge vehicle
            Surface(
                modifier = Modifier.size(80.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("Foto")
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = "${vehicle.marca} ${vehicle.model}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = vehicle.variant,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "${vehicle.preuHora}€/h",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VehicleListScreenPreview() {

    AppVehiclesTheme {   // ⚠️ Usa el teu Theme real
        VehicleListScreen()
    }
}