package com.machy1979.obchodnirejstrik.screens.extractrzp

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
import com.machy1979.obchodnirejstrik.components.SeznamOsob
import com.machy1979.obchodnirejstrik.components.SeznamPolozekZivnosti
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

@Composable
fun VypisRZPObrazovka(
    viewModel: RZPViewModel,
    adsDisabled: State<Boolean>,
    navController: NavHostController,
) {

    val companyDataFromRZP by viewModel.companyDataFromRZP.collectAsState()


    val context = LocalContext.current
    val currentScreen = TitlesOfSrceens.valueOf(TitlesOfSrceens.VypisRZP.name)


    Scaffold(
        backgroundColor = Color.Transparent,
        topBar = {
            ObchodniRejstrikAppBar(
                currentScreen = currentScreen,
                canNavigateBack = true,
                canShare = true,
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
                        ObycPolozkaNadpisHodnota("Název firmy:", companyDataFromRZP.name, true)
                        ObycPolozkaNadpisHodnota("Ico:", companyDataFromRZP.ico, true)
                        ObycPolozkaNadpisHodnota("Adresa:", companyDataFromRZP.address, true, true)
                        ObycPolozkaNadpisHodnota(
                            "Právní forma:",
                            companyDataFromRZP.pravniForma,
                            true
                        )
                        ObycPolozkaNadpisHodnota(
                            "Typ subjektu:",
                            companyDataFromRZP.typSubjektu,
                            true
                        )
                        ObycPolozkaNadpisHodnota(
                            "Evidující úřad:",
                            companyDataFromRZP.evidujiciUrad,
                            true
                        )
                        ObycPolozkaNadpisHodnota(
                            "Vznik první živnosti:",
                            companyDataFromRZP.vznikPrvniZivnosti,
                            true
                        )


                    }
                }
            }

            Spacer(modifier = Modifier.height(OdsazeniMensi))



            if (companyDataFromRZP.osoby.size != 0) SeznamOsob(
                nazevSeznamuOsob = "Osoby:",
                seznamOsob = companyDataFromRZP.osoby
            )


            SeznamPolozekZivnosti(
                nazevSeznamuPolozek = "Živnosti:",
                seznamZivnosti = companyDataFromRZP.zivnosti
            )

            if (!adsDisabled.value) {

                ORNativeAdWrapped()


            }
        }
    }
}