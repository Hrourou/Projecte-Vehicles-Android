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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.R
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

    // TODO: esto debe salir de la sesión del cliente
    val emailCliente = "maria@test.com"

    val repo = remember { ReservaRepository(RetrofitProvider.reservaApi) }
    val factory = remember { ReservaViewModelFactory(repo) }
    val vm: ReservaViewModel = viewModel(factory = factory)

    val loading by vm.loading.collectAsState()
    val reserves by vm.reserves.collectAsState()

    LaunchedEffect(Unit) {
        if (!isPreview) vm.load(emailCliente)
    }

    // Traducciones de estado (valores)
    val statusActive = stringResource(R.string.status_active)
    val statusCancelled = stringResource(R.string.status_cancelled)
    val statusFinished = stringResource(R.string.status_finished)

    fun localizeStatus(raw: String): String {
        return when (raw.trim().uppercase()) {
            "ACTIVA", "ACTIVE" -> statusActive
            "CANCELADA", "CANCELLED", "CANCELED", "CANCEL·LADA" -> statusCancelled
            "FINALITZADA", "FINALIZADA", "FINISHED" -> statusFinished
            else -> raw
        }
    }

    // Preview sigue funcionando sin backend
    val listToShow: List<ReserveMock> = if (isPreview) {
        listOf(
            ReserveMock(1, "R12345", "10/03/2025", "12/03/2025", 120.0, localizeStatus("ACTIVA")),
            ReserveMock(2, "R54321", "01/02/2025", "05/02/2025", 300.0, localizeStatus("FINALITZADA")),
            ReserveMock(3, "R67890", "15/01/2025", "18/01/2025", 210.0, localizeStatus("CANCELADA"))
        )
    } else {
        reserves.map { it.toReserveMock(localizeStatus("ACTIVA")) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reservations_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { if (!isPreview) vm.toggleOrder(emailCliente) }) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = stringResource(R.string.sort)
                        )
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = listToShow,
                        key = { it.id }
                    ) { reserva ->
                        ReserveCard(reserve = reserva, onClick = { })
                    }
                }
            }
        }
    }
}

private fun ReservaResponse.toReserveMock(localizedStatus: String): ReserveMock {
    return ReserveMock(
        id = idReserva.toInt(),
        codi = idReserva.toString(),
        dataInici = dataInici,
        dataFi = dataFi,
        preuTotal = importTotal.toDoubleOrNull() ?: 0.0,
        estat = localizedStatus
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReserveListScreenPreview() {
    AppVehiclesTheme {
        ReserveListScreen()
    }
}