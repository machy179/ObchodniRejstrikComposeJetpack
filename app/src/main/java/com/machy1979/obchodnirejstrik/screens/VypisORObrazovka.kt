package com.machy1979.obchodnirejstrik.screens.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp

import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.machy1979.obchodnirejstrik.components.ORNativeAdWrapped

import com.machy1979.obchodnirejstrik.ui.theme.*
import com.machy1979.obchodnirejstrik.viewmodel.ORViewModel

@Composable
fun VypisORObrazovka(
    viewModel: ORViewModel,
    onClickedButtonIcoSubjekt: (String) -> Unit,
    onCancelButtonClicked: () -> Unit = {},
    modifier: Modifier = Modifier,
    adsDisabled: State<Boolean>,
) {

    val companyDataFromOR by viewModel.companyDataFromOR.collectAsState()




    Column(
        modifier = Modifier
            .padding(VelikostPaddingHlavnihoOkna)
            .fillMaxHeight()
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
                    ObycPolozkaNadpisHodnota("Stav subjektu:", companyDataFromOR.stavSubjektu, true)
                    ObycPolozkaNadpisHodnota("Právní forma:", companyDataFromOR.pravniForma, true)
                    ObycPolozkaNadpisHodnota("Datum zápisu:", companyDataFromOR.datumZapisu, true)
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
                                ObycPolozkaNadpisHodnota("Vklad:", companyDataFromOR.vklad, true)
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