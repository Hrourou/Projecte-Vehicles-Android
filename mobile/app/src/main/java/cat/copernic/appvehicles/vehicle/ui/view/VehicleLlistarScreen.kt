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
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.model.VehicleMock
import cat.copernic.appvehicles.ui.theme.AppVehiclesTheme
import cat.copernic.appvehicles.vehicle.ui.viewmodel.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleLlistarScreen(
    viewModel: VehicleViewModel? = null,
    onVehicleClick: (String) -> Unit,
    onBackClick: () -> Unit = {}
) {

    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var ordenAscendente by remember { mutableStateOf(true) }
    var aplicarFiltro by remember { mutableStateOf(false) }

    // Obtenim les dades reals del ViewModel
    val vehiclesReal = viewModel?.vehicles?.collectAsState()?.value ?: emptyList()
    val isLoading = viewModel?.isLoading?.collectAsState()?.value ?: false

    // Cridem al backend al carregar la pantalla
    LaunchedEffect(viewModel) {
        viewModel?.loadVehicles()
    }

    // 🔴 FI DELS MOCKS: Ara NOMÉS mapegem el que ve de la base de dades real
    val vehiculosUi: List<VehicleMock> = vehiclesReal.map { v ->
        VehicleMock(
            id = v.id, // Guardem la matrícula real
            marca = v.marca,
            model = v.model,
            variant = v.variant,
            preuHora = v.preuHora
        )
    }

    // Ordenem per preu
    val vehiculosOrdenados = if (ordenAscendente) {
        vehiculosUi.sortedBy { it.preuHora }
    } else {
        vehiculosUi.sortedByDescending { it.preuHora }
    }

    // Filtre simulat de dates
    val vehiculosFinal = if (aplicarFiltro && fechaInicio.isNotBlank() && fechaFin.isNotBlank()) {
        vehiculosOrdenados.take(2)
    } else {
        vehiculosOrdenados
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.available_vehicles)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
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

            // Mentre carrega de la BD, mostrem la rodeta
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            // Filtres i Ordenació...
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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

            // 🔴 CONTROL DE BASE DE DADES BUIDA
            if (vehiculosFinal.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hi ha vehicles a la BD o error de connexió.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                // LLISTA DE VEHICLES REALS
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(vehiculosFinal) { vehiculo ->
                        VehicleCard(
                            vehicle = vehiculo,
                            onClick = {
                                onVehicleClick(vehiculo.id) // Enviem la matrícula al fer click
                            }
                        )
                    }
                }
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