package cat.copernic.appvehicles.vehicle.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VehiculoDisponibleCard(
    vehiculo: VehicleMock
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = "${vehiculo.marca} ${vehiculo.model}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = vehiculo.variant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "${vehiculo.preuHora}€/h",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}