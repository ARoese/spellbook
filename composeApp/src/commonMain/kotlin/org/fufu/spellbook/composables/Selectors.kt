package org.fufu.spellbook.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun <T>DropdownSelector(
    options: List<T>,
    selected: Set<T>? = null,
    optionPresenter: @Composable (T) -> Unit,
    onOptionPicked: (T)-> Unit= {},
    singleSelect: Boolean = false,
    buttonContent: @Composable RowScope.() -> Unit,
){
    Box {
        var expanded by remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()
        Button(
            onClick = { expanded = true },
            content = buttonContent
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            scrollState = scrollState
        ) {
            options.forEach {
                val background = if (it in (selected ?: emptySet())) {
                    Color.LightGray
                } else {
                    Color.Unspecified
                }
                DropdownMenuItem(
                    modifier = Modifier.background(background),
                    text = { optionPresenter(it) },
                    onClick = {
                        if (singleSelect) {
                            expanded = false
                        }
                        onOptionPicked(it)
                    }
                )
            }
        }
    }
}

@Composable
fun BooleanSelector(
    state: Boolean?,
    onChange: (Boolean?) -> Unit,
    content: @Composable RowScope.() -> Unit
){
    val lime = Color(0xFF64FF64)
    val cherry = Color(0xFFFF6464)
    val unspecified = Color.Unspecified
    val bgColor = state?.let{if(it){lime}else{cherry} } ?: unspecified
    fun nextState() : Boolean? {
        return when(state){
            true -> false
            false -> null
            null -> true
        }
    }
    val buttonColors = ButtonDefaults.buttonColors(containerColor = bgColor)
    OutlinedButton(
        onClick = { onChange(nextState()) },
        content = content,
        colors = buttonColors
    )
}