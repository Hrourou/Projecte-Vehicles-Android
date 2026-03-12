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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.core.composables.ImageUploadOrPreview
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
                }) { Text(stringResource(R.string.acceptar)) }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text(
                stringResource(
                    R.string.cancel_lar
                )) } }
        ) { DatePicker(state = datePickerState) }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) onStateChange(state.copy(fotoIdentificacioUri = uri.toString())) }
    )

    Text(stringResource(R.string.dades_personals), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    ReusableTextField(value = state.nomComplet, onValueChange = { onStateChange(state.copy(nomComplet = it)) }, label = stringResource(
        R.string.nom_complet
    ))
    ReusableTextField(value = state.numeroIdentificacio, onValueChange = { onStateChange(state.copy(numeroIdentificacio = it)) }, label = stringResource(
        R.string.n_mero_d_identificaci
    ))

    OutlinedTextField(
        value = state.dataCaducitatId, onValueChange = { }, label = { Text(stringResource(R.string.data_caducitat)) }, readOnly = true, modifier = Modifier.fillMaxWidth(),
        trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange,
            stringResource(R.string.seleccionar_data)
        ) } }
    )

    ImageUploadOrPreview(
        label = "Pujar foto identificació", // O tu stringResource
        imageUri = state.fotoIdentificacioUri,
        onUploadClick = {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onDeleteClick = {
            // Si el usuario le da a la papelera, volvemos a poner la URI a null
            onStateChange(state.copy(fotoIdentificacioUri = null))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pas2DadesConduccio(state: RegisterUiState, onStateChange: (RegisterUiState) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val llistatLlicencies = listOf("AM", "A1", "A2", "A", "B1", "B", "C1", "C", "D1", "D")
    var expaditLlicencia by remember { mutableStateOf(false) }

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
                }) { Text(stringResource(R.string.acceptar)) }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel_lar)) } }
        ) { DatePicker(state = datePickerState) }
    }

    // --- LAUNCHER PARA LA FOTO DE LA LICENCIA (El que ya tenías) ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) onStateChange(state.copy(fotoLlicenciaUri = uri.toString())) }
    )



    // TÍTULO DEL PASO 2
    Text(stringResource(R.string.dades_de_conducci_i_pagament), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)


    // CAMPOS ORIGINALES DE CONDUCCIÓN
    ExposedDropdownMenuBox(
        expanded = expaditLlicencia,
        onExpandedChange = { expaditLlicencia = !expaditLlicencia }
    ) {
        OutlinedTextField(
            value = state.tipusLlicencia,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.tipus_de_llic_ncia)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expaditLlicencia) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )
        ExposedDropdownMenu(
            expanded = expaditLlicencia,
            onDismissRequest = { expaditLlicencia = false }
        ) {
            llistatLlicencies.forEach { llicencia ->
                DropdownMenuItem(
                    text = { Text(llicencia) },
                    onClick = {
                        onStateChange(state.copy(tipusLlicencia = llicencia))
                        expaditLlicencia = false
                    }
                )
            }
        }
    }

    OutlinedTextField(
        value = state.dataCaducitatLlicencia, onValueChange = { }, label = { Text(stringResource(R.string.data_caducitat_llic_ncia)) }, readOnly = true,
        modifier = Modifier.fillMaxWidth(), trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange,
            stringResource(R.string.seleccionar_data) )}}
    )

    ImageUploadOrPreview(
        label = "Pujar foto llicència",
        imageUri = state.fotoLlicenciaUri,
        onUploadClick = {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onDeleteClick = {
            onStateChange(state.copy(fotoLlicenciaUri = null))
        }
    )

    OutlinedTextField(
        value = state.numeroTargetaCredit,
        onValueChange = { text ->
            val nomesNumeros = text.filter { it.isDigit() }
            if (nomesNumeros.length <= 19) {
                onStateChange(state.copy(numeroTargetaCredit = nomesNumeros))
            }
        },
        label = { Text(stringResource(R.string.targeta_de_cr_dit)) },
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

    Text(stringResource(R.string.dades_de_contacte_i_acc_s), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

    // --- LAUNCHER FOTO PERFIL ---
    val profilePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) onStateChange(state.copy(fotoPerfilUri = uri.toString())) }
    )

// --- FOTO PERFIL (ARRIBA DEL TODO) ---
    ImageUploadOrPreview(
        label = "Pujar foto de perfil (Opcional)",
        imageUri = state.fotoPerfilUri,
        onUploadClick = {
            profilePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onDeleteClick = {
            onStateChange(state.copy(fotoPerfilUri = null))
        }
    )

    ReusableTextField(value = state.adreca, onValueChange = { onStateChange(state.copy(adreca = it)) }, label = stringResource(
        R.string.adre_a
    ))


    ExposedDropdownMenuBox(
        expanded = expadit,
        onExpandedChange = { expadit = !expadit }
    ) {
        OutlinedTextField(
            value = state.nacionalitat, onValueChange = {}, readOnly = true, label = { Text(
                stringResource(R.string.nacionalitat)
            ) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expadit) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(), shape = MaterialTheme.shapes.medium
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
        label = stringResource(R.string.email_usuari),
        placeholder = stringResource(R.string.email_example_com)
    )
    ReusableTextField(
        value = state.password,
        onValueChange = { onStateChange(state.copy(password = it)) },
        label = stringResource(R.string.contrasenya),
        placeholder = stringResource(R.string.contrasenya_m_n_6_car_cters),
        isPassword = true
    )
}