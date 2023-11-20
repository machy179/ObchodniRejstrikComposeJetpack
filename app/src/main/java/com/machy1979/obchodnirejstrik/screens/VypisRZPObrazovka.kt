package com.machy1979.obchodnirejstrik.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import com.machy1979.obchodnirejstrik.screens.components.*
import com.machy1979.obchodnirejstrik.ui.theme.*

import com.machy1979.obchodnirejstrik.viewmodel.RZPViewModel

@Composable
fun VypisRZPObrazovka (
    viewModel: RZPViewModel,
    onCancelButtonClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    val companyDataFromRZP by viewModel.companyDataFromRZP.collectAsState()



    LazyColumn(modifier = Modifier.padding(VelikostPaddingHlavnihoOkna).fillMaxHeight())
    {
        //základní údaje
        item {
            Card(
                //  backgroundColor = Color.Blue,
                shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
                border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
                elevation = VelikostElevation,
                modifier = Modifier
                    .padding(horizontal = VelikostPaddingCardHorizontal, vertical = VelikostPaddingCardVertical)
                    .fillMaxWidth(),

                ) {
                SelectionContainer {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                    ) {

                        ObycPolozkaJenNadpisUprostred("Základní údaje subjektu", false)
                        ObycPolozkaNadpisHodnota("Název firmy:",companyDataFromRZP.name, true)
                        ObycPolozkaNadpisHodnota("Ico:",companyDataFromRZP.ico, true)
                        ObycPolozkaNadpisHodnota("Adresa:",companyDataFromRZP.address, true, true)
                        ObycPolozkaNadpisHodnota("Právní forma:",companyDataFromRZP.pravniForma, true)
                        ObycPolozkaNadpisHodnota("Typ subjektu:",companyDataFromRZP.typSubjektu, true)
                        ObycPolozkaNadpisHodnota("Evidující úřad:",companyDataFromRZP.evidujiciUrad, true)
                        ObycPolozkaNadpisHodnota("Vznik první živnosti:",companyDataFromRZP.vznikPrvniZivnosti, true)


                    }
                }
            }

            Spacer(modifier = Modifier.height(OdsazeniMensi))
        }

        item {
            SeznamOsob(nazevSeznamuOsob = "Osoby:", seznamOsob = companyDataFromRZP.osoby)
        }
        item {
            SeznamPolozekZivnosti(nazevSeznamuPolozek = "Živnosti:", seznamZivnosti = companyDataFromRZP.zivnosti)
        }
    }
}