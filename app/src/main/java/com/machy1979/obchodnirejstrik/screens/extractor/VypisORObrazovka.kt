package com.machy1979.obchodnirejstrik.screens.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.machy1979.obchodnirejstrik.components.ExpandableItemButton
import com.machy1979.obchodnirejstrik.components.ORNativeAdWrapped
import com.machy1979.obchodnirejstrik.components.ObchodniRejstrikAppBar
import com.machy1979.obchodnirejstrik.components.ObycPolozkaJenNadpisUprostred
import com.machy1979.obchodnirejstrik.components.ObycPolozkaNadpisHodnota
import com.machy1979.obchodnirejstrik.components.SeznamOsob
import com.machy1979.obchodnirejstrik.components.SeznamOsobAFirem
import com.machy1979.obchodnirejstrik.components.SeznamPolozek
import com.machy1979.obchodnirejstrik.screens.extractor.ORViewModel
import com.machy1979.obchodnirejstrik.ui.theme.ColorBorderStroke
import com.machy1979.obchodnirejstrik.ui.theme.OdsazeniMensi
import com.machy1979.obchodnirejstrik.ui.theme.PaddingTopAplikace
import com.machy1979.obchodnirejstrik.ui.theme.VelikostBorderStrokeCard
import com.machy1979.obchodnirejstrik.ui.theme.VelikostElevation
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardHorizontal
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardVertical
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingMezeryMeziHlavnimiZaznamy
import com.machy1979.obchodnirejstrik.ui.theme.VelikostZakulaceniRohu
import com.machy1979.obchodnirejstrik.utils.TitlesOfSrceens

