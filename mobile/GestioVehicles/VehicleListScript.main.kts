package cat.copernic.androidretrofit3

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListScreen() {

    // Llista simulada (temporal)
    val vehicles = listOf(
        Vehicle("Tesla", "Model 3", "Elèctric", 25.0),
        Vehicle("Toyota", "Corolla", "Híbrid", 18.5),
        Vehicle("BMW", "X1", "Dièsel", 30.0)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Llistat de Vehicles") }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(vehicles) { vehicle ->
                VehicleCard(vehicle)
            }
        }
    }
}

@Composable
fun VehicleCard(vehicle: Vehicle) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "${vehicle.marca} ${vehicle.model}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Variant: ${vehicle.variant}")

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Preu/hora: ${vehicle.preuHora} €")
        }
    }

