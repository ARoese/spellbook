package org.fufu.spellbook

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.fufu.spellbook.settings.DarkModePreference
import org.fufu.spellbook.settings.getPreferencesIsDarkMode

data class DiceColorMap(
    val map: Map<Int, Color>,
    val default: Color
)

@Composable
fun getDiceColorMap(): DiceColorMap {
    val lightDiceColorMap = DiceColorMap(
        mapOf(
            4 to Color(0xFF000080), // blue
            6 to Color(0xFF800000), // red
            8 to Color(0xFF804480), // purple
            12 to Color(0xFF226022), // green
            //20 to Color(0xFF000000), // dark yellow
        ),
        MaterialTheme.colorScheme.onSurface
    )

    val darkDiceColorMap = DiceColorMap(
        mapOf(
            4 to Color(0xFF6984D8), // blue
            6 to Color(0xFFBE4540), // red
            8 to Color(0xFF804480), // purple
            12 to Color(0xFF34A034), // green
            //20 to Color(0xFF000000), // dark yellow
        ),
        MaterialTheme.colorScheme.onSurface
    )

    return if (usingDarkTheme()) darkDiceColorMap else lightDiceColorMap
}

@Composable
fun usingDarkTheme(): Boolean {
    return when(getPreferencesIsDarkMode()){
        DarkModePreference.DARK -> true
        DarkModePreference.SYSTEM, null -> isSystemInDarkTheme()
        DarkModePreference.LIGHT -> false
    }
}

@Composable
fun WithCustomTheme(content: @Composable () -> Unit){
    val lightColors = lightColorScheme(
        secondaryContainer = Color(0xFFD7D7D7)
    )

    val darkColors = darkColorScheme(
        //primary = Color(0xFF66ffc7)
    )

    val colorScheme = if (usingDarkTheme()) darkColors else lightColors
    val typography = Typography()
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography
    ){
        content()
    }
}