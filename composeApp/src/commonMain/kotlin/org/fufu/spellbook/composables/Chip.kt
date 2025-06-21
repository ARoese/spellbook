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
    contentColor: Color = Color.Black,
    size: ChipSize = ChipSize.REGULAR,
    fillColor: Color = Color.LightGray,
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
    val actualColor = if(!enabled){
        Color.LightGray
    } else if (prepared) {
        Color.Blue
    } else {
        Color.Black
    }
    Chip(
        "Prepared",
        actualColor,
        size,
        Color.Transparent,
        BorderStroke(2.dp, actualColor)
    )
}

@Composable
fun KnownToken(
    known: Boolean,
    size: ChipSize = ChipSize.REGULAR
){
    val actualColor = if (known) Color.Blue else Color.Black
    Chip(
        "Known",
        actualColor,
        size,
        Color.Transparent,
        BorderStroke(2.dp, actualColor)
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
    Chip(tag, Color.Black, size, fillColor = Color.LightGray)
}