package com.machy1979.obchodnirejstrik
import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast


import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign.Companion.Start

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

import androidx.navigation.compose.rememberNavController
import com.machy1979.obchodnirejstrik.model.SharedState

import com.machy1979.obchodnirejstrik.screens.*
import com.machy1979.obchodnirejstrik.components.AlertDialogWrapperOpravneni
import com.machy1979.obchodnirejstrik.components.MyLinearProgressIndicator
import com.machy1979.obchodnirejstrik.screens.components.VypisORObrazovka
import com.machy1979.obchodnirejstrik.ui.theme.PaddingTopAplikace
import com.machy1979.obchodnirejstrik.screens.extractor.ORViewModel
import com.machy1979.obchodnirejstrik.screens.home.ObchodniRejstrikViewModel
import com.machy1979.obchodnirejstrik.screens.extractres.RESViewModel
import com.machy1979.obchodnirejstrik.screens.extractrzp.RZPViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.machy1979.obchodnirejstrik.components.ObchodniRejstrikAppBar
import com.machy1979.obchodnirejstrik.functions.GDPRManager
import com.machy1979.obchodnirejstrik.screens.companylist.VypisFiremSeznamObrazovka
import com.machy1979.obchodnirejstrik.screens.extractico.VypisIcoObrazovka
import com.machy1979.obchodnirejstrik.screens.extractres.VypisRESObrazovka
import com.machy1979.obchodnirejstrik.screens.extractrzp.VypisRZPObrazovka
import com.machy1979.obchodnirejstrik.screens.history.HistorieVyhledavaniObrazovka
import com.machy1979.obchodnirejstrik.screens.home.UvodniObrazovka


/**
Jedná se o starý přístup, všechno bylo tady, po úpravách tady nechat asi jen ObchodniRejstrik enum class, ObchodniRejstrikAppBar přesunout do samostnatného souboru a zbytek smazat

 */


var canNavigateBack: Boolean = false

enum class ObchodniRejstrik (@StringRes val title: Int) {

  //  UvodniObrazovka(title = R.string.app_name),
    UvodniObrazovka(title = R.string.prazdny_retezec),
    VypisFiremSeznam(title = R.string.vypis_firem_seznam),
    VypisIco(title = R.string.vypis_ico),
    VypisOR(title = R.string.vypis_or),
    VypisRZP(title = R.string.vypis_RZP),
    VypisRES(title = R.string.vypis_RES),
    HistorieVyhledavani(title = R.string.history_queries)


}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */

