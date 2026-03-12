package cat.copernic.appvehicles.vehicle.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.model.Vehicle
import cat.copernic.appvehicles.vehicle.ui.viewmodel.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleLlistarScreen(
    onVehicleClick: (String) -> Unit,
    viewModel: VehicleViewModel = viewModel()
) {

    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var ordenAscendente by remember { mutableStateOf(true) }

    var expandedDates by remember { mutableStateOf(false) }
    var expandedPrice by remember { mutableStateOf(false) }

    val vehicles by viewModel.vehicles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadVehicles()
    }

    val vehiculosOrdenados =
        if (ordenAscendente) vehicles.sortedBy { it.preuHora }
        else vehicles.sortedByDescending { it.preuHora }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Vehicles") }
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                ExposedDropdownMenuBox(
                    expanded = expandedDates,
                    onExpandedChange = { expandedDates = !expandedDates },
                    modifier = Modifier.weight(1f)
                ) {

                    OutlinedTextField(
                        value = "Dates",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedDates) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedDates,
                        onDismissRequest = { expandedDates = false }
                    ) {

                        DropdownMenuItem(
                            text = { Text("Apply availability filter") },
                            onClick = {

                                if (fechaInicio.isNotBlank() && fechaFin.isNotBlank()) {
                                    viewModel.loadVehiclesDisponibles(fechaInicio, fechaFin)
                                }

                                expandedDates = false
                            }
                        )
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expandedPrice,
                    onExpandedChange = { expandedPrice = !expandedPrice },
                    modifier = Modifier.weight(1f)
                ) {

                    OutlinedTextField(
                        value = "Price",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedPrice) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedPrice,
                        onDismissRequest = { expandedPrice = false }
                    ) {

                        DropdownMenuItem(
                            text = { Text("Ascending") },
                            onClick = {
                                ordenAscendente = true
                                expandedPrice = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Descending") },
                            onClick = {
                                ordenAscendente = false
                                expandedPrice = false
                            }
                        )
                    }
                }
            }

            if (vehiculosOrdenados.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No vehicles available")
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