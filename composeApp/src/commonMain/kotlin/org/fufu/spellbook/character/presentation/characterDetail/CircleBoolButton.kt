package org.fufu.spellbook.character.presentation.characterDetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp

@Composable
fun FilledOutlinedCircleShape(
    filledFraction: Float,
    size: Dp = 120.dp,
    outlineThickness: Dp = 20.dp,
    outlineColor: Color = Color.Black,
    fillColor: Color = Color.Cyan,
    backgroundColor: Color = Color.Blue
){
    val fillableSize = size - outlineThickness
    // +1 for overlap. Otherwise, anti-aliasing causes issues
    // this should probably be done by setting the background color to the fill color,
    // but this works and is more clear
    val internalFillSize = lerp(0.dp, fillableSize, filledFraction) + 1.dp
    Box(
        contentAlignment = Alignment.Center,
    ){
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(size)
                .background(outlineColor)
        ){}

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(size - outlineThickness)

                .background(backgroundColor)
        ){}

        if(internalFillSize > 2.dp){
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(internalFillSize)

                    .background(fillColor)
            ){}
        }
    }
}

@Composable
fun SimpleCircleBoolButton(
    state: Boolean,
    onClick: (Boolean) -> Unit,
    size: Dp
){
    val outlineThickness = lerp(0.dp, size, 0.1f)
    CircleBoolButton(
        state,
        onClick,
        true,
        size,
        outlineThickness,
        outlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        fillColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
}

@Composable
fun CircleBoolButton(
    state: Boolean,
    onClick: (Boolean) -> Unit,
    enabled: Boolean = true,
    size: Dp = 120.dp,
    outlineThickness: Dp = 20.dp,
    outlineColor: Color = Color.Black,
    fillColor: Color = Color.Black,
    backgroundColor: Color = Color.White
){
    val filledness = if(state){1.0f}else{0.0f}
    val animatedFill by animateFloatAsState(filledness)
    IconButton(
        {onClick(!state)},
        enabled = enabled,
        modifier = Modifier
            .width(size)
            .height(size)
    ){
        FilledOutlinedCircleShape(
            filledFraction = animatedFill,
            size = size,
            outlineThickness = outlineThickness,
            outlineColor = outlineColor,
            fillColor = fillColor,
            backgroundColor = backgroundColor
        )
    }
}