@Composable
fun ObchodniRejstrikApp2(
    modifier: Modifier = Modifier,
    viewModel: ObchodniRejstrikViewModel = hiltViewModel(),
    resViewModel: RESViewModel = viewModel(),
    rzpViewModel: RZPViewModel = viewModel(),
    orViewModel: ORViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    activity: Activity


) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = ObchodniRejstrik.valueOf(
        backStackEntry?.destination?.route ?: ObchodniRejstrik.UvodniObrazovka.name
    )

    val context = LocalContext.current //tohle používat místo this

    val showDialog = remember { mutableStateOf(false) }
    val saveToPdfClickedState by SharedState.saveToPdfClicked.collectAsState()

    var showToastHistoryDeleted = remember { mutableStateOf(false) }
    val nactenoQueryList by viewModel.nactenoQueryList.collectAsState()
    val adsDisabled = viewModel.adsDisabled.collectAsState()

    //je třeba to mít zde, když to bylo v MainActivity, tak rychlé telefony s Android 13 a výš to házelo chybu uživatelům
    ShowGDPRMessage(activity)

    Scaffold(
        topBar = {
            ObchodniRejstrikAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null && navController.currentBackStackEntry?.destination?.route !=ObchodniRejstrik.UvodniObrazovka.name,

                share = {
                    when (navController.currentBackStackEntry?.destination?.route) { //zjistí v jaké aktuální destinaci route se aplikace nachází a podle toho zavolá tu správnou metodu
                        ObchodniRejstrik.VypisOR.name -> orViewModel.share(context)
                        ObchodniRejstrik.VypisRZP.name -> rzpViewModel.share(context)
                        ObchodniRejstrik.VypisRES.name -> resViewModel.share(context)
                    }

                    },
                saveToPdf = {
                //    if (PermissionsChecker.checkStoragePermissions()) { nakonec nebylo třeba checkovat, není potřeba povolení
                    if (true) {
                        //oprávnění zápisu uděleno
                        when (navController.currentBackStackEntry?.destination?.route) { //zjistí v jaké aktuální destinaci route se aplikace nachází a podle toho zavolá tu správnou metodu
                            ObchodniRejstrik.VypisOR.name -> orViewModel.saveToPdf(context)
                            ObchodniRejstrik.VypisRZP.name -> rzpViewModel.saveToPdf(context)
                            ObchodniRejstrik.VypisRES.name -> resViewModel.share(context)
                        }
                    } else {
                        //není uděleno oprávnění z kápisu, spustí se Dialog a uživatel se nasměruje do systému na udělení oprávnění
                        showDialog.value = true //v compose musím řešit showDialog takto, protože sem se tato komponenta nemůže dát, musí se dát na místo, kde se mohou dávat compose funcions a řešil jsem to takot: if (showDialog.value) {AlertDialogWrapper(....


                    }
                },
                 canDeleteButton =
                    navController.currentBackStackEntry?.destination?.route == ObchodniRejstrik.HistorieVyhledavani.name,
                deleteAllHistory = {
                    viewModel.deleteAllHistory()
                    showToastHistoryDeleted.value = true
                },
                canHistoryOfSearch = nactenoQueryList,
                modifier = Modifier
                    .padding(top = PaddingTopAplikace)
                    .fillMaxWidth(),
                navController = navController


            )
        }
    )


    { innerPadding ->
        if (saveToPdfClickedState) MyLinearProgressIndicator() //pokud uživatel stisknul ulož do pdf, tak se spustí progress indicátor
        NavHost(
            navController = navController,
            startDestination = ObchodniRejstrik.UvodniObrazovka.name,
            modifier = modifier.padding(innerPadding)
        ) {

            composable(route = ObchodniRejstrik.UvodniObrazovka.name) {


                UvodniObrazovka(
                    viewModel = viewModel,

                    navController = navController
                )
            }
            composable(route = ObchodniRejstrik.VypisIco.name) {
                val context = LocalContext.current

                VypisIcoObrazovka(
                    viewModel = viewModel,
                    resViewModel = resViewModel,
                    rzpViewModel = rzpViewModel,
                    orViewModel = orViewModel,

                    navController = navController
                )
            }
            composable(route = ObchodniRejstrik.VypisFiremSeznam.name) {
                VypisFiremSeznamObrazovka(
                    viewModel = viewModel,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onCardClicked = {
                        Log.i("clickedIt: ", "111")
                        Log.i("clickedIt: ", it)
                        if (!it.equals(" ")) {
                            Log.i("clickedIt: ", "222")
                            viewModel.loadDataIco(it)
                            resViewModel.loadDataIcoRES(it, context)
                            rzpViewModel.loadDataIcoRZP(it, context)
                            orViewModel.loadDataIcoOR(it, context)
                            navController.navigate(ObchodniRejstrik.VypisIco.name)
                        }
                    },
                    navController = navController,
                    resViewModel = resViewModel,
                    rzpViewModel = rzpViewModel,
                    orViewModel = orViewModel
                )
            }
            composable(route = ObchodniRejstrik.VypisOR.name) {
                val context = LocalContext.current

                VypisORObrazovka(
                    viewModel = orViewModel,
                    onClickedButtonIcoSubjekt = {  clickedIco -> //tato funkce je pro butto ve výpisu, kde je u jednotlivých subjektů ico, aby šlo prokliknout
                        viewModel.loadDataIco(clickedIco)
                        resViewModel.loadDataIcoRES(clickedIco, context)
                        rzpViewModel.loadDataIcoRZP(clickedIco, context)
                        orViewModel.loadDataIcoOR(clickedIco, context)
                        navController.navigate(ObchodniRejstrik.VypisIco.name)
                                                },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    adsDisabled = adsDisabled,
                    orViewModel = orViewModel,
                    navController = navController
                )
            }
            composable(route = ObchodniRejstrik.VypisRZP.name) {
                val context = LocalContext.current
                VypisRZPObrazovka(
                    viewModel = rzpViewModel,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    adsDisabled = adsDisabled,
                    navController = navController
                )
            }
            composable(route = ObchodniRejstrik.VypisRES.name) {
                val context = LocalContext.current
                VypisRESObrazovka(
                    viewModel = resViewModel,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    adsDisabled = adsDisabled,
                    navController = navController
                )
            }
            composable(route = ObchodniRejstrik.HistorieVyhledavani.name) {
                val context = LocalContext.current
                HistorieVyhledavaniObrazovka(
                    viewModel = viewModel,
                    navController = navController,

                    resViewModel = resViewModel,
                    rzpViewModel = rzpViewModel,
                    orViewModel = orViewModel
                )
            }

        }
        if (showDialog.value) { //nakonec nepoužito, nechám to tady zatím, pokud v budoucnu budu chtít použít
            AlertDialogWrapperOpravneni(
                onClickPovolit = {
                showDialog.value = false
            },
                onClickNe = { showDialog.value = false },
                onDismissFunction = { showDialog.value = false }
            )
        }

        if (showToastHistoryDeleted.value) { //nakonec nepoužito, nechám to tady zatím, pokud v budoucnu budu chtít použít
    //        toastHistoryDeleted(context)
            showToastHistoryDeleted.value = false

        }
    }
}

/**
 * Resets the [OrderUiState] and pops up to [CupcakeScreen.Start]
 */
private fun cancelOrderAndNavigateToStart(
    viewModel: ObchodniRejstrikViewModel,
    navController: NavHostController
) {
 //   viewModel.resetOrder()
    navController.popBackStack(ObchodniRejstrik.UvodniObrazovka.name, inclusive = false)
}






