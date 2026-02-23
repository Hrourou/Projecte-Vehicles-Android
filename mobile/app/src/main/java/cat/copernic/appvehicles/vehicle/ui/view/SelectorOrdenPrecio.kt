package cat.copernic.appvehicles.vehicle.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectorOrdenPrecio(
    ordenAscendente: Boolean,
    onOrdenChange: (Boolean) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Ordenar por precio",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                FilterChip(
                    selected = ordenAscendente,
                    onClick = { onOrdenChange(true) },
                    label = { Text("Ascendente") }
                )

                FilterChip(
                    selected = !ordenAscendente,
                    onClick = { onOrdenChange(false) },
                    label = { Text("Descendente") }
                )
            }
        }
    }
}