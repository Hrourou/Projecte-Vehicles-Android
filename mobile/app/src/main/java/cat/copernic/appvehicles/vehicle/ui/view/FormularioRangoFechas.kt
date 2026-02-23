package cat.copernic.appvehicles.vehicle.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FormularioRangoFechas(
    fechaInicio: String,
    fechaFin: String,
    onFechaInicioChange: (String) -> Unit,
    onFechaFinChange: (String) -> Unit,
    onBuscarClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Selecciona rango de fechas",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = fechaInicio,
                onValueChange = onFechaInicioChange,
                label = { Text("Fecha inicio (dd/mm/yyyy)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = fechaFin,
                onValueChange = onFechaFinChange,
                label = { Text("Fecha fin (dd/mm/yyyy)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBuscarClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Consultar disponibilidad")
            }
        }
    }
}