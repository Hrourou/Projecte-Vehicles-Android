package cat.copernic.appvehicles.vehicle.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.tooling.preview.Preview
import cat.copernic.appvehicles.ui.theme.AppVehiclesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleLlistarScreen(
    onVehicleClick: (Int) -> Unit,
    onBackClick: () -> Unit = {}
) {

    // -----------------------------
    // Estados
    // -----------------------------
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var ordenAscendente by remember { mutableStateOf(true) }
    var aplicarFiltro by remember { mutableStateOf(false) }

    // -----------------------------
    // Datos simulados
    // -----------------------------
    val vehiculos = listOf(
        VehicleMock(1, "Tesla", "Model 3", "Elèctric", 25.0),
        VehicleMock(2, "Toyota", "Corolla", "Híbrid", 18.5),
        VehicleMock(3, "BMW", "X1", "Dièsel", 30.0)
    )

    // -----------------------------
    // Lógica ordenación
    // -----------------------------
    val vehiculosOrdenados = if (ordenAscendente) {
        vehiculos.sortedBy { it.preuHora }
    } else {
        vehiculos.sortedByDescending { it.preuHora }
    }

    // -----------------------------
    // Lógica filtrado fechas (mock)
    // -----------------------------
    val vehiculosFinal = if (aplicarFiltro && fechaInicio.isNotBlank() && fechaFin.isNotBlank()) {
        // Simulación simple (backend real lo hará de verdad)
        vehiculosOrdenados.take(2)
    } else {
        vehiculosOrdenados
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vehicles disponibles") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar")
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // -----------------------------
            // FILTRO FECHAS
            // -----------------------------
            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = "Filtrar per rang de dates",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = fechaInicio,
                            onValueChange = { fechaInicio = it },
                            label = { Text("Data inici (dd/mm/yyyy)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = fechaFin,
                            onValueChange = { fechaFin = it },
                            label = { Text("Data fi (dd/mm/yyyy)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { aplicarFiltro = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Aplicar filtre")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // -----------------------------
            // ORDEN POR PRECIO
            // -----------------------------
            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = "Ordenar per preu",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                            FilterChip(
                                selected = ordenAscendente,
                                onClick = { ordenAscendente = true },
                                label = { Text("Ascendent") }
                            )

                            FilterChip(
                                selected = !ordenAscendente,
                                onClick = { ordenAscendente = false },
                                label = { Text("Descendent") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // -----------------------------
            // LISTADO VEHICULOS
            // -----------------------------
            items(vehiculosFinal) { vehiculo ->

                VehicleCard(
                    vehicle = vehiculo,
                    onClick = {
                        onVehicleClick(vehiculo.id)
                    }
                )
            }
        }
    }
}

@Composable
fun VehicleCard(vehicle: VehicleMock, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "${vehicle.marca} ${vehicle.model}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = vehicle.variant,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${vehicle.preuHora} €/h",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VehicleListUnifiedScreenPreview() {
    AppVehiclesTheme {
        VehicleLlistarScreen(
            onVehicleClick = {}
        )
    }
}