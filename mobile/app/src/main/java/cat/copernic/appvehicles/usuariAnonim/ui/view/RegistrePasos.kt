package cat.copernic.appvehicles.usuariAnonim.ui.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import cat.copernic.appvehicles.core.composables.ImageUploadButton
import cat.copernic.appvehicles.core.composables.ReusableTextField
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pas1DadesPersonals(state: RegisterUiState, onStateChange: (RegisterUiState) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        val formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        onStateChange(state.copy(dataCaducitatId = formattedDate))
                    }
                }) { Text("Acceptar") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel·lar") } }
        ) { DatePicker(state = datePickerState) }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) onStateChange(state.copy(fotoIdentificacioUri = uri.toString())) }
    )

    Text("Dades Personals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    ReusableTextField(value = state.nomComplet, onValueChange = { onStateChange(state.copy(nomComplet = it)) }, label = "Nom complet")
    ReusableTextField(value = state.numeroIdentificacio, onValueChange = { onStateChange(state.copy(numeroIdentificacio = it)) }, label = "Número d'identificació")

    OutlinedTextField(
        value = state.dataCaducitatId, onValueChange = { }, label = { Text("Data caducitat") }, readOnly = true, modifier = Modifier.fillMaxWidth(),
        trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange, "Seleccionar data") } }
    )

    ImageUploadButton(label = "Pujar foto identificació", isUploaded = state.fotoIdentificacioUri != null) {
        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pas2DadesConduccio(state: RegisterUiState, onStateChange: (RegisterUiState) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        val formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        onStateChange(state.copy(dataCaducitatLlicencia = formattedDate))
                    }
                }) { Text("Acceptar") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel·lar") } }
        ) { DatePicker(state = datePickerState) }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) onStateChange(state.copy(fotoLlicenciaUri = uri.toString())) }
    )

    Text("Dades de Conducció i Pagament", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    ReusableTextField(value = state.tipusLlicencia, onValueChange = { onStateChange(state.copy(tipusLlicencia = it)) }, label = "Tipus de llicència")

    OutlinedTextField(
        value = state.dataCaducitatLlicencia, onValueChange = { }, label = { Text("Data caducitat llicència") }, readOnly = true,
        modifier = Modifier.fillMaxWidth(), trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange, "Seleccionar data") } }
    )

    ImageUploadButton(label = "Pujar foto llicència", isUploaded = state.fotoLlicenciaUri != null) {
        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    OutlinedTextField(
        value = state.numeroTargetaCredit,
        onValueChange = { text ->
            val nomesNumeros = text.filter { it.isDigit() }
            if (nomesNumeros.length <= 19) {
                onStateChange(state.copy(numeroTargetaCredit = nomesNumeros))
            }
        },
        label = { Text("Targeta de crèdit") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = MaterialTheme.shapes.medium
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pas3DadesContacte(state: RegisterUiState, onStateChange: (RegisterUiState) -> Unit) {
    val llistaPaisos = remember { java.util.Locale.getISOCountries().map { isoCode -> java.util.Locale("", isoCode).displayCountry }.sorted() }
    var expadit by remember { mutableStateOf(false) }

    Text("Dades de Contacte i Accés", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    ReusableTextField(value = state.adreca, onValueChange = { onStateChange(state.copy(adreca = it)) }, label = "Adreça")

    ExposedDropdownMenuBox(
        expanded = expadit,
        onExpandedChange = { expadit = !expadit }
    ) {
        OutlinedTextField(
            value = state.nacionalitat, onValueChange = {}, readOnly = true, label = { Text("Nacionalitat") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expadit) },
            modifier = Modifier.menuAnchor().fillMaxWidth(), shape = MaterialTheme.shapes.medium
        )
        ExposedDropdownMenu(expanded = expadit, onDismissRequest = { expadit = false }) {
            llistaPaisos.forEach { pais ->
                DropdownMenuItem(
                    text = { Text(pais) },
                    onClick = {
                        onStateChange(state.copy(nacionalitat = pais))
                        expadit = false
                    }
                )
            }
        }
    }

    ReusableTextField(
        value = state.email,
        onValueChange = { onStateChange(state.copy(email = it)) },
        label = "Email (Usuari)",
        placeholder = "email@example.com"
    )
    ReusableTextField(
        value = state.password,
        onValueChange = { onStateChange(state.copy(password = it)) },
        label = "Contrasenya",
        placeholder = "Contrasenya (mín 6 caràcters)",
        isPassword = true
    )
}