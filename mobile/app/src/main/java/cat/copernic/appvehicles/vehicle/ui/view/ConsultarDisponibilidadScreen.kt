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
fun ConsultarDisponibilidadScreen(
    onBackClick: () -> Unit = {}
) {

    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var mostrarResultados by remember { mutableStateOf(false) }

    val vehiculosDisponibles = listOf(
        VehicleMock(1, "Tesla", "Model 3", "Elèctric", 25.0),
        VehicleMock(2, "Toyota", "Corolla", "Híbrid", 18.5)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consultar disponibilidad") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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

            item {
                FormularioRangoFechas(
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin,
                    onFechaInicioChange = {
                        val it = ""
                        fechaInicio = it
                    },
                    onFechaFinChange = {
                        val it = ""
                        fechaFin = it
                    },
                    onBuscarClick = { mostrarResultados = true }
                )
            }

            if (mostrarResultados) {

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Vehículos disponibles",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(vehiculosDisponibles) { vehiculo ->
                    VehiculoDisponibleCard(vehiculo = vehiculo)
                }
            }
        }
    }
}

@Composable
fun FormularioRangoFechas(
    fechaInicio: String,
    fechaFin: String,
    onFechaInicioChange: () -> Unit,
    onFechaFinChange: () -> Unit,
    onBuscarClick: () -> Unit
) {
    TODO("Not yet implemented")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ConsultarDisponibilidadScreenPreview() {
    AppVehiclesTheme {
        ConsultarDisponibilidadScreen()
    }
}