package com.machy1979.obchodnirejstrik

import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.Icon

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Start
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Start
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.machy1979.obchodnirejstrik.R
import androidx.navigation.compose.rememberNavController
import com.machy1979.obchodnirejstrik.functions.PermissionsChecker
import com.machy1979.obchodnirejstrik.screens.*
import com.machy1979.obchodnirejstrik.screens.components.AlertDialogWrapper
import com.machy1979.obchodnirejstrik.screens.components.VypisORObrazovka
import com.machy1979.obchodnirejstrik.viewmodel.ORViewModel
import com.machy1979.obchodnirejstrik.viewmodel.ObchodniRejstrikViewModel
import com.machy1979.obchodnirejstrik.viewmodel.RESViewModel
import com.machy1979.obchodnirejstrik.viewmodel.RZPViewModel

/**
 * enum values that represent the screens in the app
 */

var canShare: Boolean = false

enum class ObchodniRejstrik (@StringRes val title: Int) {

    UvodniObrazovka(title = R.string.app_name),
    VypisFiremSeznam(title = R.string.vypis_firem_seznam),
    VypisIco(title = R.string.vypis_ico),
    VypisOR(title = R.string.vypis_or),
    VypisRZP(title = R.string.vypis_RZP),
    VypisRES(title = R.string.vypis_RES)


}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@Composable
fun ObchodniRejstrikAppBar(
    currentScreen: ObchodniRejstrik,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    share: () -> Unit,
    saveToPdf: () -> Unit,
    modifier: Modifier = Modifier
) {

    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }

            }
        },
        actions = {
            if (canShare) {

                Row(

                ) {
                IconButton(
                    onClick = share

                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.share_button)
                    )
                }
                    IconButton(
                        onClick = saveToPdf
                    ) {
                        Icon(
                            imageVector =  Icons.Filled.Download,
                            contentDescription = stringResource(R.string.share_button)
                        )
                    }
                }
            }
            }

    )
}


@Composable
fun ObchodniRejstrikApp2(
    modifier: Modifier = Modifier,
    viewModel: ObchodniRejstrikViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    resViewModel: RESViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    rzpViewModel: RZPViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    orViewModel: ORViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavHostController = rememberNavController()


) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = ObchodniRejstrik.valueOf(
        backStackEntry?.destination?.route ?: ObchodniRejstrik.UvodniObrazovka.name
    )
    val companyData by  viewModel.companyData.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current //tohle používat místo this

    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ObchodniRejstrikAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                saveToPdf = {
                    if (PermissionsChecker.checkStoragePermissions()) {
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
                share = {
                    when (navController.currentBackStackEntry?.destination?.route) { //zjistí v jaké aktuální destinaci route se aplikace nachází a podle toho zavolá tu správnou metodu
                        ObchodniRejstrik.VypisOR.name -> orViewModel.share(context)
                        ObchodniRejstrik.VypisRZP.name -> rzpViewModel.share(context)
                        ObchodniRejstrik.VypisRES.name -> resViewModel.share(context)
                    }

                    }



            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = ObchodniRejstrik.UvodniObrazovka.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = ObchodniRejstrik.UvodniObrazovka.name) {

                canShare = false
                UvodniObrazovka(
                    viewModel = viewModel,

                    hledejDleIcoButton = {
                        viewModel.loadDataIco(it)
                        resViewModel.loadDataIcoRES(it)
                        rzpViewModel.loadDataIcoRZP(it)
                        orViewModel.loadDataIcoOR(it,context)
                        navController.navigate(ObchodniRejstrik.VypisIco.name)
                    },
                    hledejDleNazvuButton = {
                        viewModel.loadDataNazev(it) //it dostane z UvodniObrazovka hledejDleNazvuButton - bude to to, co je napsané ve vyhldedávacím poli
                        navController.navigate(ObchodniRejstrik.VypisFiremSeznam.name)
                    }
                )
            }
            composable(route = ObchodniRejstrik.VypisIco.name) {
                val context = LocalContext.current
                canShare = false

                VypisIcoObrazovka(
                    viewModel = viewModel,
                    resViewModel = resViewModel,
                    rzpViewModel = rzpViewModel,
                    orViewModel = orViewModel,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    hledejORButtonClicked = {
                        navController.navigate(ObchodniRejstrik.VypisOR.name)
                    },
                   hledejRZPButtonClicked = {
                        navController.navigate(ObchodniRejstrik.VypisRZP.name)
                    },
                    hledejRESButtonClicked = {
                        navController.navigate(ObchodniRejstrik.VypisRES.name)
                    }
                )
            }
            composable(route = ObchodniRejstrik.VypisFiremSeznam.name) {
                canShare = false
                VypisFiremSeznamObrazovka(
                    viewModel = viewModel,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onCardClicked = {
                        viewModel.loadDataIco(it)
                        resViewModel.loadDataIcoRES(it)
                        rzpViewModel.loadDataIcoRZP(it)
                        orViewModel.loadDataIcoOR(it, context)
                        navController.navigate(ObchodniRejstrik.VypisIco.name)
                    }
                )
            }
            composable(route = ObchodniRejstrik.VypisOR.name) {
                val context = LocalContext.current
                canShare = true
                VypisORObrazovka(
                    viewModel = orViewModel,

                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    }
                )
            }
            composable(route = ObchodniRejstrik.VypisRZP.name) {
                val context = LocalContext.current
                canShare = true
                VypisRZPObrazovka(
                    viewModel = rzpViewModel,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    }
                )
            }
            composable(route = ObchodniRejstrik.VypisRES.name) {
                val context = LocalContext.current
                canShare = true
                VypisRESObrazovka(
                    viewModel = resViewModel,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    }
                )
            }

        }
        if (showDialog.value) {
            AlertDialogWrapper(
                onClickPovolit = {
                println("Oprávnění .....44444-4")
                showDialog.value = false
                println("Oprávnění .....5555555-5")
            },
                onClickNe = { showDialog.value = false },
                onDismissFunction = { showDialog.value = false }
            )
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


