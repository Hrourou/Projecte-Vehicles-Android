package cat.copernic.appvehicles.vehicle.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import cat.copernic.appvehicles.ui.theme.AppVehiclesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdenarVehiculosPorPrecioScreen(
    onBackClick: () -> Unit = {}
) {

    var ordenAscendente by remember { mutableStateOf(true) }

    val vehiculos = listOf(
        VehicleMock(1, "Tesla", "Model 3", "Elèctric", 25.0),
        VehicleMock(2, "Toyota", "Corolla", "Híbrid", 18.5),
        VehicleMock(3, "BMW", "X1", "Dièsel", 30.0)
    )

    val vehiculosOrdenados = if (ordenAscendente) {
        vehiculos.sortedBy { it.preuHora }
    } else {
        vehiculos.sortedByDescending { it.preuHora }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ordenar vehículos por precio") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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

            SelectorOrdenPrecio(
                ordenAscendente = ordenAscendente,
                onOrdenChange = { ordenAscendente = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(vehiculosOrdenados) { vehiculo ->
                    VehiculoDisponibleCard(vehiculo = vehiculo)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrdenarVehiculosPorPrecioScreenPreview() {
    AppVehiclesTheme {
        OrdenarVehiculosPorPrecioScreen()
    }
}