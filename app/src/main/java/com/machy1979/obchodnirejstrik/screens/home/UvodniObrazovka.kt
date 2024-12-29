package com.machy1979.obchodnirejstrik.screens.home

import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.machy1979.obchodnirejstrik.utils.TitlesOfSrceens
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.components.ORNativeAdLayout
import com.machy1979.obchodnirejstrik.components.ObchodniRejstrikAppBar
import com.machy1979.obchodnirejstrik.model.Query
import com.machy1979.obchodnirejstrik.navigation.ObchodniRejstrikScreens
import com.machy1979.obchodnirejstrik.components.CustomButton
import com.machy1979.obchodnirejstrik.components.ExpandableItemButton
import com.machy1979.obchodnirejstrik.components.ObycPolozkaHodnota
import com.machy1979.obchodnirejstrik.ui.theme.*
import com.machy1979.obchodnirejstrik.screens.extractor.ORViewModel
import com.machy1979.obchodnirejstrik.screens.extractres.RESViewModel
import com.machy1979.obchodnirejstrik.screens.extractrzp.RZPViewModel

@Composable
fun UvodniObrazovka(
    viewModel: ObchodniRejstrikViewModel = hiltViewModel(),
    resViewModel: RESViewModel = hiltViewModel(),
    rzpViewModel: RZPViewModel = hiltViewModel(),
    orViewModel: ORViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    var dotaz by rememberSaveable { mutableStateOf("") }
    var dotazMesto by rememberSaveable { mutableStateOf("") }

    //history list:
    val nactenoQueryList by viewModel.nactenoQueryList.collectAsState()
    val adsDisabled = viewModel.adsDisabled.collectAsState()

    val currentScreen = TitlesOfSrceens.valueOf(TitlesOfSrceens.UvodniObrazovka.name)


    Scaffold(

        topBar = {
            ObchodniRejstrikAppBar(
                currentScreen = currentScreen,
                canNavigateBack = false,
                share = { },
                saveToPdf = { },
                deleteAllHistory = { },
                canHistoryOfSearch = nactenoQueryList,
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
                ).padding(WindowInsets.navigationBars.asPaddingValues()) // Přidání prostoru pro navigation bar

                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fetching current app configuration
            val context = LocalContext.current
            val activity = context as? Activity

            val isLandscape =
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            var expanded by rememberSaveable { mutableStateOf(false) }
            val paddingModifierHlavniCard = if (isLandscape) {
                Modifier.padding(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 1.dp)
            } else {
                Modifier.padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 1.dp)
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
                Row(
                    modifier = paddingModifierHlavniCard.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
/*                Text(
                    text = "Historie vyhledávání -> ", //->   ˄˅
                    color = Color.DarkGray,
                    modifier = Modifier
                        .clickable {
                            zobrazHistoriiVyhledavani()
                        }
                )*/
                }
            }

            Card(
                elevation = VelikostElevation,
                modifier = if (!nactenoQueryList) {
                    paddingModifierHlavniCard
                } else {
                    Modifier.padding(start = 20.dp, end = 20.dp, top = 1.dp, bottom = 10.dp)
                },
                shape = RoundedCornerShape(VelikostZakulaceniRohuButtonTextField),
            ) {
                Column(
                    modifier = Modifier
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                ) {
                    Card(
                        elevation = VelikostElevation,
                        modifier = Modifier
                            .then(
                                if (expanded) Modifier
                                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 0.dp)
                                    .then(if (expanded) Modifier.fillMaxWidth() else Modifier)
                                    .padding(PaddingVButtonu)
                                else Modifier
                            ),
                        shape = RoundedCornerShape(VelikostZakulaceniRohuButtonTextFieldVnitrni),
                    ) {
                        OutlinedTextField(
                            value = dotaz,
                            onValueChange = {
                                dotaz = it
                            },
                            label = { Text("ICO nebo název subjektu") },
                            modifier = Modifier
                                .padding(PaddingVButtonu)
                                .fillMaxWidth(),
                            trailingIcon = {
                                Icon(
                                    painter = if (expanded) {
                                        painterResource(id = R.drawable.collapsed_icon)
                                    } else {
                                        painterResource(id = R.drawable.expandabled_icon)
                                    },
                                    contentDescription = "Search",
                                    modifier = Modifier
                                        .clickable { expanded = !expanded }
                                )
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.Black,
                                disabledTextColor = Color.Transparent,
                                backgroundColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                    }

                    Card(
                        elevation = VelikostElevation,
                        modifier = paddingModifierSpodniCard,
                        shape = RoundedCornerShape(VelikostZakulaceniRohuButtonTextFieldVnitrni),
                    ) {
                        if (expanded) {
                            OutlinedTextField(
                                value = dotazMesto,
                                onValueChange = {
                                    dotazMesto = it
                                },
                                label = { Text("Sídlo subjektu - nepovinné") },
                                modifier = Modifier
                                    .padding(PaddingVButtonu)
                                    .fillMaxWidth(),
                                colors = TextFieldDefaults.textFieldColors(
                                    textColor = Color.Black,
                                    disabledTextColor = Color.Transparent,
                                    backgroundColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }

            CustomButton("Načíst dle ICO", false, true,
                onClick = {
                    viewModel.loadDataIco(dotaz)
                    resViewModel.loadDataIcoRES(dotaz, context)
                    rzpViewModel.loadDataIcoRZP(dotaz, context)
                    orViewModel.loadDataIcoOR(dotaz, context)
                    navController.navigate(ObchodniRejstrikScreens.VypisIcoObrazovka.name)
                }
            )

            CustomButton("Načíst dle názvu", false, true,
                onClick = {
                    viewModel.vynulujCompanysData()
                    viewModel.loadDataNazev(
                        dotaz,
                        dotazMesto
                    )
                    navController.navigate(ObchodniRejstrikScreens.VypisFiremSeznamObrazovka.name)
                }
            )

            if (!adsDisabled.value) {
                var adIsLoaded by rememberSaveable { mutableStateOf(false) }
                ORNativeAdLayout { isLoaded ->
                    if (isLoaded) {
                        adIsLoaded = true
                    } else {
                        Log.d("NativeAd", "Failed to load ad")
                        adIsLoaded = false
                    }
                }

                if (adIsLoaded) {
                    CustomButton("Odstranění reklam", false, true,
                        onClick = {
                            activity?.let {
                                Log.d("remove ads Storky", "1")
                                viewModel.startPurchase(it)
                            }
                        }
                    )
                }

            }


        }
    }
    }

@Composable
fun ListOfHistory(
    queryList: List<Query>,
    expandHistory: () -> Unit,
    goToIcoButton: (String) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(true) }
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
            .fillMaxWidth()
            .animateContentSize( //efekt pro rozbalení
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),

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

                Text(
                    text = "Historie vyhledávání",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                        .weight(1f)
                )

                ExpandableItemButton(
                    expanded = expanded,
                    onClick = {
                        expanded = !expanded
                        expandHistory()
                    },
                    modifier = Modifier
                        .padding(0.dp)

                )
            }
            if (expanded) {
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
                                    // ObycPolozkaHodnota(query.address, false, false)
                                }
                            }
                        }
                    }
                }
            }


        }
        Spacer(modifier = Modifier.height(OdsazeniMensi))


    }


}




