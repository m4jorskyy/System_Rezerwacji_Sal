package com.example.rezerwacje.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rezerwacje.R
import com.example.rezerwacje.data.model.RegisterRequest
import com.example.rezerwacje.ui.navigation.Screen
import com.example.rezerwacje.ui.theme.RezerwacjeTheme
import com.example.rezerwacje.ui.viewmodel.RegisterState
import com.example.rezerwacje.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val viewModel: RegisterViewModel = viewModel()
    val registerState by viewModel.registerState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is RegisterState.Error -> {
                viewModel.emitUiMessage(state.message)
                viewModel.resetState()
            }

            is RegisterState.Success -> {
                viewModel.emitUiMessage("Registration successful")
                viewModel.resetState()
                navController.navigate(Screen.LOGIN.route)
            }

            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    text = stringResource(R.string.register_text),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                )

                OutlinedTextField(
                    value = username,
                    label = {
                        Text(
                            stringResource(R.string.username_text),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    },
                    onValueChange = { username = it },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = lastname,
                    label = {
                        Text(
                            stringResource(R.string.lastname_text),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    },
                    onValueChange = { lastname = it },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = email,
                    label = {
                        Text(
                            stringResource(R.string.email_text),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    },
                    onValueChange = { email = it },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = password,
                    label = {
                        Text(
                            stringResource(R.string.password_text),
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                        )
                    },
                    onValueChange = { password = it },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        viewModel.register(
                            RegisterRequest(
                                username,
                                lastname,
                                email,
                                password
                            )
                        )
                    },
                    enabled = username.isNotBlank() && lastname.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                    modifier = Modifier
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        stringResource(R.string.register_text),
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (registerState is RegisterState.Loading) {
                    CircularProgressIndicator()
                }
            }

            Button(
                onClick = { navController.navigate(Screen.HOME.route) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    stringResource(R.string.back_text),
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RezerwacjeTheme {
        val navController = rememberNavController()
        RegisterScreen(navController = navController)
    }
}