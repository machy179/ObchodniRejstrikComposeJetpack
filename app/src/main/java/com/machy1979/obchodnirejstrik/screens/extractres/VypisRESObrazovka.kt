package com.machy1979.obchodnirejstrik.screens.extractres

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.machy1979.obchodnirejstrik.components.ORNativeAdWrapped
import com.machy1979.obchodnirejstrik.components.ObycPolozkaJenNadpisUprostred
import com.machy1979.obchodnirejstrik.components.ObycPolozkaNadpisHodnota
import com.machy1979.obchodnirejstrik.components.SeznamDvoupolozekNace
import com.machy1979.obchodnirejstrik.ui.theme.*

//zatím je to jen kopie z VypisRZPObrazovka, tak to předělat
@Composable
fun VypisRESObrazovka (
    viewModel: RESViewModel,
    onCancelButtonClicked: () -> Unit = {},
    modifier: Modifier = Modifier,
    adsDisabled: State<Boolean>,
    navController: NavHostController
) {

    val companyDataFromRES by viewModel.companyDataFromRES.collectAsState()



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
                        ObycPolozkaNadpisHodnota("Název firmy:",companyDataFromRES.name, true)
                        ObycPolozkaNadpisHodnota("Ico:",companyDataFromRES.ico, true)
                        ObycPolozkaNadpisHodnota("Sídlo:",companyDataFromRES.address, true, true)
                        ObycPolozkaNadpisHodnota("Právní forma:",companyDataFromRES.pravniForma, true)
                        ObycPolozkaNadpisHodnota("Datum vzniku:",companyDataFromRES.datumVzniku, true)
                        ObycPolozkaNadpisHodnota("Základní územní jednotka:",companyDataFromRES.zakladniUzemniJednotka, true)
                        ObycPolozkaNadpisHodnota("Kód ZÚJ:",companyDataFromRES.kodZUJ, true)
                        ObycPolozkaNadpisHodnota("Okres:",companyDataFromRES.okres, true)
                        ObycPolozkaNadpisHodnota("Kód okresu:",companyDataFromRES.kodOkresu, true)
                 }
                }


            }
            Spacer(modifier = Modifier.height(OdsazeniMensi))

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
                        ObycPolozkaJenNadpisUprostred("Statistické údaje:", false)
                        ObycPolozkaNadpisHodnota("Instit. sektor:",companyDataFromRES.institucSektor, true)
                        ObycPolozkaNadpisHodnota("Počet zam.:",companyDataFromRES.pocetZamestnancu, true)

                    }
                }


            }
            Spacer(modifier = Modifier.height(OdsazeniMensi))
        }

        item {
            SeznamDvoupolozekNace(nazevSeznamuDvoupolozek = "Klasifikace ekonomických činností CZ-NACE", seznamDvoupolozek = companyDataFromRES.nace)
        }

        if (!adsDisabled.value) {
            item {
                ORNativeAdWrapped()
            }

        }

    }
}