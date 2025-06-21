package org.fufu.spellbook

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun WithCustomTheme(content: @Composable () -> Unit){
    /*
    val lightColors = MaterialTheme.colors.copy(
        primary = Color(0xFF1EB980)
    )

    val darkColors = MaterialTheme.colors.copy(
        primary = Color(0xFF66ffc7)
    )

    val colors = if (isSystemInDarkTheme()) darkColors else lightColors
    val typography = Typography(
        h1 = TextStyle(
            fontWeight = FontWeight.W100,
            fontSize = 96.sp
        ),
        button = TextStyle(
            fontWeight = FontWeight.W600,
            fontSize = 14.sp
        )
    )
     */
    MaterialTheme(
        //colors = colors,
        //typography = typography
    ){
        content()
    }
}