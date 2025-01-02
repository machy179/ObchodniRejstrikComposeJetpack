package com.machy1979.obchodnirejstrik.screens.companylist


import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.machy1979.obchodnirejstrik.components.Nacitani
import com.machy1979.obchodnirejstrik.components.ObchodniRejstrikAppBar
import com.machy1979.obchodnirejstrik.components.ObycPolozkaHodnota
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
import com.machy1979.obchodnirejstrik.ui.theme.VelikostZakulaceniRohu
import com.machy1979.obchodnirejstrik.utils.TitlesOfSrceens


//vypsání seznamu nalezených subjektů/firem
@Composable
fun VypisFiremSeznamObrazovka(
    viewModel: ObchodniRejstrikViewModel,
    navController: NavHostController,
    resViewModel: RESViewModel,
    rzpViewModel: RZPViewModel,
    orViewModel: ORViewModel,
) {

    val context = LocalContext.current
    val errorMessage by viewModel.errorMessage.collectAsState()
    val nacitani by viewModel.nacitani.collectAsState()
    val currentScreen = TitlesOfSrceens.valueOf(TitlesOfSrceens.VypisFiremSeznam.name)

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
            modifier = Modifier
                .padding(VelikostPaddingHlavnihoOkna)
                .fillMaxWidth().padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
              //  .padding(WindowInsets.navigationBars.asPaddingValues()) // Přidání prostoru pro navigation bar
        ) {
            if (nacitani) {
                Nacitani()
            } else {
                if ((errorMessage == "")) {
                    LazyColumn(modifier = Modifier.fillMaxHeight()) {
                        items(viewModel.companysData) {
                            Card(
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
                                    )
                                    .fillMaxWidth()
                                    .clickable {
                                        if (!it.equals(" ")) {
                                            Log.i("clickedIt: ", "222")
                                            viewModel.loadDataIco(it.ico)
                                            resViewModel.loadDataIcoRES(it.ico, context)
                                            rzpViewModel.loadDataIcoRZP(it.ico, context)
                                            orViewModel.loadDataIcoOR(it.ico, context)
                                            navController.navigate(ObchodniRejstrikScreens.VypisIcoObrazovka.name)
                                        }

                                    },

                                ) {
                                Column {
                                    Row {
                                        Spacer(Modifier.weight(1f))

                                    }

                                    ObycPolozkaHodnota(it.name, true, true)
                                    ObycPolozkaHodnota("ICO: " + it.ico, true, false)
                                    ObycPolozkaHodnota(it.address, false, false)
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
}