package org.fufu.spellbook.spell.presentation.spellDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

data class EditableValue<T>(val value: T, val onChange: (T)->Unit)

@Composable
fun DisplayBlock(
    datums: Map<String, String>,
    modifier: Modifier = Modifier
){
    Row(modifier = modifier){
        Column(horizontalAlignment = Alignment.End){
            datums.keys
                .map{"$it: "}
                .forEach {
                    Text(
                        it,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }
        }
        Column{
            datums.values.forEach{
                Text(it)
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