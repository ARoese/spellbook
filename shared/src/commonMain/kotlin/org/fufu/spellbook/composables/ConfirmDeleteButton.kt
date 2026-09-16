package org.fufu.spellbook.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun ConfirmDeleteButton(
    onDeleteClicked: () -> Unit
) {
    var isDialogOpen by remember { mutableStateOf(false) }

    IconButton(
        onClick = {isDialogOpen=true}
    ){
        Icon(Icons.Filled.Delete, "Delete")
    }

    if (isDialogOpen) {
        val confirmColors = ButtonDefaults.buttonColors().copy(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            title = { Text("Are you sure?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                Button(
                    colors = confirmColors,
                    onClick = {
                        onDeleteClicked()
                        isDialogOpen = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = { isDialogOpen = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}