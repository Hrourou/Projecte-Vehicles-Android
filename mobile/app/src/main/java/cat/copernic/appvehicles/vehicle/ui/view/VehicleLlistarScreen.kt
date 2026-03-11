package cat.copernic.appvehicles.vehicle.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.model.Vehicle
import cat.copernic.appvehicles.ui.theme.AppVehiclesTheme
import cat.copernic.appvehicles.vehicle.ui.viewmodel.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleLlistarScreen(
    onVehicleClick: (String) -> Unit,
    onBackClick: () -> Unit = {},
    viewModel: VehicleViewModel = viewModel()
) {

    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var ordenAscendente by remember { mutableStateOf(true) }

    val vehicles by viewModel.vehicles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Cargar vehículos al abrir pantalla
    LaunchedEffect(Unit) {
        viewModel.loadVehicles()
    }

    // Ordenación por precio
    val vehiculosOrdenados = if (ordenAscendente) {
        vehicles.sortedBy { it.preuHora }
    } else {
        vehicles.sortedByDescending { it.preuHora }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.available_vehicles)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            // -----------------------------
            // FILTRO POR FECHAS
            // -----------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = stringResource(R.string.filter_by_date_range),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = fechaInicio,
                        onValueChange = { fechaInicio = it },
                        label = { Text(stringResource(R.string.start_date_dd_mm_yyyy)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fechaFin,
                        onValueChange = { fechaFin = it },
                        label = { Text(stringResource(R.string.end_date_dd_mm_yyyy)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (fechaInicio.isNotBlank() && fechaFin.isNotBlank()) {
                                viewModel.loadVehiclesDisponibles(fechaInicio, fechaFin)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.apply_filter))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -----------------------------
            // ORDENACIÓN
            // -----------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = stringResource(R.string.sort_by_price),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        FilterChip(
                            selected = ordenAscendente,
                            onClick = { ordenAscendente = true },
                            label = { Text(stringResource(R.string.ascending)) }
                        )

                        FilterChip(
                            selected = !ordenAscendente,
                            onClick = { ordenAscendente = false },
                            label = { Text(stringResource(R.string.descending)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -----------------------------
            // LISTADO VEHÍCULOS
            // -----------------------------
            if (vehiculosOrdenados.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No vehicles available",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

            } else {

                LazyColumn {

                    items(vehiculosOrdenados) { vehicle ->

                        VehicleCard(
                            vehicle = vehicle,
                            onClick = {
                                onVehicleClick(vehicle.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleCard(
    vehicle: Vehicle,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
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

@Preview(showBackground = true)
@Composable
fun VehicleListUnifiedScreenPreview() {
    AppVehiclesTheme {
        VehicleLlistarScreen(
            onVehicleClick = {}
        )
    }
}