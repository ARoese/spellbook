package org.fufu.spellbook.spell.presentation.spellDetail

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import org.fufu.spellbook.getDiceColorMap

sealed interface SpellTextThing {
    data class JustText(val text: String) : SpellTextThing
    data class Roll(val diceCount: Int?, val dieFaceCount: Int) : SpellTextThing
    data class InTextCondition(val text: String, val conditionName: String): SpellTextThing
}

private val dieRollRegex: Regex = Regex(
    """(\d*)[dD](\d+)"""
)

fun tryParseRoll(match: MatchResult): SpellTextThing.Roll? {
    val diceCount = match.groups[1]?.value?.toIntOrNull()
    val dieFaceCount = match.groups[2]?.value?.toIntOrNull() ?: return null
    return SpellTextThing.Roll(diceCount, dieFaceCount)
}

private fun explodeSpellTextForDice(text: String): List<SpellTextThing>{
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

data class Match(
    val matchStart: Int,
    val matchEnd: Int
)

data class StringMatch(
    val condition: String,
    val match: Match
)

private fun matchFirst(text: String, toMatch: String): Match? {
    if(toMatch.isEmpty()){
        return null
    }

    (0.. text.length - toMatch.length).forEach {
        val subs = text.substring(it, it+toMatch.length)
        if(subs == toMatch){
            return Match(it, it+toMatch.length)
        }
    }
    return null
}

private fun matchFirstIgnoreCase(text: String, toMatch: String): Match? {
    return matchFirst(text.lowercase(), toMatch.lowercase())
}

private fun matchAnyString(
    text: String,
    toMatch: Set<String>
): StringMatch? {
    return toMatch.mapNotNull {
        matchFirstIgnoreCase(text, it)?.let { match ->
            StringMatch(
                it, match
            )
        }
    }.minByOrNull { it.match.matchStart }
}

private fun explodeSpellTextForConditions(
    text: String,
    conditionNames: Set<String>
): List<SpellTextThing> {
    fun recurse(remainingText: String) : List<SpellTextThing?> {
        if(remainingText.isEmpty()){
            return emptyList()
        }

        val match = matchAnyString(remainingText, conditionNames)
            ?: return listOf(SpellTextThing.JustText(remainingText))


        val prior = remainingText.substring(0, match.match.matchStart).ifEmpty { null }
        val conditionText = remainingText.substring(
            match.match.matchStart,
            match.match.matchEnd
        )
        val tail = remainingText.substring(match.match.matchEnd).ifEmpty { null }
        return listOf<SpellTextThing?>(
            prior?.let{ SpellTextThing.JustText(it) },
            SpellTextThing.InTextCondition(conditionText, match.condition)
        ).plus(tail?.let { recurse(it) } ?: emptyList())
    }
    return recurse(text).filterNotNull()
}

// explode text into a sequence of simple text spans
// and rolls/other special detail elements
fun explodeSpellText(text: String, conditionNames: Set<String>): List<SpellTextThing> {
    return explodeSpellTextForDice(text).flatMap {
        if(it is SpellTextThing.JustText){
            explodeSpellTextForConditions(it.text, conditionNames)
        }else{
            listOf(it)
        }
    }
}

@Composable
private fun AnnotatedString.Builder.buildFromRoll(roll: SpellTextThing.Roll) {
    val colorMap = getDiceColorMap()
    val color = colorMap.map[roll.dieFaceCount] ?: colorMap.default
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = color)){
        append("${roll.diceCount ?: ""}d${roll.dieFaceCount}")
    }
}

@Composable
private fun AnnotatedString.Builder.buildFromCondition(
    condition: SpellTextThing.InTextCondition,
    onClick: (String) -> Unit
) {
    withStyle(
        style = SpanStyle(
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline
        )
    ){
        withLink(
            LinkAnnotation.Clickable(
                condition.conditionName,
                linkInteractionListener = {
                    onClick(condition.conditionName)
                }
            )
        ){
            append(condition.text)
        }
    }
}

private fun cleanUpSpellText(text: String): String{
    return text
        .replace("\t", "  ")
        .replace("\r\n", "\n")
        //.replace("\n", "\n\n")
}

@Composable
fun SpellText(
    text: String,
    conditionNames: Set<String>,
    onClickCondition: (String) -> Unit
){
    val things = explodeSpellText(cleanUpSpellText(text), conditionNames)
    Text(
        style = MaterialTheme.typography.bodyMedium,
        text = buildAnnotatedString {
            things.forEach {
                when(it){
                    is SpellTextThing.JustText -> append(it.text)
                    is SpellTextThing.Roll -> buildFromRoll(it)
                    is SpellTextThing.InTextCondition -> buildFromCondition(it, onClickCondition)
                }
            }
        })
}