package com.machy1979.obchodnirejstrik.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.model.SharedState
import com.machy1979.obchodnirejstrik.navigation.ObchodniRejstrikScreens
import com.machy1979.obchodnirejstrik.utils.TitlesOfSrceens

@Composable
fun ObchodniRejstrikAppBar(
    currentScreen: TitlesOfSrceens,
    canNavigateBack: Boolean,
    canShare: Boolean = false,
    share: () -> Unit,
    saveToPdf: () -> Unit,
    canDeleteButton: Boolean = false,
    deleteAllHistory: () -> Unit,
    canHistoryOfSearch: Boolean = false,
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    var appBarOffset by remember { mutableStateOf(0f) }
    val saveToPdfClickedState by SharedState.saveToPdfClicked.collectAsState()


    TopAppBar(
        title = {
            Text(
                stringResource(currentScreen.title),
                color = colorResource(id = R.color.pozadi_prvku_top_app_bar)
            )
        },
        modifier = modifier,
        backgroundColor = Color.Transparent, // Nastavíme transparentní barvu pozadí
        elevation = if (appBarOffset > 0) 4.dp else 0.dp, // Přidáme stín, pokud je appBarOffset větší než 0

        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                        tint = colorResource(id = R.color.pozadi_prvku_top_app_bar)
                    )
                }

            }
        },
        actions = {
            if (canNavigateBack) {

                Row(

                ) {
                    if (canShare && !saveToPdfClickedState) {
                        IconButton(onClick = share) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = stringResource(R.string.share_button),
                                tint = colorResource(id = R.color.pozadi_prvku_top_app_bar)
                            )
                        }
                        IconButton(onClick = {
                            saveToPdf()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = stringResource(R.string.share_button),
                                tint = colorResource(id = R.color.pozadi_prvku_top_app_bar)
                            )
                        }
                    }

                    if (canDeleteButton) {
                        IconButton(onClick = deleteAllHistory) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.delete_history_button),
                                tint = colorResource(id = R.color.pozadi_prvku_top_app_bar)
                            )
                        }
                    } else {
                        IconButton(onClick = { navController.navigate(TitlesOfSrceens.UvodniObrazovka.name) }) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = stringResource(R.string.home_button),
                                tint = colorResource(id = R.color.pozadi_prvku_top_app_bar)
                            )
                        }
                    }
                }
            } else if (canHistoryOfSearch) {
                Row(
                    modifier = Modifier
                        .padding(end = 12.dp, top = 0.dp)
                        .clickable {
                            navController.navigate(ObchodniRejstrikScreens.HistorieVyhladavaniObrazovka.name)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Historie",
                        color = colorResource(id = R.color.pozadi_prvku_top_app_bar),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = stringResource(R.string.history_of_search_button),
                        tint = colorResource(id = R.color.pozadi_prvku_top_app_bar)
                    )
                }

            }

        }

    )

}