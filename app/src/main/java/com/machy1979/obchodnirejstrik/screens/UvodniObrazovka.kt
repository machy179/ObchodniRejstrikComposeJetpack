package com.machy1979.obchodnirejstrik.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.machy1979.obchodnirejstrik.screens.components.CustomButton
import com.machy1979.obchodnirejstrik.ui.theme.*
import com.machy1979.obchodnirejstrik.viewmodel.ObchodniRejstrikViewModel

@Composable
fun UvodniObrazovka(
    viewModel: ObchodniRejstrikViewModel,
    onSelectionChanged: (String) -> Unit = {},
    onCancelButtonClicked: () -> Unit = {},
    hledejDleIcoButton: (String) -> Unit = {},
    hledejDleNazvuButton: (String) -> Unit = {},
    modifier: Modifier = Modifier
){
    var selectedValue by rememberSaveable { mutableStateOf("") }
    val dotaz = remember { mutableStateOf(TextFieldValue()) }




    Column (
        modifier = modifier.padding(VelikostPaddingHlavnihoOkna).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){

        Card(
            elevation = VelikostElevation,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp),
            shape = RoundedCornerShape(VelikostZakulaceniRohuButtonTextField ),

        ) {
            OutlinedTextField(
                value = dotaz.value,
                onValueChange = {
                    dotaz.value = it                            },
                label = { Text("ICO nebo název subjektu") },
                modifier = Modifier
                    .padding(PaddingVButtonu)
                    .fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = com.machy1979.obchodnirejstrik.R.drawable.search_icon),
                        contentDescription = "Search"
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    disabledTextColor = Color.Transparent,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
        }

        CustomButton("Načíst dle ICO", false, true,
            onClick = {
                hledejDleIcoButton(dotaz.value.text)
            }
        )

        CustomButton("Načíst dle názvu", false,true,
            onClick = {
                viewModel.vynulujCompanysData()
                hledejDleNazvuButton(dotaz.value.text)
            }
        )

    }
}

@Preview
@Composable
fun SelectOptionPreview() {
    UvodniObrazovka(ObchodniRejstrikViewModel())
}