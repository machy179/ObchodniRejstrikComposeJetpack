package com.machy1979.obchodnirejstrik.screens.extractico



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.machy1979.obchodnirejstrik.components.CustomButton
import com.machy1979.obchodnirejstrik.components.Nacitani
import com.machy1979.obchodnirejstrik.components.ORNativeAdLayout
import com.machy1979.obchodnirejstrik.components.ObchodniRejstrikAppBar
import com.machy1979.obchodnirejstrik.components.ObycPolozkaNadpisHodnota
import com.machy1979.obchodnirejstrik.components.VypisErrorHlasku
import com.machy1979.obchodnirejstrik.navigation.ObchodniRejstrikScreens
import com.machy1979.obchodnirejstrik.screens.extractor.ORViewModel
import com.machy1979.obchodnirejstrik.screens.extractres.RESViewModel
import com.machy1979.obchodnirejstrik.screens.extractrzp.RZPViewModel
import com.machy1979.obchodnirejstrik.screens.home.ObchodniRejstrikViewModel
import com.machy1979.obchodnirejstrik.ui.theme.ColorBorderStroke
import com.machy1979.obchodnirejstrik.ui.theme.PaddingTopAplikace
import com.machy1979.obchodnirejstrik.ui.theme.VelikostBorderStrokeCard
import com.machy1979.obchodnirejstrik.ui.theme.VelikostElevation
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardHorizontal
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardVertical
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingHlavnihoOkna
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingMezeryMeziHlavnimiZaznamy
import com.machy1979.obchodnirejstrik.ui.theme.VelikostZakulaceniRohu
import com.machy1979.obchodnirejstrik.utils.TitlesOfSrceens

@Composable
fun VypisIcoObrazovka(
    viewModel: ObchodniRejstrikViewModel,
    resViewModel: RESViewModel,
    rzpViewModel: RZPViewModel,
    orViewModel: ORViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController,
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

    val adsDisabled = viewModel.adsDisabled.collectAsState()

    val currentScreen = TitlesOfSrceens.valueOf(TitlesOfSrceens.VypisIco.name)
    Scaffold(
        backgroundColor = Color.Transparent,
        topBar = {
            ObchodniRejstrikAppBar(
                currentScreen = currentScreen,
                canNavigateBack = true,
                share = { },
                saveToPdf = { },
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
            modifier = modifier
                .padding(VelikostPaddingHlavnihoOkna)
                .fillMaxWidth().padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                ),
         //       .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {

            if (nacitani) {
                Nacitani()
            } else {

                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(VelikostPaddingCardVertical),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if ((errorMessage == "")) {
                        Card(
                            //  backgroundColor = Color.Blue,
                            shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
                            border = BorderStroke(
                                width = VelikostBorderStrokeCard,
                                color = ColorBorderStroke
                            ),
                            elevation = VelikostElevation,
                            modifier = Modifier
                                .padding(
                                    horizontal = VelikostPaddingCardHorizontal,
                                    vertical = VelikostPaddingCardVertical
                                ),

                            ) {
                            SelectionContainer {
                                Column(
                                    modifier = Modifier
                                        .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                        .fillMaxWidth()
                                ) {
                                    ObycPolozkaNadpisHodnota("Název firmy:", companyData.name, true)
                                    ObycPolozkaNadpisHodnota("Ico:", companyData.ico, true)
                                    ObycPolozkaNadpisHodnota("Dic:", companyData.dic, true)
                                    ObycPolozkaNadpisHodnota("Adresa:", companyData.address, false)
                                }
                            }

                        }

                        CustomButton(
                            if (errorMessageOR == " ") {
                                ("Načíst z OR")
                            } else {
                                ("Subjekt není v OR")
                            }, nacitaniOR, buttonClickedOR,
                            onClick = {
                                //    hledejORButtonClicked()
                                navController.navigate(ObchodniRejstrikScreens.VypisORObrazovka.name)
                            }
                        )


                        CustomButton(if (errorMessageRZP == " ") {
                            ("Načíst z RŽP")
                        } else {
                            ("Subjekt není v RŽP")
                        }, nacitaniRZP, buttonClickedRZP,
                            onClick = {
                                navController.navigate(ObchodniRejstrikScreens.VypisRZPObrazovka.name)
                            }
                        )
                        CustomButton(if (errorMessageRES == " ") {
                            ("Načíst z RES")
                        } else {
                            ("Subjekt není v RES")
                        }, nacitaniRES, buttonClickedRES,
                            onClick = {
                                navController.navigate(ObchodniRejstrikScreens.VypisRESObrazovka.name)
                            }
                        )


                    } else {
                        VypisErrorHlasku(errorMessage)
                    }
                    if (!adsDisabled.value) {
                        ORNativeAdLayout { isLoaded ->
                        }
                    }
                }


            }
        }
    }
}