package org.fufu.spellbook.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
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
        val rotation by animateFloatAsState(if(expanded){180f}else{0f})
        Box{
            val colors = ButtonDefaults.buttonColors()
            Button(
                onClick = { expanded = true },
                content = buttonContent,
                colors = colors
            )
            Icon(
                rememberVectorPainter(Icons.Default.ArrowDropDown),
                tint = { colors.contentColor },
                "dropdown",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(y=1.dp)
                    .rotate(rotation)
            )
        }

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