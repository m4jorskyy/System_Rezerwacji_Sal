package com.example.rezerwacje.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rezerwacje.data.model.RoomDataModel

@Composable
fun RoomCard(
    room: RoomDataModel,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null, // Kliknięcie w kartę (wybór)
    onEdit: (() -> Unit)? = null,  // Kliknięcie w edycję (menu)
    onDelete: (() -> Unit)? = null // Kliknięcie w usuwanie (menu)
) {
    var expanded by remember { mutableStateOf(false) }
    val isEditable = onEdit != null && onDelete != null

    // Kolory i obramowanie w zależności od wyboru
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier), // Obsługa kliknięcia całej karty
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            // --- TREŚĆ KARTY ---
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(end = if (isEditable) 24.dp else 0.dp)
            ) {
                // Górny rząd: Nazwa i Pojemność
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = room.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small,
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Text(
                            text = "Capacity: ${room.capacity}",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.DarkGray
                        )
                    }
                }

                // Info o budynku
                Text(
                    text = "Building: ${room.building} • Floor: ${room.floor}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tagi wyposażenia (Chips)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (room.whiteboard) EquipmentChip("Whiteboard")
                    if (room.projector) EquipmentChip("Projector")
                    if (room.desks) EquipmentChip("Desks")

                    if (!room.whiteboard && !room.projector && !room.desks) {
                        Text(
                            text = "No equipment",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }

            // --- MENU (Jeśli edytowalne) ---
            if (isEditable) {
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    IconButton(
                        onClick = { expanded = true }
                        // W Compose IconButton automatycznie przechwytuje kliknięcie,
                        // więc nie "przebije się" do onClick karty (nie zaznaczy jej).
                        // To działa jak e.stopPropagation()
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                expanded = false
                                onEdit?.invoke()
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                expanded = false
                                onDelete?.invoke()
                            }
                        )
                    }
                }
            }
        }
    }
}

// Pomocniczy Chip do wyświetlania wyposażenia
@Composable
fun EquipmentChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}