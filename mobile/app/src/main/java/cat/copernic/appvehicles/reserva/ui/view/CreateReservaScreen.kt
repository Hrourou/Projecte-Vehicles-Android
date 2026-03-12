package cat.copernic.appvehicles.reserva.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.reserva.data.model.CreateReservaRequest
import cat.copernic.appvehicles.reserva.viewmodel.ReservaViewModel
import cat.copernic.appvehicles.vehicle.ui.viewmodel.VehicleViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Modal dissenyat segons les guies de Material 3 per a la selecció interactiva de dates.
 *
 * @param onDateSelected Callback executat en confirmar, proveint un String format YYYY-MM-DD.
 * @param onDismiss Callback en cancel·lar l'operació.
 */
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerModal(onDateSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                    onDateSelected(date.toString())
                }
                onDismiss()
            }) { Text(stringResource(R.string.ok)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    ) { DatePicker(state = datePickerState) }
}

/**
 * Pantalla que permet a l'usuari processar el lloguer d'un vehicle pre-seleccionat.
 * L'algorisme intern avalua dinàmicament el preu total basant-se en els dies i el cost per hora del vehicle.
 *
 * @param matriculaFixa Referència a la identitat del vehicle escollit.
 * @param onNavigateBack Funció de retorn de navegació.
 * @param viewModel Encarregat de gestionar la petició POST cap a l'API.
 * @param vehicleViewModel Repositori de les dades base dels vehicles per extreure les tarifes.
 * @param userEmail Adreça de l'usuari autenticat; avalua els permisos d'accés a l'esdeveniment de creació.
 * @param onReservaCreada Event executat post-creació (envia la clau primària resultant per a navegació encadenada).
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReservationScreen(
    matriculaFixa: String = "",
    onNavigateBack: () -> Unit = {},
    viewModel: ReservaViewModel? = null,
    vehicleViewModel: VehicleViewModel? = null,
    userEmail: String = "",
    onReservaCreada: (Long) -> Unit = {}
) {
    // 1. Obtenció de dades locals
    val vehiclesReals = vehicleViewModel?.vehicles?.collectAsState()?.value ?: emptyList()
    val vehicleSeleccionat = vehiclesReals.find { it.id == matriculaFixa }

    val preuHora = vehicleSeleccionat?.preuHora ?: 0.0
    val nomVehicle = if (vehicleSeleccionat != null) "${vehicleSeleccionat.marca} ${vehicleSeleccionat.model}" else matriculaFixa

    // 2. Definició d'estat de UI
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    var costCalculat by remember { mutableStateOf(0.0) }
    val fiancaFixa = 300.0 // Valor prefixat temporal. Pot ser extret d'una constant del sistema.

    val isLoggedIn = userEmail.isNotBlank()

    // Llegeix els recursos abans d'entrar a la corrutina
    val selectDatesError = stringResource(R.string.error_dates_required)
    val invalidDatesError = stringResource(R.string.error_invalid_date_range)

    // 3. Reactor de càlcul de pressupost
    LaunchedEffect(startDate, endDate) {
        try {
            if (startDate.isNotBlank() && endDate.isNotBlank()) {
                val inici = LocalDate.parse(startDate)
                val fi = LocalDate.parse(endDate)
                var dies = ChronoUnit.DAYS.between(inici, fi)
                if (dies <= 0) dies = 1L
                costCalculat = dies * 24 * preuHora
            } else {
                costCalculat = 0.0
            }
        } catch (e: Exception) { costCalculat = 0.0 }
    }

    // 4. Reactor de l'estat remot
    val loading = viewModel?.loading?.collectAsState()?.value ?: false
    val creationResult = viewModel?.creationResult?.collectAsState()?.value
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var reservaId by remember { mutableStateOf<Long?>(null) }
    var importConfirmat by remember { mutableStateOf("0.00") }

    LaunchedEffect(creationResult) {
        creationResult?.fold(
            onSuccess = { reserva ->
                errorMsg = null
                reservaId = reserva.idReserva
                importConfirmat = reserva.importTotal
                showSuccessDialog = true
            },
            onFailure = { e ->
                // Sense traducció: mostra directament l'error tècnic
                errorMsg = e.message
            }
        )
    }

    // Modal de calendaris
    if (showStartDatePicker) DatePickerModal(onDateSelected = { startDate = it }, onDismiss = { showStartDatePicker = false })
    if (showEndDatePicker) DatePickerModal(onDateSelected = { endDate = it }, onDismiss = { showEndDatePicker = false })

    // Diàleg final d'èxit
    if (showSuccessDialog && reservaId != null) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.reservation_created_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.reservation_code, reservaId!!))
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.reservation_amount, importConfirmat))
                }
            },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    viewModel?.clearCreationResult()
                    onReservaCreada(reservaId!!)
                }) { Text(stringResource(R.string.view_detail)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    viewModel?.clearCreationResult()
                    onNavigateBack()
                }) { Text(stringResource(R.string.close)) }
            }
        )
    }

    // Renderització principal
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_reservation_title)) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back)) } }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(16.dp)) {

            // Prevenció d'accés anònim
            if (!isLoggedIn) {
                OutlinedCard(
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.login_required_desc),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(stringResource(R.string.select_dates), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f).clickable { showStartDatePicker = true }) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = {},
                        enabled = false,
                        label = { Text(stringResource(R.string.start_date)) },
                        trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                        colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface)
                    )
                }
                Box(modifier = Modifier.weight(1f).clickable { showEndDatePicker = true }) {
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = {},
                        enabled = false,
                        label = { Text(stringResource(R.string.end_date)) },
                        trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                        colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.reservation_vehicle), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nomVehicle,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.cost_summary), fontWeight = FontWeight.Bold)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    CostRow(stringResource(R.string.rental_cost), String.format("%.2f €", costCalculat))
                    CostRow(stringResource(R.string.deposit), String.format("%.2f €", fiancaFixa))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    CostRow(stringResource(R.string.total_to_pay), String.format("%.2f €", costCalculat + fiancaFixa))
                }
            }

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(errorMsg!!, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    errorMsg = null
                    if (startDate.isBlank() || endDate.isBlank()) {
                        errorMsg = selectDatesError
                        return@Button
                    }
                    val i = LocalDate.parse(startDate)
                    val f = LocalDate.parse(endDate)
                    if (i.isAfter(f)) {
                        errorMsg = invalidDatesError
                        return@Button
                    }

                    viewModel?.crearReserva(CreateReservaRequest(userEmail, matriculaFixa, startDate, endDate, userEmail))
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !loading && isLoggedIn
            ) {
                if (loading) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text(stringResource(R.string.confirm_reservation))
            }
        }
    }
}

/**
 * Component estructural per facilitar la presentació semàntica de parells "Etiqueta-Valor" econòmics.
 */
@Composable
fun CostRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
    }
}