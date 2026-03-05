package cat.copernic.appvehicles.client.ui.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.client.ui.viewmodel.EditProfileViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onLoggedOut: () -> Unit = {}
) {
    val vm: EditProfileViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    val pickPhoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        vm.onPhotoPicked(uri?.toString())
    }
    val pickDniImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        vm.onDniImagePicked(uri?.toString())
    }
    val pickLicenseImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        vm.onLicenseImagePicked(uri?.toString())
    }

    LaunchedEffect(Unit) { vm.loadProfile() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_profile_title)) },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = stringResource(R.string.logout)
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
            }

            state.errorKey?.let {
                Text(
                    text = stringResource(errorKeyToRes(it)),
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(8.dp))
            }

            state.messageKey?.let {
                Text(
                    text = stringResource(messageKeyToRes(it)),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
            }

            // Extra: componente imagen (foto cliente)
            Text(stringResource(R.string.profile_photo), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            AsyncImage(
                model = state.photoUri,
                contentDescription = stringResource(R.string.profile_photo),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { pickPhoto.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.pick_profile_photo))
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.nomComplet,
                onValueChange = { vm.onFieldChange(nomComplet = it) },
                label = { Text(stringResource(R.string.full_name_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { },
                label = { Text(stringResource(R.string.email_readonly)) },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.telefon,
                onValueChange = { vm.onFieldChange(telefon = it) },
                label = { Text(stringResource(R.string.phone_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.adreca,
                onValueChange = { vm.onFieldChange(adreca = it) },
                label = { Text(stringResource(R.string.address_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Enunciado: nacionalidad debería ser desplegable traducido (lo dejo como texto por ahora).
            OutlinedTextField(
                value = state.nacionalitat,
                onValueChange = { vm.onFieldChange(nacionalitat = it) },
                label = { Text(stringResource(R.string.nationality_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.documentation), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.dataCaducitatDni,
                onValueChange = { vm.onFieldChange(dataCaducitatDni = it) },
                label = { Text(stringResource(R.string.id_expiry_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Text(stringResource(R.string.id_image), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model = state.dniImageUri,
                contentDescription = stringResource(R.string.id_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { pickDniImage.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.pick_id_image))
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.tipusCarnetConduir,
                onValueChange = { vm.onFieldChange(tipusCarnetConduir = it) },
                label = { Text(stringResource(R.string.license_type_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.dataCaducitatCarnet,
                onValueChange = { vm.onFieldChange(dataCaducitatCarnet = it) },
                label = { Text(stringResource(R.string.license_expiry_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Text(stringResource(R.string.license_image), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model = state.licenseImageUri,
                contentDescription = stringResource(R.string.license_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { pickLicenseImage.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.pick_license_image))
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.payment), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.numeroTargetaCredit,
                onValueChange = { vm.onFieldChange(numeroTargetaCredit = it) },
                label = { Text(stringResource(R.string.credit_card_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { vm.saveChanges() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text(stringResource(R.string.save_changes))
            }
        }
    }

    // Extra: logout confirm dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout_confirm_title)) },
            text = { Text(stringResource(R.string.logout_confirm_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        vm.logout()
                        onLoggedOut()
                    }
                ) { Text(stringResource(R.string.logout)) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

private fun errorKeyToRes(key: String): Int = when (key) {
    "session_missing_dni" -> R.string.error_session_missing_dni
    "full_name_required" -> R.string.error_full_name_required
    "profile_load_error" -> R.string.error_profile_load
    "profile_save_error" -> R.string.error_profile_save
    else -> R.string.error_generic
}

private fun messageKeyToRes(key: String): Int = when (key) {
    "profile_saved" -> R.string.profile_saved
    else -> R.string.profile_saved
}