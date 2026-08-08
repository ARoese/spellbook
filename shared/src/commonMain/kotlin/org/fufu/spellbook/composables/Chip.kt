package org.fufu.spellbook.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ChipSize {
    SMALL, REGULAR
}

@Composable
fun Chip(
    content: String,
    contentColor: Color = MaterialTheme.colorScheme.onSecondary,
    size: ChipSize = ChipSize.REGULAR,
    fillColor: Color = MaterialTheme.colorScheme.secondary,
    border : BorderStroke? = null
){
    Box(
        modifier = Modifier
            .widthIn(
                min = when (size) {
                    ChipSize.SMALL -> 50.dp
                    ChipSize.REGULAR -> 80.dp
                }
            )
            .clip(RoundedCornerShape(6.dp))
            .let { if (border != null) it.border(border = border) else it }
            .background(fillColor)
            .padding(
                vertical = 2.dp,
                horizontal = 4.dp
            )
    ) {
        Text(
            content,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
fun PreparedToken(
    prepared: Boolean,
    enabled: Boolean = true,
    size: ChipSize = ChipSize.REGULAR
){
    val (bg, textColor, outline) = if(enabled) {
        if(prepared){
            Triple(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary, MaterialTheme.colorScheme.primary)
        }else{
            Triple(Color.Transparent, MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.onSurface)
        }
    }
    else {
        Triple(Color.Transparent, MaterialTheme.colorScheme.onSurfaceVariant, Color.Transparent)
    }

    Chip(
        "Prepared",
        textColor,
        size,
        bg,
        BorderStroke(2.dp, outline)
    )
}

@Composable
fun KnownToken(
    known: Boolean,
    size: ChipSize = ChipSize.REGULAR
){
    val (bg, textColor, outline) = if(known){
            Triple(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary, MaterialTheme.colorScheme.primary)
        }else{
            Triple(Color.Transparent, MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.onSurface)
        }

    Chip(
        "Known",
        textColor,
        size,
        bg,
        BorderStroke(2.dp, outline)
    )
}

@Composable
fun ClickableToken(
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    token: @Composable () -> Unit
){
    Box(
        modifier = Modifier
            .clickable(enabled, onClick = onClick)
    ){
        token()
    }
}

@Composable
fun TagChip(
    tag: String,
    size: ChipSize = ChipSize.REGULAR
){
    Chip(tag, size=size)
}