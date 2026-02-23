package cat.copernic.appvehicles.reserva.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import cat.copernic.appvehicles.ui.theme.AppVehiclesTheme

/**
 * RF51 - Llistar reserves
 * Pantalla plantilla amb dades simulades (Mock)
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReserveListScreen(
    onBackClick: () -> Unit = {}
) {

    // EXACTAMENTE como vehicles
    val reserves = listOf(
        ReserveMock(1, "R12345", "10/03/2025", "12/03/2025", 120.0, "ACTIVA"),
        ReserveMock(2, "R54321", "01/02/2025", "05/02/2025", 300.0, "FINALITZADA"),
        ReserveMock(3, "R67890", "15/01/2025", "18/01/2025", 210.0, "CANCELADA")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Les meves reserves") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Tornar enrere"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            items(reserves) { reserve ->

                ReserveCard(
                    reserve = reserve,
                    onClick = { }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReserveListScreenPreview() {
    AppVehiclesTheme {
        ReserveListScreen()
    }
}