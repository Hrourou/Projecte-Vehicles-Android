package cat.copernic.androidretrofit3

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import cat.copernic.appvehicles.ui.theme.AppVehiclesTheme
import cat.copernic.appvehicles.vehicle.ui.view.VehicleMock

/**
 * RF91 - Detall vehicle
 * Pantalla plantilla amb dades simulades (Mock)
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(
    vehicle: VehicleMock,
    onBackClick: () -> Unit = {}
) {

    val potencia = "283 CV"
    val color = "Blanc"
    val limitKm = "300 km/dia"
    val minDies = "1 dia"
    val maxDies = "15 dies"
    val fianca = "500€"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detall vehicle") },
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("Imatge vehicle")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${vehicle.marca} ${vehicle.model}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = vehicle.variant,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${vehicle.preuHora}€/hora",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text("Potència: $potencia")
                    Text("Color: $color")
                    Text("Límit quilometratge: $limitKm")
                    Text("Mínim dies lloguer: $minDies")
                    Text("Màxim dies lloguer: $maxDies")
                    Text("Fiança estàndard: $fianca")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reservar vehicle")
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VehicleDetailScreenPreview() {

    val mockVehicle = VehicleMock(
        id = 1,
        marca = "Tesla",
        model = "Model 3",
        variant = "Elèctric",
        preuHora = 25.0
    )

    AppVehiclesTheme {
        VehicleDetailScreen(vehicle = mockVehicle)
    }
}