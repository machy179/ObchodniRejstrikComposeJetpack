package com.machy1979.obchodnirejstrik.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.machy1979.obchodnirejstrik.viewmodel.ObchodniRejstrikViewModel
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.machy1979.obchodnirejstrik.functions.RozparzovaniDatDotazDleIco
import com.machy1979.obchodnirejstrik.screens.components.Nacitani
import com.machy1979.obchodnirejstrik.screens.components.VypisErrorHlasku
import com.machy1979.obchodnirejstrik.ui.theme.*
import com.machy1979.obchodnirejstrik.viewmodel.ORViewModel

@Composable
fun VypisORObrazovka (
    viewModel: ORViewModel,
    onCancelButtonClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    val companyDataFromOR by viewModel.companyDataFromOR.collectAsState()




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
                        ObycPolozkaNadpisHodnota("Název firmy:",companyDataFromOR.name, true)
                        ObycPolozkaNadpisHodnota("Ico:",companyDataFromOR.ico, true)
                        ObycPolozkaNadpisHodnota("Sídlo:",companyDataFromOR.address, true, true)
                        ObycPolozkaNadpisHodnota("Stav subjektu:",companyDataFromOR.stavSubjektu, true)
                        ObycPolozkaNadpisHodnota("Právní forma:",companyDataFromOR.pravniForma, true)
                        ObycPolozkaNadpisHodnota("Datum zápisu:",companyDataFromOR.datumZapisu, true)
                        ObycPolozkaNadpisHodnota("Soud:",companyDataFromOR.soud, true)
                        ObycPolozkaNadpisHodnota("Spisová značka:",companyDataFromOR.spisovaZnacka, false)


                    }
                }
            }

            Spacer(modifier = Modifier.height(OdsazeniMensi))
        }
        item {
            SeznamPolozek(nazevSeznamuPolozek = "Předmět podnikání:", seznamPolozek = companyDataFromOR.predmetPodnikani)
        }
        item {
            SeznamPolozek(nazevSeznamuPolozek = "Ostatní skutečnosti:", seznamPolozek = companyDataFromOR.ostatniSkutecnosti)
        }
        //kapitál
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
                            .padding(10.dp)
                    ) {
                        ObycPolozkaJenNadpisUprostred("Kapitál:", false)
                        ObycPolozkaNadpisHodnota("Vklad:",companyDataFromOR.vklad, true)
                        ObycPolozkaNadpisHodnota("Splaceno:",companyDataFromOR.splaceno, true)
                        ObycPolozkaNadpisHodnota("Akcie:",companyDataFromOR.akcie, false)
                    }
                }
            }
            Spacer(modifier = Modifier.height(OdsazeniMensi))


        }

        item {
            SeznamOsobAFirem(nazevSeznamuOsobAFirem = "Statutární orgán:", seznamOsob = companyDataFromOR.statutarniOrganOsoby, seznamFirem = companyDataFromOR.statutarniOrganFirmy,dalsiTextSeznam = companyDataFromOR.statutarniOrganSkutecnosti)
        }
        item {
            SeznamOsob(nazevSeznamuOsob = "Prokura:", seznamOsob = companyDataFromOR.prokura)
        }
        item {
            SeznamOsob(nazevSeznamuOsob = "Dozorčí rada:", seznamOsob = companyDataFromOR.dozorciRada)
        }
        item {
            SeznamOsobAFirem(nazevSeznamuOsobAFirem = "Společníci s vkladem:", seznamOsob = companyDataFromOR.spolecniciSVklademOsoby, seznamFirem = companyDataFromOR.spolecniciSVklademFirmy)
        }
        item {
            SeznamOsobAFirem(nazevSeznamuOsobAFirem = "Akcionáři:", seznamOsob = companyDataFromOR.akcionariOsoby, seznamFirem = companyDataFromOR.akcionariFirmy)
        }

        item {
            SeznamOsobAFirem(nazevSeznamuOsobAFirem = "Likvidace:", seznamOsob = companyDataFromOR.likvidaceOsoby, seznamFirem = companyDataFromOR.likvidaceFirmy)
        }
    }
}