@Composable
fun VypisORObrazovka(
    viewModel: ORViewModel,
    orViewModel: ORViewModel,
    onClickedButtonIcoSubjekt: (String) -> Unit,
    adsDisabled: State<Boolean>,
    navController: NavHostController,
) {

    val companyDataFromOR by viewModel.companyDataFromOR.collectAsState()

    val context = LocalContext.current
    val currentScreen = TitlesOfSrceens.valueOf(TitlesOfSrceens.VypisOR.name)


    Scaffold(
        backgroundColor = Color.Transparent,
        topBar = {
            ObchodniRejstrikAppBar(
                currentScreen = currentScreen,
                canNavigateBack = true,
                canShare = true,
                share = { orViewModel.share(context) },
                saveToPdf = { orViewModel.saveToPdf(context) },
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
                .fillMaxWidth().padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
        )
        {
            //základní údaje
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
                    .fillMaxWidth(),

                ) {
                SelectionContainer {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                    ) {

                        ObycPolozkaJenNadpisUprostred("Základní údaje subjektu", false)
                        ObycPolozkaNadpisHodnota("Název firmy:", companyDataFromOR.name, true)
                        ObycPolozkaNadpisHodnota("Ico:", companyDataFromOR.ico, true)
                        ObycPolozkaNadpisHodnota("Sídlo:", companyDataFromOR.address, true, true)
                        ObycPolozkaNadpisHodnota(
                            "Stav subjektu:",
                            companyDataFromOR.stavSubjektu,
                            true
                        )
                        ObycPolozkaNadpisHodnota(
                            "Právní forma:",
                            companyDataFromOR.pravniForma,
                            true
                        )
                        ObycPolozkaNadpisHodnota(
                            "Datum zápisu:",
                            companyDataFromOR.datumZapisu,
                            true
                        )
                        ObycPolozkaNadpisHodnota("Soud:", companyDataFromOR.soud, true)
                        ObycPolozkaNadpisHodnota(
                            "Spisová značka:",
                            companyDataFromOR.spisovaZnacka,
                            false
                        )


                    }
                }
            }

            Spacer(modifier = Modifier.height(OdsazeniMensi))

            if (companyDataFromOR.predmetPodnikani.size != 0) {
                SeznamPolozek(
                    nazevSeznamuPolozek = "Předmět podnikání:",
                    seznamPolozek = companyDataFromOR.predmetPodnikani
                )
            }
            if (companyDataFromOR.ostatniSkutecnosti.size != 0) {
                SeznamPolozek(
                    nazevSeznamuPolozek = "Ostatní skutečnosti:",
                    seznamPolozek = companyDataFromOR.ostatniSkutecnosti
                )
            }
            //kapitál
            if (companyDataFromOR.vklad != "" || companyDataFromOR.splaceno != "") {
                var expanded by remember { mutableStateOf(true) }
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
                        )
                        .fillMaxWidth()
                        .animateContentSize( //efekt pro rozbalení
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),

                    ) {
                    SelectionContainer {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(2.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(2.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // ObycPolozkaJenNadpisUprostred("Kapitál:", false)
                                Text(
                                    text = "Kapitál:",
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                        .weight(1f)
                                )
                                ExpandableItemButton(
                                    expanded = expanded,
                                    onClick = { expanded = !expanded },
                                    modifier = Modifier
                                        .padding(0.dp)

                                )
                            }
                            if (expanded) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .padding(horizontal = VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                ) {
                                    ObycPolozkaNadpisHodnota(
                                        "Vklad:",
                                        companyDataFromOR.vklad,
                                        true
                                    )
                                    ObycPolozkaNadpisHodnota(
                                        "Splaceno:",
                                        companyDataFromOR.splaceno,
                                        true
                                    )
                                    companyDataFromOR.akcie.forEach {
                                        ObycPolozkaNadpisHodnota("Akcie:", it, true)
                                    }
                                }
                            }


                        }
                    }
                }
                Spacer(modifier = Modifier.height(OdsazeniMensi))


            }

            if (companyDataFromOR.statutarniOrganOsoby.size != 0 || companyDataFromOR.statutarniOrganFirmy.size != 0 || companyDataFromOR.statutarniOrganSkutecnosti.size != 0) {
                SeznamOsobAFirem(
                    onClickedButtonIcoSubjekt = onClickedButtonIcoSubjekt,
                    nazevSeznamuOsobAFirem = "Statutární orgán:",
                    seznamOsob = companyDataFromOR.statutarniOrganOsoby,
                    seznamFirem = companyDataFromOR.statutarniOrganFirmy,
                    dalsiTextSeznam = companyDataFromOR.statutarniOrganSkutecnosti
                )
            }
            if (companyDataFromOR.prokura.size != 0) {
                SeznamOsob(nazevSeznamuOsob = "Prokura:", seznamOsob = companyDataFromOR.prokura)
            }
            if (companyDataFromOR.dozorciRada.size != 0) {
                SeznamOsob(
                    nazevSeznamuOsob = "Dozorčí rada:",
                    seznamOsob = companyDataFromOR.dozorciRada
                )
            }
            if (companyDataFromOR.spolecniciSVklademOsoby.size != 0 || companyDataFromOR.spolecniciSVklademFirmy.size != 0) {
                SeznamOsobAFirem(
                    onClickedButtonIcoSubjekt = onClickedButtonIcoSubjekt,
                    nazevSeznamuOsobAFirem = "Společníci s vkladem:",
                    seznamOsob = companyDataFromOR.spolecniciSVklademOsoby,
                    seznamFirem = companyDataFromOR.spolecniciSVklademFirmy
                )
            }
            if (companyDataFromOR.akcionariOsoby.size != 0 || companyDataFromOR.akcionariFirmy.size != 0) {
                SeznamOsobAFirem(
                    onClickedButtonIcoSubjekt = onClickedButtonIcoSubjekt,
                    nazevSeznamuOsobAFirem = "Akcionáři:",
                    seznamOsob = companyDataFromOR.akcionariOsoby,
                    seznamFirem = companyDataFromOR.akcionariFirmy
                )
            }

            if (companyDataFromOR.likvidaceOsoby.size != 0 || companyDataFromOR.likvidaceFirmy.size != 0) {
                SeznamOsobAFirem(
                    onClickedButtonIcoSubjekt = onClickedButtonIcoSubjekt,
                    nazevSeznamuOsobAFirem = "Likvidace:",
                    seznamOsob = companyDataFromOR.likvidaceOsoby,
                    seznamFirem = companyDataFromOR.likvidaceFirmy
                )
            }

            if (companyDataFromOR.vedouciOrganizacniSlozky.size != 0) {
                SeznamOsob(
                    nazevSeznamuOsob = "Vedoucí organizační složky:",
                    seznamOsob = companyDataFromOR.vedouciOrganizacniSlozky
                )
            }

            if (!adsDisabled.value) {
                ORNativeAdWrapped()
            }
        }
    }
}