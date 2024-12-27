package com.machy1979.obchodnirejstrik.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.machy1979.obchodnirejstrik.screens.companylist.VypisFiremSeznamObrazovka
import com.machy1979.obchodnirejstrik.screens.components.VypisORObrazovka
import com.machy1979.obchodnirejstrik.screens.extractico.VypisIcoObrazovka
import com.machy1979.obchodnirejstrik.screens.history.HistorieVyhledavaniObrazovka
import com.machy1979.obchodnirejstrik.screens.home.ObchodniRejstrikViewModel
import com.machy1979.obchodnirejstrik.screens.home.UvodniObrazovka
import com.machy1979.obchodnirejstrik.screens.extractor.ORViewModel
import com.machy1979.obchodnirejstrik.screens.extractres.RESViewModel
import com.machy1979.obchodnirejstrik.screens.extractres.VypisRESObrazovka
import com.machy1979.obchodnirejstrik.screens.extractrzp.RZPViewModel
import com.machy1979.obchodnirejstrik.screens.extractrzp.VypisRZPObrazovka

@Composable
fun ObchodniRejstrikNavigation(
    activity: Activity?

) {
    val navController = rememberNavController()
    val currentScreen = rememberSaveable { mutableStateOf(ObchodniRejstrikScreens.UvodniObrazovka.name) }

    val ObchodniRejstrikViewModel = hiltViewModel<ObchodniRejstrikViewModel>()
    val resViewModel = hiltViewModel<RESViewModel>()
    val rzpViewModel = hiltViewModel<RZPViewModel>()
    val orViewModel = hiltViewModel<ORViewModel>()

    val adsDisabled = ObchodniRejstrikViewModel.adsDisabled.collectAsState()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = currentScreen.value
    ) {

        composable(ObchodniRejstrikScreens.UvodniObrazovka.name) {
            UvodniObrazovka(
                navController = navController,
                viewModel = ObchodniRejstrikViewModel
            )
        }

        composable(ObchodniRejstrikScreens.HistorieVyhladavaniObrazovka.name) {
            HistorieVyhledavaniObrazovka(
                navController = navController,
                viewModel = ObchodniRejstrikViewModel,
                resViewModel = resViewModel,
                rzpViewModel = rzpViewModel,
                orViewModel = orViewModel
            )
        }

        composable(ObchodniRejstrikScreens.VypisFiremSeznamObrazovka.name) {
            VypisFiremSeznamObrazovka(
                navController = navController,
                viewModel = ObchodniRejstrikViewModel,
                resViewModel = resViewModel,
                rzpViewModel = rzpViewModel,
                orViewModel = orViewModel
            )
        }

        composable(ObchodniRejstrikScreens.VypisIcoObrazovka.name) {
            VypisIcoObrazovka(
                navController = navController,
                viewModel = ObchodniRejstrikViewModel,
                resViewModel = resViewModel,
                rzpViewModel = rzpViewModel,
                orViewModel = orViewModel
            )
        }

        composable(ObchodniRejstrikScreens.VypisORObrazovka.name) {
            VypisORObrazovka(
                navController = navController,
                viewModel = orViewModel,
                onClickedButtonIcoSubjekt = {  clickedIco -> //tato funkce je pro butto ve výpisu, kde je u jednotlivých subjektů ico, aby šlo prokliknout
                    ObchodniRejstrikViewModel.loadDataIco(clickedIco)
                    resViewModel.loadDataIcoRES(clickedIco, context)
                    rzpViewModel.loadDataIcoRZP(clickedIco, context)
                    orViewModel.loadDataIcoOR(clickedIco, context)
                    navController.navigate(ObchodniRejstrikScreens.VypisIcoObrazovka.name)
                },
                orViewModel = orViewModel,
                adsDisabled = adsDisabled

            )
        }

        composable(ObchodniRejstrikScreens.VypisRESObrazovka.name) {
            VypisRESObrazovka(
                navController = navController,
                viewModel = resViewModel,

                adsDisabled = adsDisabled

            )
        }

        composable(ObchodniRejstrikScreens.VypisRZPObrazovka.name) {
            VypisRZPObrazovka(
                navController = navController,
                viewModel = rzpViewModel,

                adsDisabled = adsDisabled

            )
        }

    }

}