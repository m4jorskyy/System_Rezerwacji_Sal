// Components.kt
package com.example.rezerwacje.ui.theme

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButtonDefaults
//import androidx.compose.material3.TextButtonDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AppDefaults {
    @Composable
    fun buttonColors() = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
        disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f)
    )

    @Composable
    fun textFieldColors() = TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
        disabledIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.outline,
        focusedPlaceholderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
    )

//    @Composable
//    fun outlinedButtonColors() = OutlinedButtonDefaults.outlinedButtonColors(
//        contentColor = MaterialTheme.colorScheme.primary,
//        disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
//        containerColor = Color.Transparent
//    )
//
//    @Composable
//    fun textButtonColors() = TextButtonDefaults.textButtonColors(
//        contentColor = MaterialTheme.colorScheme.primary,
//        disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
//    )
}