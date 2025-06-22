package org.fufu.spellbook.spell.presentation.spellDetail

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import org.fufu.spellbook.getDiceColorMap

sealed interface SpellTextThing {
    data class JustText(val text: String) : SpellTextThing
    data class Roll(val diceCount: Int?, val dieFaceCount: Int) : SpellTextThing
    // TODO: match these too, but it probably needs to come from a database of conditions
    //data class Condition(val conditionText: String): SpellTextThing
}

private val dieRollRegex: Regex = Regex(
    """(\d*)[dD](\d+)"""
)

fun tryParseRoll(match: MatchResult): SpellTextThing.Roll? {
    val diceCount = match.groups[1]?.value?.toIntOrNull()
    val dieFaceCount = match.groups[2]?.value?.toIntOrNull() ?: return null
    return SpellTextThing.Roll(diceCount, dieFaceCount)
}

// explode text into a sequence of simple text spans
// and rolls/other special detail elements
fun explodeSpellText(text: String): List<SpellTextThing> {
    fun recurse(remainingText: String) : List<SpellTextThing?> {
        if(remainingText.isEmpty()){
            return emptyList()
        }
        val match = dieRollRegex.find(remainingText)
            ?: return listOf(SpellTextThing.JustText(remainingText))
        val resultingRoll = tryParseRoll(match)

        val prior = remainingText.substring(0, match.range.first).ifEmpty { null }
        val tail = remainingText.substring(match.range.last+1).ifEmpty { null }
        return listOf<SpellTextThing?>(
            prior?.let{ SpellTextThing.JustText(it) },
            resultingRoll
        ).plus(tail?.let { recurse(it) } ?: emptyList())
    }
    return recurse(text).filterNotNull()
}

@Composable
private fun AnnotatedString.Builder.buildFromRoll(roll: SpellTextThing.Roll) {
    val colorMap = getDiceColorMap()
    val color = colorMap.map[roll.dieFaceCount] ?: colorMap.default
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = color)){
        append("${roll.diceCount ?: ""}d${roll.dieFaceCount}")
    }
}

private fun cleanUpSpellText(text: String): String{
    return text
        .replace("\t", "  ")
        .replace("\r\n", "\n")
        //.replace("\n", "\n\n")
}

@Composable
fun SpellText(text: String){
    val things = explodeSpellText(cleanUpSpellText(text))
    Text(
        style = MaterialTheme.typography.bodyMedium,
        text = buildAnnotatedString {
            things.forEach {
                when(it){
                    is SpellTextThing.JustText -> append(it.text)
                    is SpellTextThing.Roll -> buildFromRoll(it)
                }
            }
        })
}