package com.machy1979.obchodnirejstrik

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Start
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.machy1979.obchodnirejstrik.screens.*
import com.machy1979.obchodnirejstrik.screens.components.VypisORObrazovka
import com.machy1979.obchodnirejstrik.viewmodel.ObchodniRejstrikViewModel
import com.machy1979.obchodnirejstrik.viewmodel.RESViewModel

/**
 * enum values that represent the screens in the app
 */
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
        }
    )
}


@Composable
fun ObchodniRejstrikApp2(
    modifier: Modifier = Modifier,
    viewModel: ObchodniRejstrikViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    resViewModel: RESViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
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

    Scaffold(
        topBar = {
            ObchodniRejstrikAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
     // val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = ObchodniRejstrik.UvodniObrazovka.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = ObchodniRejstrik.UvodniObrazovka.name) {
                UvodniObrazovka(
                    viewModel = viewModel,
                    hledejDleIcoButton = {
                        viewModel.loadDataIco(it)
                        resViewModel.loadDataIcoRES(it)
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
                VypisIcoObrazovka(
                    viewModel = viewModel,
                    resViewModel = resViewModel,
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
                VypisFiremSeznamObrazovka(
                    viewModel = viewModel,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onCardClicked = {
                        viewModel.loadDataIco(it)
                        resViewModel.loadDataIcoRES(it)
                        navController.navigate(ObchodniRejstrik.VypisIco.name)
                    }
                )
            }
            composable(route = ObchodniRejstrik.VypisOR.name) {
                val context = LocalContext.current
                VypisORObrazovka(
                    viewModel = viewModel,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    }
                )
            }
            composable(route = ObchodniRejstrik.VypisRZP.name) {
                val context = LocalContext.current
                VypisRZPObrazovka(
                    viewModel = viewModel,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    }
                )
            }
            composable(route = ObchodniRejstrik.VypisRES.name) {
                val context = LocalContext.current
                VypisRESObrazovka(
                    viewModel = resViewModel,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    }
                )
            }

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

/**
 * Creates an intent to share order details
 */
private fun shareOrder(context: Context, subject: String, summary: String) {
    // Create an ACTION_SEND implicit intent with order details in the intent extras
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.app_name)
        )
    )
}

