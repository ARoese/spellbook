package org.fufu.spellbook.spell.presentation.spellDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sunnychung.lib.android.composabletable.ux.Table
import org.fufu.spellbook.horizontalScrollViaVerticalWheel

data class EditableValue<T>(val value: T, val onChange: (T)->Unit)

@Composable
fun DisplayBlock(
    datums: Map<String, String>,
    modifier: Modifier = Modifier
){
    val list = datums.toList()
    val scrollState = rememberScrollState()
    val crsc = rememberCoroutineScope()
    Table(
        rowCount = datums.size,
        columnCount = 2,
        modifier = modifier
            .requiredHeightIn(0.dp, 300.dp)
            .horizontalScrollViaVerticalWheel(scrollState, crsc),
        horizontalScrollState = scrollState
    ) { row, col ->
        val datum = list.getOrNull(row)
        if(datum != null){
            val (key,value) = datum;
            if (col == 0) {
                Text(
                    "$key:",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.wrapContentSize(Alignment.CenterEnd).padding(end=8.dp)
                )
            } else {
                Text(value)
            }
        }
    }

}

@Composable
fun EditableDataBlock(
    datums: Map<String, EditableValue<String>>,
    isEditing: Boolean = false,
    modifier: Modifier = Modifier
){
    if(isEditing){
        Column(modifier = modifier){
            datums.forEach{(key, value) ->
                Text(
                    "$key:",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
                TextField(
                    value.value,
                    onValueChange = value.onChange
                )
            }
        }

    }else{
        DisplayBlock(
            datums.mapValues{
                it.value.value
            },
            modifier = modifier
        )
    }
}

@Composable
fun EditableText(
    text: String,
    style: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    isEditing: Boolean = false,
    onChange: (String) -> Unit = {}
){
    if(isEditing){
        TextField(
            text,
            onValueChange = onChange
        )
    }else{
        Text(
            text,
            style = style,
            fontWeight = fontWeight,
            fontStyle = fontStyle
        )

    }
}