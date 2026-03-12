package cat.copernic.appvehicles.vehicle.ui.view

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.model.Vehicle
import cat.copernic.appvehicles.vehicle.ui.viewmodel.VehicleViewModel
import cat.copernic.appvehicles.core.composables.rememberBase64Bitmap
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleLlistarScreen(
    onVehicleClick: (String, String, String) -> Unit,
    viewModel: VehicleViewModel = viewModel()
) {

    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var ordenAscendente by remember { mutableStateOf(true) }

    var expandedPrice by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

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
                title = { Text(stringResource(R.string.app_vehicles)) }
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // FILTRO FECHAS
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _: DatePicker, year: Int, month: Int, day: Int ->
                                fechaInicio = "%04d-%02d-%02d".format(year, month + 1, day)

                                DatePickerDialog(
                                    context,
                                    { _: DatePicker, year2: Int, month2: Int, day2: Int ->
                                        fechaFin = "%04d-%02d-%02d".format(year2, month2 + 1, day2)

                                        if (fechaInicio.isNotBlank() && fechaFin.isNotBlank()) {

                                            val start = java.time.LocalDate.parse(fechaInicio)
                                            val end = java.time.LocalDate.parse(fechaFin)

                                            val days = java.time.temporal.ChronoUnit.DAYS.between(start, end)

                                            if (days < 0) {
                                                Toast.makeText(
                                                    context,
                                                    "error_end_date_after_start",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                viewModel.loadVehiclesDisponibles(fechaInicio, fechaFin)
                                            }
                                        }
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                ) {
                    Text(stringResource(R.string.dates))
                }

                // ORDENAR POR PRECIO
                ExposedDropdownMenuBox(
                    expanded = expandedPrice,
                    onExpandedChange = { expandedPrice = !expandedPrice },
                    modifier = Modifier.weight(1f)
                ) {

                    OutlinedTextField(
                        value = stringResource(R.string.price),
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

                // BOTÓ LIMPIAR FILTRE
                IconButton(
                    onClick = {
                        fechaInicio = ""
                        fechaFin = ""
                        viewModel.loadVehicles()

                        Toast.makeText(
                            context,
                            "date_filter_cleared)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear filter"
                    )
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
                                onVehicleClick(vehicle.id, fechaInicio, fechaFin)
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
        Column {

            val base64String = vehicle.fotoBase64
            val uriSimulada = base64String?.let { "data:image/jpeg;base64,$it" }
            val fotoCocheBitmap = rememberBase64Bitmap(uriSimulada)

            if (fotoCocheBitmap != null) {

                Image(
                    bitmap = fotoCocheBitmap,
                    contentDescription = "Foto de ${vehicle.marca} ${vehicle.model}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )

            } else {

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {

                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Sense imatge",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "${vehicle.marca} ${vehicle.model}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = vehicle.variant,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${vehicle.preuHora} €/h",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}
