package com.machy1979.obchodnirejstrik.screens.extractres

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.machy1979.obchodnirejstrik.components.ORNativeAdWrapped
import com.machy1979.obchodnirejstrik.components.ObchodniRejstrikAppBar
import com.machy1979.obchodnirejstrik.components.ObycPolozkaJenNadpisUprostred
import com.machy1979.obchodnirejstrik.components.ObycPolozkaNadpisHodnota
import com.machy1979.obchodnirejstrik.components.SeznamDvoupolozekNace
import com.machy1979.obchodnirejstrik.ui.theme.ColorBorderStroke
import com.machy1979.obchodnirejstrik.ui.theme.OdsazeniMensi
import com.machy1979.obchodnirejstrik.ui.theme.PaddingTopAplikace
import com.machy1979.obchodnirejstrik.ui.theme.VelikostBorderStrokeCard
import com.machy1979.obchodnirejstrik.ui.theme.VelikostElevation
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardHorizontal
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardVertical
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingMezeryMeziHlavnimiZaznamy
import com.machy1979.obchodnirejstrik.ui.theme.VelikostZakulaceniRohu
import com.machy1979.obchodnirejstrik.utils.TitlesOfSrceens

//zatím je to jen kopie z VypisRZPObrazovka, tak to předělat
@Composable
fun VypisRESObrazovka(
    viewModel: RESViewModel,
    adsDisabled: State<Boolean>,
    navController: NavHostController,
) {

    val companyDataFromRES by viewModel.companyDataFromRES.collectAsState()

    val context = LocalContext.current
    val currentScreen = TitlesOfSrceens.valueOf(TitlesOfSrceens.VypisRES.name)


    Scaffold(
        backgroundColor = Color.Transparent,
        topBar = {
            ObchodniRejstrikAppBar(
                currentScreen = currentScreen,
                canNavigateBack = true,
                canShare = false,
                share = { viewModel.share(context) },
                saveToPdf = { viewModel.saveToPdf(context) },
                deleteAllHistory = { },
                canHistoryOfSearch = false,
                modifier = Modifier
                    .padding(top = PaddingTopAplikace)
                    .fillMaxWidth(),
                navController = navController,
            )

        },
    ) { paddingValues ->


        Column(
            modifier = Modifier
                .fillMaxWidth().padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
        )
        {
            //základní údaje
            Card(
                //  backgroundColor = Color.Blue,
                shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
                border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
                elevation = VelikostElevation,
                modifier = Modifier
                    .padding(
                        horizontal = VelikostPaddingCardHorizontal,
                        vertical = VelikostPaddingCardVertical
                    )
                    .fillMaxWidth(),

                ) {
                SelectionContainer {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                    ) {

                        ObycPolozkaJenNadpisUprostred("Základní údaje subjektu", false)
                        ObycPolozkaNadpisHodnota("Název firmy:", companyDataFromRES.name, true)
                        ObycPolozkaNadpisHodnota("Ico:", companyDataFromRES.ico, true)
                        ObycPolozkaNadpisHodnota("Sídlo:", companyDataFromRES.address, true, true)
                        ObycPolozkaNadpisHodnota(
                            "Právní forma:",
                            companyDataFromRES.pravniForma,
                            true
                        )
                        ObycPolozkaNadpisHodnota(
                            "Datum vzniku:",
                            companyDataFromRES.datumVzniku,
                            true
                        )
                        ObycPolozkaNadpisHodnota(
                            "Základní územní jednotka:",
                            companyDataFromRES.zakladniUzemniJednotka,
                            true
                        )
                        ObycPolozkaNadpisHodnota("Kód ZÚJ:", companyDataFromRES.kodZUJ, true)
                        ObycPolozkaNadpisHodnota("Okres:", companyDataFromRES.okres, true)
                        ObycPolozkaNadpisHodnota("Kód okresu:", companyDataFromRES.kodOkresu, true)
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
                    .padding(
                        horizontal = VelikostPaddingCardHorizontal,
                        vertical = VelikostPaddingCardVertical
                    )
                    .fillMaxWidth(),

                ) {
                SelectionContainer {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                    ) {
                        ObycPolozkaJenNadpisUprostred("Statistické údaje:", false)
                        ObycPolozkaNadpisHodnota(
                            "Instit. sektor:",
                            companyDataFromRES.institucSektor,
                            true
                        )
                        ObycPolozkaNadpisHodnota(
                            "Počet zam.:",
                            companyDataFromRES.pocetZamestnancu,
                            true
                        )

                    }
                }


            }
            Spacer(modifier = Modifier.height(OdsazeniMensi))

            SeznamDvoupolozekNace(
                nazevSeznamuDvoupolozek = "Klasifikace ekonomických činností CZ-NACE",
                seznamDvoupolozek = companyDataFromRES.nace
            )


            if (!adsDisabled.value) {

                ORNativeAdWrapped()


            }

        }
    }
}