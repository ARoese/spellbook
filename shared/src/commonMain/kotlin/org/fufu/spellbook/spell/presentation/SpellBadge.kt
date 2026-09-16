package org.fufu.spellbook.spell.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt
import kotlin.sequences.Sequence


enum class MaterialCost {
    GOLD,
    SILVER,
    COPPER,
    NONE;

    public fun color(): Color? {
        return when( this ) {
            GOLD -> Color(0xDB, 0xB1, 0x7, 0xFF)
            SILVER -> Color(0xAF, 0xAF, 0xAF, 0xFF)
            COPPER -> Color(184, 115, 51)
            NONE -> null
        }
    }
}

enum class MagicDamageType {
    ACID,
    COLD,
    FIRE,
    FORCE,
    LIGHTNING,
    NECROTIC,
    POISON,
    PSYCHIC,
    RADIANT,
    THUNDER
}

data class ParsedComponents (
    val hasVerbal: Boolean,
    val hasSomatic: Boolean,
    val hasMaterial: Boolean,
    val materialCost: MaterialCost
)

fun parseComponents(str: String): ParsedComponents {
    var hasVerbal = false
    var hasSomatic = false
    var hasMaterial = false
    var material = MaterialCost.NONE

    // effectively, a handwritten parser for the regular language described by
    // `([VSM\s][\s,]*){0,3}\s*[(](.*)[)]?`
    // ex: V,S,M (A jade worth at least 5gp)
    val parts = str.split('(', limit = 2)
    parts.getOrNull(0)?.let { components ->
        if(components.contains('V')) {
            hasVerbal = true
        }
        if(components.contains('S')) {
            hasSomatic = true
        }
        if(components.contains('M')) {
            hasMaterial = true
        }
    }

    fun findValueTag(str: String, tag: String): Boolean {
        if(tag.isEmpty()) {
            return false
        }

        var location = 0
        while(true) {
            location = str.indexOf(tag, startIndex = location, ignoreCase = true)
            if(location == -1) {
                return false
            }

            // validity checks.
            // The match should be preceded by that start of string,
            // or allowable characters
            val before = str.getOrNull(location-1)
            val after = str.getOrNull(location+tag.length)
            location += tag.length

            fun unacceptable(c: Char?): Boolean {
                if(c == null) {
                    return true
                }
                val unacceptableCategories = CharCategory.entries
                    .filter { it.name.contains("LETTER")}
                return unacceptableCategories
                    .any { it.contains(c) }
            }

            if(unacceptable(before) || unacceptable(after)) {
                continue
            }

            return true
        }
    }

    parts.getOrNull(1)?.let { specifier ->
        if(findValueTag(specifier, "cp")) {
            material = MaterialCost.COPPER
        }
        if(findValueTag(specifier, "sp")) {
            material = MaterialCost.SILVER
        }
        if(findValueTag(specifier, "gp")) {
            material = MaterialCost.GOLD
        }
        if(findValueTag(specifier, "pp")) {
            material = MaterialCost.GOLD
        }
    }

    if(material != MaterialCost.NONE) {
        hasMaterial = true
    }

    return ParsedComponents (
        hasVerbal,
        hasSomatic,
        hasMaterial,
        material
    )
}


/**
 * Hazard diamond-themed spell info badge.
 *                 Somatic
 *             Verbal + Material
 *                Damage Type
 *
 * If it has a verbal component, there is a V in the left diamond
 * If it has a somatic component, there is an S in the top diamond
 * If it has a Material component, the right diamond has an M.
 *  - If there is a coin cost, the background is colored to match gold, silver, copper, respectively
 * Bottom diamond is colored to match the damage type(s). It is white if there is none.
 * If the spell is a ritual spell, the ritual symbol (or an R) will appear in that bottom diamond
 */
