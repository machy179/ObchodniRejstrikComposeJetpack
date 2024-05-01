package com.machy1979.obchodnirejstrik.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer

import androidx.compose.material.Card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp

import com.machy1979.obchodnirejstrik.screens.components.*
import com.machy1979.obchodnirejstrik.ui.theme.*
import com.machy1979.obchodnirejstrik.viewmodel.ORViewModel
import com.machy1979.obchodnirejstrik.viewmodel.ObchodniRejstrikViewModel
import com.machy1979.obchodnirejstrik.viewmodel.RESViewModel
import com.machy1979.obchodnirejstrik.viewmodel.RZPViewModel

@Composable
fun  VypisIcoObrazovka(
    viewModel: ObchodniRejstrikViewModel,
    resViewModel: RESViewModel,
    rzpViewModel: RZPViewModel,
    orViewModel: ORViewModel,
    onCancelButtonClicked: () -> Unit = {},
    hledejORButtonClicked: () -> Unit = {},
    hledejRZPButtonClicked: () -> Unit = {},
    hledejRESButtonClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {


   // val companyData by viewModel.companyData.collectAsState()
    val companyData by viewModel.companyData.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()


    val nacitani by viewModel.nacitani.collectAsState()

    val nacitaniOR by orViewModel.nacitaniOR.collectAsState()
    val errorMessageOR by orViewModel.errorMessageOR.collectAsState()
    val buttonClickedOR by orViewModel.buttonClickedOR.collectAsState()

    val nacitaniRZP by rzpViewModel.nacitaniRZP.collectAsState()
    val errorMessageRZP by rzpViewModel.errorMessageRZP.collectAsState()
    val buttonClickedRZP by rzpViewModel.buttonClickedRZP.collectAsState()

    val nacitaniRES by resViewModel.nacitaniRES.collectAsState()
    val errorMessageRES by resViewModel.errorMessageRES.collectAsState()
    val buttonClickedRES by resViewModel.buttonClickedRES.collectAsState()




    Column(
        modifier = modifier
            .padding(VelikostPaddingHlavnihoOkna)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)

    ) {

        if(nacitani) {
            Nacitani()
        } else {

            if((errorMessage=="")) {
                Card(
                    //  backgroundColor = Color.Blue,
                    shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
                    border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
                    elevation = VelikostElevation,
                    modifier = Modifier
                        .padding(horizontal = VelikostPaddingCardHorizontal, vertical = VelikostPaddingCardVertical),

                    ) {
                    SelectionContainer {
                        Column(
                            modifier = Modifier
                                .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                .fillMaxWidth()
                        ) {
                            ObycPolozkaNadpisHodnota("Název firmy:",companyData.name, true)
                            ObycPolozkaNadpisHodnota("Ico:",companyData.ico, true)
                            ObycPolozkaNadpisHodnota("Dic:",companyData.dic, true)
                            ObycPolozkaNadpisHodnota("Adresa:",companyData.address, false)
                        }
                    }

                }

                CustomButton(if (errorMessageOR==" ") { ("Načíst z OR")} else {("Subjekt není v OR")}, nacitaniOR,buttonClickedOR,
                    onClick = {
                        hledejORButtonClicked()
                    }
                )

                CustomButton(if (errorMessageRZP==" ") { ("Načíst z RŽP")} else {("Subjekt není v RŽP")}, nacitaniRZP,buttonClickedRZP,
                    onClick = {
                        hledejRZPButtonClicked()
                    }
                )
                CustomButton(if (errorMessageRES==" ") { ("Načíst z RES")} else {("Subjekt není v RES")}, nacitaniRES,buttonClickedRES,
                    onClick = {
                        hledejRESButtonClicked()
                    }
                )


            } else {
                VypisErrorHlasku(errorMessage)
        }
    }
    }
}