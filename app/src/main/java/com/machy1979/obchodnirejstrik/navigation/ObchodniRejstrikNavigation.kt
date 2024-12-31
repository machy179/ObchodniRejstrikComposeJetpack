package com.machy1979.obchodnirejstrik.navigation

import android.app.Activity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.machy1979.obchodnirejstrik.screens.companylist.VypisFiremSeznamObrazovka
import com.machy1979.obchodnirejstrik.screens.components.VypisORObrazovka
import com.machy1979.obchodnirejstrik.screens.extractico.VypisIcoObrazovka
import com.machy1979.obchodnirejstrik.screens.extractor.ORViewModel
import com.machy1979.obchodnirejstrik.screens.extractres.RESViewModel
import com.machy1979.obchodnirejstrik.screens.extractres.VypisRESObrazovka
import com.machy1979.obchodnirejstrik.screens.extractrzp.RZPViewModel
import com.machy1979.obchodnirejstrik.screens.extractrzp.VypisRZPObrazovka
import com.machy1979.obchodnirejstrik.screens.history.HistorieVyhledavaniObrazovka
import com.machy1979.obchodnirejstrik.screens.home.ObchodniRejstrikViewModel
import com.machy1979.obchodnirejstrik.screens.home.UvodniObrazovka

@Composable
fun ObchodniRejstrikNavigation(
    activity: Activity?,

    ) {
    val navController = rememberNavController()
    val currentScreen =
        rememberSaveable { mutableStateOf(ObchodniRejstrikScreens.UvodniObrazovka.name) }

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

        animatedComposable(
            route = ObchodniRejstrikScreens.UvodniObrazovka.name,
            navController = navController
        ) {
            UvodniObrazovka(
                navController = it,
                viewModel = ObchodniRejstrikViewModel
            )
        }

        animatedComposable(
            route = ObchodniRejstrikScreens.HistorieVyhladavaniObrazovka.name,
            navController = navController
        ) {
            HistorieVyhledavaniObrazovka(
                navController = it,
                viewModel = ObchodniRejstrikViewModel,
                resViewModel = resViewModel,
                rzpViewModel = rzpViewModel,
                orViewModel = orViewModel
            )
        }

        animatedComposable(
            route = ObchodniRejstrikScreens.VypisFiremSeznamObrazovka.name,
            navController = navController
        ) {
            VypisFiremSeznamObrazovka(
                navController = it,
                viewModel = ObchodniRejstrikViewModel,
                resViewModel = resViewModel,
                rzpViewModel = rzpViewModel,
                orViewModel = orViewModel
            )
        }

        animatedComposable(
            route = ObchodniRejstrikScreens.VypisIcoObrazovka.name,
            navController = navController
        ) {
            VypisIcoObrazovka(
                navController = it,
                viewModel = ObchodniRejstrikViewModel,
                resViewModel = resViewModel,
                rzpViewModel = rzpViewModel,
                orViewModel = orViewModel
            )
        }

        animatedComposable(
            route = ObchodniRejstrikScreens.VypisORObrazovka.name,
            navController = navController
        ) {
            VypisORObrazovka(
                navController = it,
                viewModel = orViewModel,
                onClickedButtonIcoSubjekt = { clickedIco -> //tato funkce je pro butto ve výpisu, kde je u jednotlivých subjektů ico, aby šlo prokliknout
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

        animatedComposable(
            route = ObchodniRejstrikScreens.VypisRESObrazovka.name,
            navController = navController
        ) {
            VypisRESObrazovka(
                navController = it,
                viewModel = resViewModel,

                adsDisabled = adsDisabled

            )
        }

        animatedComposable(
            route = ObchodniRejstrikScreens.VypisRZPObrazovka.name,
            navController = navController
        ) {
            VypisRZPObrazovka(
                navController = it,
                viewModel = rzpViewModel,

                adsDisabled = adsDisabled

            )
        }

    }

}

//because of animation effect
fun NavGraphBuilder.animatedComposable(
    route: String,
    navController: NavHostController,
    content: @Composable (NavHostController) -> Unit,
) {
    composable(
        route = route,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up, tween(500)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Down, tween(500)

            )
        }
    ) {
        content(navController)
    }
}