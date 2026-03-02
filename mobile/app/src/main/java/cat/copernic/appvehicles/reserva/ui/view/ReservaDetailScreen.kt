package cat.copernic.appvehicles.reserva.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.reserva.data.model.ReservaResponse
import cat.copernic.appvehicles.reserva.viewmodel.ReservaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(
    reservaId: Long = 0L,
    viewModel: ReservaViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onCancelReservation: () -> Unit = {}
) {
    val reservaState by viewModel.reservaDetail.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(reservaId) {
        if (reservaId != 0L) {
            viewModel.loadReservaDetalle(reservaId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reservation_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            ReservaDetailContent(
                reserva = reservaState ?: dummyReserva(),
                onCancelReservation = onCancelReservation,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun ReservaDetailContent(
    reserva: ReservaResponse,
    onCancelReservation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.DirectionsCar,
                contentDescription = stringResource(R.string.vehicle_photo_description),
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Detalles
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.reservation_code, "RES-${reserva.idReserva}"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                DetailRow(label = stringResource(R.string.reservation_vehicle), value = reserva.vehicleMatricula)
                DetailRow(label = stringResource(R.string.start_date), value = reserva.dataInici)
                DetailRow(label = stringResource(R.string.end_date), value = reserva.dataFi)
                DetailRow(label = stringResource(R.string.reservation_status_label), value = stringResource(R.string.status_active)) // adaptar estado real si tienes

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                DetailRow(label = stringResource(R.string.deposit), value = "${reserva.fiancaPagada} €")
                DetailRow(label = stringResource(R.string.rental_cost), value = "${reserva.importTotal} €")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.reservation_total, "${reserva.importTotal} €"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onCancelReservation,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(
                text = stringResource(R.string.cancel_reservation),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onError
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun dummyReserva() = ReservaResponse(
    idReserva = 98765,
    dataInici = "15/11/2023 - 10:00",
    dataFi = "20/11/2023 - 10:00",
    clientEmail = "maria@test.com",
    vehicleMatricula = "Toyota Corolla Híbrido",
    importTotal = "250.00",
    fiancaPagada = "150.00"
)

@Preview(showBackground = true)
@Composable
fun ReservaDetailScreenPreview() {
    MaterialTheme {
        ReservationDetailScreen()
    }
}