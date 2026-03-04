package cat.copernic.appvehicles.usuariAnonim.ui.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
// AQUESTS DOS IMPORTS SÓN LA CLAU PER SOLUCIONAR L'ERROR DE "expadit":
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cat.copernic.appvehicles.usuariAnonim.ui.viewmodel.RegisterViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class RegisterUiState(
    val nomComplet: String = "",
    val numeroIdentificacio: String = "",
    val dataCaducitatId: String = "",
    val tipusLlicencia: String = "",
    val dataCaducitatLlicencia: String = "",
    val numeroTargetaCredit: String = "",
    val adreca: String = "",
    val nacionalitat: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val fotoIdentificacioUri: String? = null,
    val fotoLlicenciaUri: String? = null
)

// --- COMPONENTS REUTILITZABLES ---

@Composable
fun ReusableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    placeholder: String? = null // NUEVO: Parámetro opcional para la pista
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        // NUEVO: Si le pasamos un placeholder, lo dibuja dentro del campo
        placeholder = placeholder?.let { { Text(it) } },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun ImageUploadButton(label: String, isUploaded: Boolean = false, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        color = if (isUploaded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isUploaded) Icons.Default.CheckCircle else Icons.Default.AddAPhoto,
                contentDescription = "Pujar $label",
                tint = if (isUploaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isUploaded) "Foto seleccionada!" else label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUploaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isUploaded) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// --- PANTALLA PRINCIPAL ---

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val context = androidx.compose.ui.platform.LocalContext.current

    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 3

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registre - Pas $currentStep de $totalSteps") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar enrere")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(onClick = { currentStep-- }, enabled = !uiState.isLoading) {
                            Text("Enrere")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (currentStep < totalSteps) {
                        Button(
                            onClick = {
                                val llistaErrors = mutableListOf<String>()
                                val regexNom = "^[a-zA-ZÀ-ÿ\\s]+$".toRegex()
                                val regexData = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()

                                if (currentStep == 1) {
                                    if (uiState.nomComplet.isBlank()) llistaErrors.add("• El nom complet no pot estar buit.")
                                    else if (!uiState.nomComplet.matches(regexNom)) llistaErrors.add("• El nom només pot contenir lletres i espais.")

                                    if (uiState.numeroIdentificacio.isBlank()) llistaErrors.add("• El número d'identificació no pot estar buit.")

                                    if (!uiState.dataCaducitatId.matches(regexData)) {
                                        llistaErrors.add("• Format de data incorrecte (Usa YYYY-MM-DD).")
                                    } else {
                                        try {
                                            val dataParsed = java.time.LocalDate.parse(uiState.dataCaducitatId)
                                            if (dataParsed.isBefore(java.time.LocalDate.now())) llistaErrors.add("• La data de caducitat no pot ser passada.")
                                        } catch (e: Exception) {
                                            llistaErrors.add("• Data invàlida.")
                                        }
                                    }
                                } else if (currentStep == 2) {
                                    if (uiState.adreca.isBlank()) llistaErrors.add("• L'adreça no pot estar buida.")
                                    if (uiState.nacionalitat.isBlank()) llistaErrors.add("• La nacionalitat no pot estar buida.")

                                    if (uiState.email.isBlank()) llistaErrors.add("• L'email no pot estar buit.")
                                    else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()) llistaErrors.add("• Format d'email incorrecte.")

                                    if (uiState.password.isBlank()) llistaErrors.add("• La contrasenya no pot estar buida.")
                                }

                                if (llistaErrors.isNotEmpty()) {
                                    val missatgeFinal = llistaErrors.joinToString(separator = "\n")
                                    viewModel.updateState(uiState.copy(errorMessage = missatgeFinal))
                                } else {
                                    viewModel.updateState(uiState.copy(errorMessage = null))
                                    currentStep++
                                }
                            },
                            enabled = !uiState.isLoading
                        ) {
                            Text("Següent")
                        }
                    } else {
                        // VALIDACIONS DEL PAS 3 ABANS D'ENVIAR
                        Button(
                            onClick = {
                                val llistaErrors = mutableListOf<String>()
                                val regexData = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()

                                if (uiState.tipusLlicencia.isBlank()) llistaErrors.add("• El tipus de llicència no pot estar buit.")

                                if (!uiState.dataCaducitatLlicencia.matches(regexData)) {
                                    llistaErrors.add("• Format de data incorrecte (Usa YYYY-MM-DD).")
                                } else {
                                    try {
                                        val dataParsed = java.time.LocalDate.parse(uiState.dataCaducitatLlicencia)
                                        if (dataParsed.isBefore(java.time.LocalDate.now())) llistaErrors.add("• La llicència no pot estar caducada.")
                                    } catch (e: Exception) {
                                        llistaErrors.add("• Data invàlida.")
                                    }
                                }

                                // Validación de la tarjeta (Solo números y entre 13 y 19 dígitos)
                                val regexTargeta = "^[0-9]{13,19}$".toRegex()

                                if (uiState.numeroTargetaCredit.isBlank()) {
                                    llistaErrors.add("• La targeta de crèdit no pot estar buida.")
                                } else if (!uiState.numeroTargetaCredit.matches(regexTargeta)) {
                                    llistaErrors.add("• La targeta ha de tenir entre 13 i 19 números.")
                                }

                                if (llistaErrors.isNotEmpty()) {
                                    val missatgeFinal = llistaErrors.joinToString(separator = "\n")
                                    viewModel.updateState(uiState.copy(errorMessage = missatgeFinal))
                                } else {
                                    viewModel.updateState(uiState.copy(errorMessage = null))
                                    viewModel.register(context) // Tot correcte, enviem al servidor!
                                }
                            },
                            enabled = !uiState.isLoading
                        ) {
                            Text("Finalitzar")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp).verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            when (currentStep) {
                1 -> Pas1DadesPersonals(uiState) { viewModel.updateState(it) }
                2 -> Pas3DadesContacte(uiState) { viewModel.updateState(it) }
                3 -> Pas2DadesConduccio(uiState) { viewModel.updateState(it) }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- PASSOS DEL FORMULARI ---

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
        placeholder = "email@example.com" // AQUÍ ESTÁ EL PLACEHOLDER
    )
    ReusableTextField(
        value = state.password,
        onValueChange = { onStateChange(state.copy(password = it)) },
        label = "Contrasenya",
        placeholder = "Contrasenya (mín 6 caràcters)", // AQUÍ ESTÁ EL PLACEHOLDER
        isPassword = true
    )}

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
    // Asegúrate de tener estos imports arriba del archivo si no los tienes:
    // import androidx.compose.foundation.text.KeyboardOptions
    // import androidx.compose.ui.text.input.KeyboardType

    OutlinedTextField(
        value = state.numeroTargetaCredit,
        onValueChange = { text ->
            // Filtramos para que solo entren números (nada de letras ni símbolos)
            val nomesNumeros = text.filter { it.isDigit() }
            // Limitamos a 19 caracteres máximo (el tope internacional)
            if (nomesNumeros.length <= 19) {
                onStateChange(state.copy(numeroTargetaCredit = nomesNumeros))
            }
        },
        label = { Text("Targeta de crèdit") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        // Esto abre automáticamente el teclado numérico del móvil
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = MaterialTheme.shapes.medium
    )
}