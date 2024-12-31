package com.machy1979.obchodnirejstrik.screens.history

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.machy1979.obchodnirejstrik.components.ObchodniRejstrikAppBar
import com.machy1979.obchodnirejstrik.components.ObycPolozkaHodnota
import com.machy1979.obchodnirejstrik.model.Query
import com.machy1979.obchodnirejstrik.navigation.ObchodniRejstrikScreens
import com.machy1979.obchodnirejstrik.screens.extractor.ORViewModel
import com.machy1979.obchodnirejstrik.screens.extractres.RESViewModel
import com.machy1979.obchodnirejstrik.screens.extractrzp.RZPViewModel
import com.machy1979.obchodnirejstrik.screens.home.ObchodniRejstrikViewModel
import com.machy1979.obchodnirejstrik.ui.theme.ColorBorderStroke
import com.machy1979.obchodnirejstrik.ui.theme.PaddingTopAplikace
import com.machy1979.obchodnirejstrik.ui.theme.PaddingVnitrniCard
import com.machy1979.obchodnirejstrik.ui.theme.VelikostBorderStrokeCard
import com.machy1979.obchodnirejstrik.ui.theme.VelikostElevation
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardHorizontal
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardVertical
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingHlavnihoOkna
import com.machy1979.obchodnirejstrik.ui.theme.VelikostZakulaceniRohu
import com.machy1979.obchodnirejstrik.utils.TitlesOfSrceens

@Composable
fun HistorieVyhledavaniObrazovka(
    viewModel: ObchodniRejstrikViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navController: NavHostController,
    resViewModel: RESViewModel,
    rzpViewModel: RZPViewModel,
    orViewModel: ORViewModel,
) {
    val context = LocalContext.current //tohle používat místo this
    //history list:
    val queryList = viewModel.queryList.collectAsState().value
    val nactenoQueryList by viewModel.nactenoQueryList.collectAsState()


    val currentScreen = TitlesOfSrceens.valueOf(TitlesOfSrceens.HistorieVyhledavani.name)
    Scaffold(
        backgroundColor = Color.Transparent,
        topBar = {
            ObchodniRejstrikAppBar(
                navController = navController,
                currentScreen = currentScreen,
                canNavigateBack = true,
                canDeleteButton = queryList.size > 0, //pokud bude queryList větší než 0, což znamená, že v historii jsou položky, tak je to true a je možné zobrazit delete button
                share = { },
                saveToPdf = { },
                deleteAllHistory = {
                    viewModel.deleteAllHistory()
                    toastHistoryDeleted(context)
                },
                modifier = Modifier
                    .padding(top = PaddingTopAplikace)
                    .fillMaxWidth(),
            )

        },
    ) { paddingValues ->

        Column(
            modifier = modifier
                .padding(VelikostPaddingHlavnihoOkna)
                .fillMaxWidth().padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .padding(WindowInsets.navigationBars.asPaddingValues()) // Přidání prostoru pro navigation bar
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
// Fetching current app configuration

            val isLandscape =
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            var expanded by rememberSaveable { mutableStateOf(false) }
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




            if (nactenoQueryList) {

                ListOfHistory(queryList,
                    goToIcoButton = {

                        viewModel.loadDataIco(it)
                        resViewModel.loadDataIcoRES(it, context)
                        rzpViewModel.loadDataIcoRZP(it, context)
                        orViewModel.loadDataIcoOR(it, context)
                        navController.navigate(ObchodniRejstrikScreens.VypisIcoObrazovka.name)

                    })


            }


        }
    }
}

@Composable
fun ListOfHistory(
    queryList: List<Query>,
    goToIcoButton: (String) -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Seznam položek
                queryList.forEach { query ->
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
                                goToIcoButton(query.ico)
                            }
                    ) {
                        Column {
                            Row {
                                Spacer(Modifier.weight(1f))
                            }

                            ObycPolozkaHodnota(query.name, true, true)
                            ObycPolozkaHodnota("ICO: " + query.ico, true, false)
                            ObycPolozkaHodnota(query.address, false, false)
                        }
                    }
                }
            }
        }


    }


}

fun toastHistoryDeleted(context: Context) {
    Toast.makeText(context, "Historie vymazána", Toast.LENGTH_LONG)
        .show()
}