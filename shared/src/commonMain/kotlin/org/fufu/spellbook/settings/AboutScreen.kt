package org.fufu.spellbook.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.regular.File
import org.fufu.spellbook.SharedBuildKonfig

class NullProvider : PreviewParameterProvider<() -> Unit> {
    override val values: Sequence<() -> Unit> = sequenceOf{}
}

@Composable
@Preview
fun AboutScreen(
    @PreviewParameter(NullProvider::class)
    onBack: () -> Unit
){
    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth()){
                Row(modifier = Modifier.align(Alignment.CenterEnd)){
                    IconButton(
                        onClick = { onBack() },
                    ) {
                        Icon(Icons.Filled.Close, "Close")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ){
            Text("SpellBook", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            val buildVersion = SharedBuildKonfig.buildVersion
            Text("Version: $buildVersion", style = MaterialTheme.typography.labelLarge)
            val uriHandler = LocalUriHandler.current

            Button(
                onClick = {
                    uriHandler.openUri(SharedBuildKonfig.spellbookGithubLink)
                }
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Brands.Github,
                    "Github Icon",
                    Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text("Source code")
            }

            Spacer(Modifier.height(40.dp))
            Text("5e SRD", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("5e SRD data source is the 5e-bits/5e-database. " +
                    "The data is licensed under the OPEN GAME LICENSE Version 1.0a.")
            Button(
                onClick = {
                    uriHandler.openUri("https://github.com/5e-bits/5e-database")
                }
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Brands.Github,
                    "Github Icon",
                    Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text("5e-database")
            }
            Button(
                onClick = {
                    uriHandler.openUri("https://github.com/5e-bits/5e-database")
                }
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Regular.File,
                    "File icon",
                    Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text("SRD License")
            }
        }
    }
}