package com.machy1979.obchodnirejstrik.screens

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
// Fetching current app configuration

        val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        var expanded by remember { mutableStateOf (false) }
        val paddingModifierHlavniCard = if (isLandscape) {
            Modifier.padding(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 10.dp)
        } else {
            Modifier.padding(start = 20.dp, end = 20.dp, top = 100.dp, bottom = 10.dp)
        }
        val paddingModifierSpodniCard = if (expanded) {
            Modifier
                .fillMaxWidth()
                .padding(PaddingVnitrniCard)
                .clickable {
                    expanded = !expanded
                }
        } else {
            Modifier
                .padding(0.dp)
                .clickable {
                    expanded = !expanded
                }
        }
        Card(
            elevation = VelikostElevation,
            // modifier = Modifier
            //    .padding(horizontal = 20.dp, vertical = 70.dp),
            modifier = paddingModifierHlavniCard,
            shape = RoundedCornerShape(VelikostZakulaceniRohuButtonTextField ),

        ) {
            Column (
                modifier = Modifier
                    .animateContentSize( //efekt pro rozbalení
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
            ){
                Card(
                    elevation = VelikostElevation,
                    modifier = Modifier
                        .then(if (expanded) Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 0.dp)
                            .then(if (expanded) Modifier.fillMaxWidth() else Modifier)
                            .padding(PaddingVButtonu)
                        else Modifier),
                    shape = RoundedCornerShape(VelikostZakulaceniRohuButtonTextFieldVnitrni),
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
                                painter = if(expanded) {
                                    painterResource(id = com.machy1979.obchodnirejstrik.R.drawable.collapsed_icon)
                                } else {
                                    painterResource(id = com.machy1979.obchodnirejstrik.R.drawable.expandabled_icon)

                                },
                                contentDescription = "Search",
                                modifier = Modifier
                                    .clickable { expanded = !expanded }
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

                Card(
                    elevation = VelikostElevation,
                    modifier = paddingModifierSpodniCard,
                    shape = RoundedCornerShape(VelikostZakulaceniRohuButtonTextFieldVnitrni),
                ) {
                    if (expanded) {
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