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


// -----------------------------------------------------------------
// COMPONENT CALENDARI (DatePickerDialog de Material 3)
// Ens permet seleccionar la data amb una interfície gràfica de calendari
// -----------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerModal(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    // Convertim els milisegons a format "YYYY-MM-DD"
                    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                    onDateSelected(date.toString())
                }
                onDismiss()
            }) { Text(stringResource(R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

private data class VehicleOption(
    val name: String,
    val matricula: String,
    val preuHora: Double
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReservationScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ReservaViewModel? = null,
    vehicleViewModel: VehicleViewModel? = null,
    userEmail: String = "client@exemple.com",
    onReservaCreada: (Long) -> Unit = {}
) {
    // 1. CARREGAR VEHICLES REALS
    val vehiclesReals = vehicleViewModel?.vehicles?.collectAsState()?.value ?: emptyList()
    val availableVehicles = if (vehiclesReals.isNotEmpty()) {
        vehiclesReals.map { VehicleOption("${it.marca} ${it.model}", it.id, it.preuHora) }
    } else {
        listOf(VehicleOption(stringResource(R.string.no_vehicles_available), "", 0.0))
    }

    var selectedVehicle by remember { mutableStateOf(availableVehicles.firstOrNull() ?: VehicleOption("", "", 0.0)) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // 2. DATES AMB CALENDARI (Controls)
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    // Obtenim el context per poder llegir els strings des de dins del botó
    val context = androidx.compose.ui.platform.LocalContext.current

    var costCalculat by remember { mutableStateOf(0.0) }
    val fiancaFixa = 300.0

    // 3. CÀLCUL DEL PREU (S'executa quan canvien les dates o el vehicle)
    LaunchedEffect(startDate, endDate, selectedVehicle) {
        try {
            if (startDate.isNotBlank() && endDate.isNotBlank()) {
                val inici = LocalDate.parse(startDate)
                val fi = LocalDate.parse(endDate)

                var dies = ChronoUnit.DAYS.between(inici, fi)
                if (dies <= 0) dies = 1L // Mínim un dia de lloguer

                costCalculat = dies * 24 * selectedVehicle.preuHora
            } else {
                costCalculat = 0.0
            }
        } catch (e: Exception) {
            costCalculat = 0.0
        }
    }

    // 4. ESTAT DEL BACKEND
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
                // Posem el text directament per evitar problemes amb Compose
                errorMsg = "Error reservation: " + e.message
            }
        )
    }

    // MODAL DELS CALENDARIS
    if (showStartDatePicker) {
        DatePickerModal(onDateSelected = { startDate = it }, onDismiss = { showStartDatePicker = false })
    }
    if (showEndDatePicker) {
        DatePickerModal(onDateSelected = { endDate = it }, onDismiss = { showEndDatePicker = false })
    }

    // DIÀLEG D'ÈXIT
    if (showSuccessDialog && reservaId != null) {
        AlertDialog(
            onDismissRequest = {},
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
                    onReservaCreada(reservaId!!)
                }) { Text(stringResource(R.string.view_detail)) }
            }
        )
    }

    // INTERFÍCIE (UI)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_reservation_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // FILA DE CALENDARIS
            Text(stringResource(R.string.select_dates), style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                // Quadre de text clicable per la Data d'Inici
                Box(modifier = Modifier.weight(1f).clickable { showStartDatePicker = true }) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { },
                        enabled = false, // Desactivat perquè l'usuari no pugui escriure manualment
                        label = { Text(stringResource(R.string.start_date)) },
                        trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                        colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                // Quadre de text clicable per la Data Final
                Box(modifier = Modifier.weight(1f).clickable { showEndDatePicker = true }) {
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { },
                        enabled = false,
                        label = { Text(stringResource(R.string.end_date)) },
                        trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                        colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DESPLEGABLE DE VEHICLES
            Text(stringResource(R.string.available_vehicle), style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }) {
                OutlinedTextField(
                    value = selectedVehicle.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.reservation_vehicle)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                    availableVehicles.forEach { vehicle ->
                        DropdownMenuItem(text = { Text(vehicle.name) }, onClick = { selectedVehicle = vehicle; isDropdownExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // RESUM PREU
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.cost_summary), fontWeight = FontWeight.Bold)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    CostRow(stringResource(R.string.rental_cost), String.format("%.2f €", costCalculat))
                    CostRow(stringResource(R.string.deposit), String.format("%.2f €", fiancaFixa))
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    CostRow(stringResource(R.string.total_to_pay), String.format("%.2f €", costCalculat + fiancaFixa))
                }
            }

            // ERRORS (Mala data seleccionada, etc.)
            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.weight(1f))

            // BOTÓ CONFIRMAR
            Button(
                onClick = {
                    errorMsg = null

                    // Comprovar si les dates estan buides o del revés
                    if (startDate.isBlank() || endDate.isBlank()) {
                        errorMsg = "Selecciona les dates"
                        return@Button
                    }
                    val i = LocalDate.parse(startDate)
                    val f = LocalDate.parse(endDate)
                    if (i.isAfter(f)) {
                        errorMsg = "Error: " // Hauries de llegir el recurs aquí amb el Context, però ho deixem simple
                        return@Button
                    }

                    // Cridar al Backend
                    viewModel?.crearReserva(CreateReservaRequest(userEmail, selectedVehicle.matricula, startDate, endDate, userEmail))
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !loading
            ) {
                if (loading) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text(stringResource(R.string.confirm_reservation))
            }
        }
    }
}

@Composable
fun CostRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
    }
}