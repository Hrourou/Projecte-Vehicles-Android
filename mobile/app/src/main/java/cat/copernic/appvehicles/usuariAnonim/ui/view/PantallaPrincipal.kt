package cat.copernic.appvehicles.usuariAnonim.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cat.copernic.appvehicles.core.composables.VehicleCard
import cat.copernic.appvehicles.core.composables.VehicleMock
import cat.copernic.appvehicles.core.navigation.AppBottomNavigation
import androidx.compose.ui.tooling.preview.Preview
import cat.copernic.appvehicles.ui.theme.AppVehiclesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onVehicleClick: (Int) -> Unit) {
    // Datos de prueba estáticos para poder ver el diseño
    val mockVehicles = listOf(
        VehicleMock(1, "Seat", "Ibiza", "Combustió", 15.0),
        VehicleMock(2, "Tesla", "Model 3", "Elèctric", 30.0),
        VehicleMock(3, "Toyota", "Yaris", "Híbrid", 18.0)
        )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AppVehicles") }, // Deberás cambiarlo por el logo/nombre final (RN30)
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },

    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            // RF56: Sección visual para el filtro de fechas
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Filtre dates")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        //Uso de recursos de string para soportar Catalán, Castellano e Inglés
                        text = "Seleccionar dates de disponibilitat...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            LazyColumn {
                items(mockVehicles) { vehicle ->
                    VehicleCard(
                        vehicle = vehicle,
                        onClick = { onVehicleClick(vehicle.id) }
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    // Reemplaza 'AppVehiclesTheme' por el nombre real de tu tema
    // (suele estar en ui/theme/Theme.kt)
    AppVehiclesTheme {
        HomeScreen(onVehicleClick = {})
    }
}