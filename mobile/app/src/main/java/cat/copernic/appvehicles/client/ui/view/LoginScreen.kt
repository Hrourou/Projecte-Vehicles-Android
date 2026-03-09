package cat.copernic.appvehicles.client.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.client.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    vm: LoginViewModel,
    onLoginSuccess: () -> Unit = {},
    onNavigateToRecover: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {

    val state by vm.uiState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoginSuccess()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.login_title)) }) }
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
                        text = stringResource(R.string.login_subtitle),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = vm::onEmailChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.email_label)) },
                        singleLine = true,
                        isError = state.emailError != null
                    )

                    state.emailError?.let {
                        Text(
                            text = stringResource(errorKeyToRes(it)),
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = vm::onPasswordChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.password_label)) },
                        singleLine = true,
                        isError = state.passwordError != null,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    stringResource(
                                        if (passwordVisible) R.string.hide else R.string.show
                                    )
                                )
                            }
                        }
                    )

                    state.passwordError?.let {
                        Text(
                            text = stringResource(errorKeyToRes(it)),
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    state.generalError?.let {
                        Text(
                            text = stringResource(errorKeyToRes(it)),
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Extras: links recuperar + registrar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = onNavigateToRecover) {
                            Text(stringResource(R.string.forgot_password))
                        }
                        TextButton(onClick = onNavigateToRegister) {
                            Text(stringResource(R.string.go_to_register))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            vm.onLoginClick()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.isLoginEnabled && !state.isLoading
                    ) {
                        Text(stringResource(R.string.login_action))
                    }

                    if (state.isLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
    "password_required" -> R.string.error_password_required
    "invalid_credentials" -> R.string.error_invalid_credentials
    else -> R.string.error_generic
}