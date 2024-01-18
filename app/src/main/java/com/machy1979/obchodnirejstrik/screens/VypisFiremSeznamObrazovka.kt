package com.machy1979.obchodnirejstrik.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier

import androidx.compose.ui.platform.LocalContext

import com.machy1979.obchodnirejstrik.screens.components.Nacitani
import com.machy1979.obchodnirejstrik.screens.components.ObycPolozkaHodnota

import com.machy1979.obchodnirejstrik.screens.components.VypisErrorHlasku
import com.machy1979.obchodnirejstrik.ui.theme.*
import com.machy1979.obchodnirejstrik.viewmodel.ObchodniRejstrikViewModel


//vypsání seznamu nalezených subjektů/firem
@Composable
fun VypisFiremSeznamObrazovka (
    viewModel: ObchodniRejstrikViewModel,
    onCancelButtonClicked: () -> Unit = {},
    onCardClicked: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current //tohle používat místo this
    val errorMessage by viewModel.errorMessage.collectAsState()
    val nacitani by viewModel.nacitani.collectAsState()


    Column(modifier = Modifier.padding(VelikostPaddingHlavnihoOkna)) {
        if(nacitani) {
            Nacitani()
        } else {
            if((errorMessage=="")) {
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(viewModel.companysData) {
                Card(
                    //  backgroundColor = Color.Blue,
                    shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
                    border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
                    elevation = VelikostElevation,
                    modifier = Modifier
                        .padding(horizontal = VelikostPaddingCardHorizontal, vertical = VelikostPaddingCardVertical)
                        .fillMaxWidth()
                        .clickable {
                            onCardClicked(it.ico)
                        },

                    ) {
                    Column {
                        Row {
                            Spacer(Modifier.weight(1f))

                        }

                        ObycPolozkaHodnota(it.name, true,true)
                        ObycPolozkaHodnota("ICO: "+it.ico, true, false)
                        ObycPolozkaHodnota(it.address, false,false)
                    }
                }



            }
        }
            } else {
                VypisErrorHlasku(errorMessage)

            }

        }
    }
}