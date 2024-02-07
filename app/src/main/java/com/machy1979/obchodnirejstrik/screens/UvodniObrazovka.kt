package com.machy1979.obchodnirejstrik.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.machy1979.obchodnirejstrik.screens.components.CustomButton
import com.machy1979.obchodnirejstrik.ui.theme.*
import com.machy1979.obchodnirejstrik.viewmodel.ObchodniRejstrikViewModel

@Composable
fun UvodniObrazovka(
    viewModel: ObchodniRejstrikViewModel,
    hledejDleIcoButton: (String) -> Unit = {},
    hledejDleNazvuButton: (Pair<String, String>) -> Unit = {},
    modifier: Modifier = Modifier
){
    val dotaz = remember { mutableStateOf(TextFieldValue()) }
    val dotazMesto = remember { mutableStateOf(TextFieldValue()) }


    Column (
        modifier = modifier
            .padding(VelikostPaddingHlavnihoOkna)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
// Fetching current app configuration

        val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        val paddingModifier = if (isLandscape) {
            Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp)
        } else {
            Modifier.padding(start = 20.dp, end = 20.dp, top = 100.dp, bottom = 10.dp)
        }
        Card(
            elevation = VelikostElevation,
            // modifier = Modifier
            //    .padding(horizontal = 20.dp, vertical = 70.dp),
            modifier = paddingModifier,
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



            var expanded by remember { mutableStateOf (false) }

        Column(
            modifier = Modifier.align(Alignment.Start) // Zarovnává kartu doleva
        ) {
            Card(
                elevation = VelikostElevation,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 20.dp)
                    .then(if (expanded) Modifier.fillMaxWidth() else Modifier)
                    .padding(PaddingVButtonu)
                    .clickable {
                        expanded = !expanded
                    },
                shape = RoundedCornerShape(VelikostZakulaceniRohuButtonTextField ),
            ) {
                Column(
                ) {
                    if (!expanded) {
                        Icon(
                            painter = painterResource(id = com.machy1979.obchodnirejstrik.R.drawable.expandabled_icon),
                            contentDescription = "Expand",
                            modifier = Modifier
                                .clickable { expanded = !expanded }
                                .padding(end = 8.dp) // Umožňuje umístit ikonu odstupcem od textového pole
                        )
/*                        Text(modifier = Modifier
                            .padding(VelikostPaddingCardNepovinneHodnoty),
                            text = "  +  ",
                            color = Color.Gray // Sets text color to gray
                        )*/
                    }

                    if (expanded) {
                        Icon(
                            painter = painterResource(id = com.machy1979.obchodnirejstrik.R.drawable.collapsed_icon),
                            contentDescription = "Collaps",
                            modifier = Modifier
                                .clickable { expanded = !expanded }
                                .padding(end = 8.dp) // Umožňuje umístit ikonu odstupcem od textového pole
                        )
                        OutlinedTextField(
                            value = dotazMesto.value,
                            onValueChange = {
                                dotazMesto.value = it
                                            },
                            label = { Text("Sídlo subjektu - nepovinné") },
                            modifier = Modifier
                                .padding(PaddingVButtonu)
                                .fillMaxWidth(),
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
                }
            }
        }

        CustomButton("Načíst dle ICO", false, true,
            onClick = {
                hledejDleIcoButton(dotaz.value.text)
            }
        )

        CustomButton("Načíst dle názvu", false,true,
            onClick = {
                viewModel.vynulujCompanysData()
                hledejDleNazvuButton(Pair(dotaz.value.text, dotazMesto.value.text))
            }
        )

    }
}

@Preview
@Composable
fun SelectOptionPreview() {
    UvodniObrazovka(ObchodniRejstrikViewModel())
}