@Composable
fun SpellBadge(
    components: ParsedComponents,
    isRitual: Boolean,
    modifier: Modifier = Modifier,
) {
    val damages: Set<MagicDamageType> = setOf(MagicDamageType.FIRE) // TODO: this
    //val lineColor = MaterialTheme.colorScheme.onPrimaryContainer
    val lineColor = Color.Black
    // for some inexplicable reason, setting the textstyle to a default textyle
    // prevents text from being offset slightly downward at smaller scales
    val textStyle = TextStyle()
    // this offset is meant to help deal with the text offset issue
    val innerRotation = Modifier.fillMaxSize().rotate(-45f).offset((-0.5).dp, (-1).dp)//.scale(sqrt(0.5f))
    val autoSize = TextAutoSize.StepBased()
    val outerRotation = Modifier
        .rotate(45.0f)
        .scale(sqrt(0.5f))

    val textColor = Color.Black
    val badgeBlue = Color(71, 121, 212, 255)
    val badgeRed = Color(222, 61, 61, 255)
    val badgeYellow = Color(236, 236, 79, 255)
    val badgeWhite = Color(255,255,255)

    Box(modifier.aspectRatio(1.0f)) {
        Row(outerRotation.background(lineColor)) {
            val innerBoxModifier = Modifier.aspectRatio(1.0f)
                .border(2.dp, color=lineColor)
                .padding(2.dp)
                //.width(10.dp)
                .weight(1f)
            Column(Modifier.weight(1f).fillMaxWidth()) {
                val innerBoxModifier = innerBoxModifier.weight(1f).fillMaxHeight()
                // Top diamond
                Box(innerBoxModifier
                    .background(
                        badgeRed
                    )
                ) {
                    components.materialCost.color()?.let {
                        Box(Modifier
                            .aspectRatio(1.0f)
                            .scale(0.6f)
                            .background(
                                it
                            )
                        )
                    }
                    Box(innerRotation, contentAlignment = Alignment.Center) {
                        if(components.hasMaterial){
                            Text("M", autoSize = autoSize, color = textColor, fontWeight = FontWeight.Bold, style = textStyle)
                        }
                    }
                }
                // Left diamond
                Box(innerBoxModifier.background(badgeBlue)) {
                    Box(innerRotation, contentAlignment = Alignment.Center) {
                        if(components.hasVerbal) {
                            Text("V", autoSize = autoSize, color = textColor, fontWeight = FontWeight.Bold, style = textStyle)
                        }
                    }
                }
            }
            Column(Modifier.weight(1f).fillMaxWidth()) {
                val innerBoxModifier = innerBoxModifier.weight(1f).fillMaxHeight()
                // Right diamond
                Box(innerBoxModifier.background(badgeYellow)) {
                    Box(innerRotation, contentAlignment = Alignment.Center) {
                        if(components.hasSomatic) {
                            Text("S", autoSize = autoSize, color = textColor, fontWeight = FontWeight.Bold, style = textStyle)
                        }
                    }
                }
                // Bottom diamond
                Box(innerBoxModifier.background(badgeWhite)) {
                    /*
                    components.materialCost.color()?.let {
                        Box(Modifier
                            .aspectRatio(1.0f)
                            .scale(0.6f)
                            .background(
                                it
                            )
                        )
                    }
                     */
                    Box(innerRotation, contentAlignment = Alignment.Center) {
                        if(isRitual) {
                            Text("R", autoSize = autoSize, color = textColor, fontWeight = FontWeight.Bold, style = textStyle)
                        }
                    }
                }
            }
        }
    }
}

private data class SpellBadgeArgs(
    val c: ParsedComponents,
    val r: Boolean
)

private class SpellBadgePreviewProvider : PreviewParameterProvider<SpellBadgeArgs> {
    val components: Sequence<ParsedComponents> = sequenceOf(
        "V, S, M (A jade worth at least 50gp)",
        "V, M (A jade worth at least 50 sp)",
        "S, M (A jade worth at least 50 cp)",
        "V, S, M (A bit of phosphorus or wychwood, or a glowworm)",
        "V, S, M",
        "S, M",
        "V, S"
    ).map { parseComponents(it) }

    val ritual: Sequence<Boolean> = generateSequence {
        listOf(true, false, false).random()
    }

    override val values: Sequence<SpellBadgeArgs> = components.zip(ritual).map { (c,r) ->
        SpellBadgeArgs(c,r)
    }
}

@Composable
@Preview
private fun SpellBadgePreview(
    @PreviewParameter(SpellBadgePreviewProvider::class)
    args: SpellBadgeArgs
) {
    SpellBadge(
        args.c,
        args.r
    )
}