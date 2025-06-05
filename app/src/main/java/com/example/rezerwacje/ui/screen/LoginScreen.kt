// LoginScreen.kt
package com.example.rezerwacje.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.R
import com.example.rezerwacje.data.model.LoginRequest
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.theme.AppDefaults
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import com.example.rezerwacje.ui.viewmodel.LoginState
import com.example.rezerwacje.ui.viewmodel.LoginViewModel
import com.example.rezerwacje.ui.viewmodel.LoginViewModelFactory
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(context))
    val loginState by viewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginState.Error -> {
                viewModel.emitUiMessage(state.message)
                viewModel.resetState()
            }

            is LoginState.Success -> {
                viewModel.emitUiMessage(context.getString(R.string.login_success))
                viewModel.resetState()
                navController.navigate(Screen.RESERVATION.route)
            }

            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.login_text),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily

                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            stringResource(R.string.login_text),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = AppDefaults.textFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            stringResource(R.string.password_text),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = AppDefaults.textFieldColors()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.login(LoginRequest(email, password))
                    },
                    enabled = email.isNotEmpty() && password.isNotEmpty(),
                    modifier = Modifier
                        .height(50.dp),
                    colors = AppDefaults.buttonColors(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        stringResource(R.string.login_text),
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                    )
                }


                if (loginState is LoginState.Loading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }



            Button(
                onClick = { navController.navigate(Screen.HOME.route) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = AppDefaults.buttonColors(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    stringResource(
                        R.string.back_text
                    ),
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    RezerwacjeTheme {
        val navController = rememberNavController()
        LoginScreen(navController = navController)
    }
}