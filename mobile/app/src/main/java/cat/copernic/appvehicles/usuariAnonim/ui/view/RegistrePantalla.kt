package cat.copernic.appvehicles.usuariAnonim.ui.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cat.copernic.appvehicles.usuariAnonim.ui.viewmodel.RegisterViewModel

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
                                    viewModel.register(context)
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