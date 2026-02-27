package cat.copernic.appvehicles.reserva.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.reserva.data.api.remote.RetrofitProvider
import cat.copernic.appvehicles.reserva.data.model.ReservaResponse
import cat.copernic.appvehicles.reserva.data.repository.ReservaRepository
import cat.copernic.appvehicles.reserva.viewmodel.ReservaViewModel
import cat.copernic.appvehicles.reserva.viewmodel.ReservaViewModelFactory
import cat.copernic.appvehicles.ui.theme.AppVehiclesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReserveListScreen(
    onBackClick: () -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current

    // Para pruebas: más adelante esto saldrá del usuario logueado
    val emailCliente = "maria@test.com"

    val repo = remember { ReservaRepository(RetrofitProvider.reservaApi) }
    val factory = remember { ReservaViewModelFactory(repo) }
    val vm: ReservaViewModel = viewModel(factory = factory)

    val loading by vm.loading.collectAsState()
    val reserves by vm.reserves.collectAsState()
    val asc by vm.asc.collectAsState()

    LaunchedEffect(Unit) {
        if (!isPreview) vm.load(emailCliente)
    }

    // Preview sigue funcionando sin backend
    val listToShow: List<ReserveMock> = if (isPreview) {
        listOf(
            ReserveMock(1, "R12345", "10/03/2025", "12/03/2025", 120.0, "ACTIVA"),
            ReserveMock(2, "R54321", "01/02/2025", "05/02/2025", 300.0, "FINALITZADA"),
            ReserveMock(3, "R67890", "15/01/2025", "18/01/2025", 210.0, "CANCELADA")
        )
    } else {
        reserves.map { it.toReserveMock() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Les meves reserves") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar enrere")
                    }
                },
                actions = {
                    IconButton(onClick = { if (!isPreview) vm.toggleOrder(emailCliente) }) {
                        Icon(Icons.Default.SwapVert, contentDescription = "Ordenar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!isPreview && loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(listToShow) { reserva ->
                        ReserveCard(reserve = reserva, onClick = { })
                    }
                }
            }

            if (!isPreview) {
                // Solo para que veas el estado actual (opcional)
                // Text("Orden: ${if (asc) "asc" else "desc"}", modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}

private fun ReservaResponse.toReserveMock(): ReserveMock {
    return ReserveMock(
        id = idReserva.toInt(), // tu ReserveMock usa Int
        codi = idReserva.toString(),
        dataInici = dataInici,
        dataFi = dataFi,
        preuTotal = importTotal.toDoubleOrNull() ?: 0.0,
        estat = "ACTIVA" // backend aún no manda estado; cuando lo tengáis, lo cambiamos
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReserveListScreenPreview() {
    AppVehiclesTheme {
        ReserveListScreen()
    }
}