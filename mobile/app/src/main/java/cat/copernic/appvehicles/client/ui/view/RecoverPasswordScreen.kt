package cat.copernic.appvehicles.client.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.client.ui.viewmodel.RecoverPasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverPasswordScreen(
    onBackClick: () -> Unit = {}
) {
    val vm: RecoverPasswordViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.recover_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = stringResource(R.string.recover_subtitle),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.recover_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = vm::onEmailChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.email_label)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    state.errorKey?.let {
                        Text(
                            text = stringResource(errorKeyToRes(it)),
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    state.successKey?.let {
                        Text(
                            text = stringResource(successKeyToRes(it)),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Button(
                        onClick = vm::onSendClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.email.isNotBlank()
                    ) {
                        Text(stringResource(R.string.recover_action))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun errorKeyToRes(key: String): Int = when (key) {
    "email_required" -> R.string.error_email_required
    "email_invalid" -> R.string.error_email_invalid
    else -> R.string.error_generic
}

private fun successKeyToRes(key: String): Int = when (key) {
    "recover_sent" -> R.string.recover_success
    else -> R.string.recover_success
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun RecoverPasswordScreenPreview() {
    MaterialTheme {
        RecoverPasswordScreen()
    }
}