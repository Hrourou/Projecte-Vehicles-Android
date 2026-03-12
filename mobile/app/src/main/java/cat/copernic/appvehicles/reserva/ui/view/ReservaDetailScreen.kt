package cat.copernic.appvehicles.reserva.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.reserva.viewmodel.ReservaViewModel

/**
 * Pantalla que mostra els detalls d'una reserva específica.
 * Permet consultar la informació del vehicle, el desglossament de costos i
 * ofereix l'opció de cancel·lar la reserva si el seu estat ho permet.
 *
 * @param reservaId Identificador de la reserva a consultar.
 * @param viewModel ViewModel que gestiona la lògica de negoci de les reserves.
 * @param onNavigateBack Funció de retorn a la pantalla anterior.
 * @param userEmail Correu electrònic de l'usuari actiu (utilitzat per autoritzar la cancel·lació).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(
    reservaId: Long,
    viewModel: ReservaViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    userEmail: String
) {
    // Observació de l'estat del ViewModel
    val reserva by viewModel.reservaDetail.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val cancelResult by viewModel.cancelResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Constants de text traduïbles precarregades per ser utilitzades fora i dins de les corrutines
    val errorCancelPrefix = stringResource(R.string.error_cancel_prefix)
    val statusActive = stringResource(R.string.status_active)
    val statusCancelled = stringResource(R.string.status_cancelled)
    val statusFinished = stringResource(R.string.status_finished)

    // Càrrega inicial de dades de la reserva
    LaunchedEffect(reservaId) {
        if (reservaId != 0L) {
            viewModel.loadReservaDetalle(reservaId)
        }
    }

    // Gestor de la resposta del backend en cas de cancel·lació
    LaunchedEffect(cancelResult) {
        cancelResult?.onSuccess {
            snackbarHostState.showSnackbar(it.message)
            viewModel.clearCancelResult()
        }
        cancelResult?.onFailure { exception ->
            snackbarHostState.showSnackbar("$errorCancelPrefix: ${exception.message}")
            viewModel.clearCancelResult()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reservation_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
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
        } else if (reserva == null) {
            // Gestió d'error de connexió o dades no trobades
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Rounded.Warning, contentDescription = "Error", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.error_generic_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.error_load_reservation_details),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            reserva?.let { dadesReserva ->

                val estatReserva = dadesReserva.estat ?: "ACTIVA"
                val fiancaDouble = dadesReserva.fiancaPagada.toDoubleOrNull() ?: 0.0
                val importDouble = dadesReserva.importTotal.toDoubleOrNull() ?: 0.0
                val totalSumat = fiancaDouble + importDouble

                // Traducció dinàmica de l'estat provinent de la base de dades
                val displayStatus = when (estatReserva.uppercase()) {
                    "ACTIVA" -> statusActive
                    "CANCELADA", "CANCEL·LADA" -> statusCancelled
                    "FINALITZADA", "FINALIZADA" -> statusFinished
                    else -> estatReserva
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = stringResource(R.string.vehicle_photo_description),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.reservation_code, "RES-${dadesReserva.idReserva}"),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Surface(
                                    color = if (estatReserva == "ACTIVA") MaterialTheme.colorScheme.primaryContainer
                                    else if (estatReserva == "CANCELADA") MaterialTheme.colorScheme.errorContainer
                                    else MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = displayStatus,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            DetailRow(label = stringResource(R.string.reservation_vehicle), value = dadesReserva.vehicleMatricula)
                            DetailRow(label = stringResource(R.string.start_date), value = dadesReserva.dataInici)
                            DetailRow(label = stringResource(R.string.end_date), value = dadesReserva.dataFi)

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            DetailRow(label = stringResource(R.string.deposit), value = "${dadesReserva.fiancaPagada} €")
                            DetailRow(label = stringResource(R.string.rental_cost), value = "${dadesReserva.importTotal} €")

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(R.string.reservation_total, String.format("%.2f €", totalSumat)),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Lògica de presentació condicionada per l'estat
                    if (estatReserva == "ACTIVA") {
                        Button(
                            onClick = { showConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text(text = stringResource(R.string.cancel_reservation), color = MaterialTheme.colorScheme.onError)
                        }
                    } else if (estatReserva == "CANCELADA") {
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Email, contentDescription = "Email", tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.cancellation_notification_title),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                val emailTo = stringResource(R.string.email_to, userEmail)
                                val emailSubject = stringResource(R.string.email_subject_cancellation)
                                val emailGreeting = stringResource(R.string.email_body_greeting)
                                val emailVehicle = stringResource(R.string.email_body_cancelled_vehicle, dadesReserva.vehicleMatricula)
                                val emailRefund = stringResource(R.string.email_body_refund, String.format("%.2f", totalSumat))

                                Text(
                                    text = "$emailTo\n$emailSubject\n\n$emailGreeting\n$emailVehicle\n$emailRefund",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.cannot_cancel_started_finished),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(stringResource(R.string.cancel_reservation)) },
            text = { Text(stringResource(R.string.confirm_cancel_question)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.cancelReserva(reservaId, userEmail)
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

/**
 * Funció de suport per renderitzar files de detall.
 */
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}