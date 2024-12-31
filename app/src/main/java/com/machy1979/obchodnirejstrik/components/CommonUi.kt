package com.machy1979.obchodnirejstrik.components


import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.functions.StringToGpsToMap
import com.machy1979.obchodnirejstrik.model.Firma
import com.machy1979.obchodnirejstrik.model.Nace
import com.machy1979.obchodnirejstrik.model.Osoba
import com.machy1979.obchodnirejstrik.model.Zivnosti
import com.machy1979.obchodnirejstrik.ui.theme.ColorBorderStroke
import com.machy1979.obchodnirejstrik.ui.theme.OdsazeniMensi
import com.machy1979.obchodnirejstrik.ui.theme.PaddingLinearProgressIndicatoru
import com.machy1979.obchodnirejstrik.ui.theme.PaddingVButtonu
import com.machy1979.obchodnirejstrik.ui.theme.PozadiTextu
import com.machy1979.obchodnirejstrik.ui.theme.ProgressIndicatorColor
import com.machy1979.obchodnirejstrik.ui.theme.ProgressIndicatorSize
import com.machy1979.obchodnirejstrik.ui.theme.ProgressIndicatorSizeButtons
import com.machy1979.obchodnirejstrik.ui.theme.VelikostBorderStrokeCard
import com.machy1979.obchodnirejstrik.ui.theme.VelikostElevation
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardHorizontal
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardVertical
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingMezeryMeziHlavnimiZaznamy
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingTextuHlavniZaznamy
import com.machy1979.obchodnirejstrik.ui.theme.VelikostSpodniOdsazeni
import com.machy1979.obchodnirejstrik.ui.theme.VelikostZakulaceniRohu
import com.machy1979.obchodnirejstrik.ui.theme.VelikostZakulaceniRohuButton

@Composable
fun VypisErrorHlasku(errorMessage: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {

        Text(
            text = errorMessage,
            color = Color.Red,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = VelikostPaddingMezeryMeziHlavnimiZaznamy)
        )

    }
}

@Composable
fun Nacitani() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        ProgressIndicatorLoading(
            progressIndicatorSize = ProgressIndicatorSize,
            progressIndicatorColor = ProgressIndicatorColor
        )

    }
}

@Composable
fun SeznamPolozekZivnosti(nazevSeznamuPolozek: String, seznamZivnosti: MutableList<Zivnosti>) {
    Card(
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
        ) {
            if (!(nazevSeznamuPolozek == "")) {
                Text(
                    text = nazevSeznamuPolozek,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 3.dp)
                )


            }

            Column(modifier = Modifier) {
                seznamZivnosti.forEach {
                    Card(
                        shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
                        border = BorderStroke(
                            width = VelikostBorderStrokeCard,
                            color = ColorBorderStroke
                        ),
                        elevation = VelikostElevation,
                        modifier = Modifier
                            .padding(vertical = VelikostPaddingCardVertical)
                            .fillMaxWidth(),

                        ) {
                        SelectionContainer {
                            Column(
                                modifier = Modifier
                                    .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                    .fillMaxWidth()
                            ) {

                                ObycPolozkaNadpisHodnota("Název:", it.nazevZivnosti, true)
                                ObycPolozkaNadpisHodnota("Druh:", it.druhZivnosti, true)
                                ObycPolozkaNadpisHodnota(
                                    "Vznik oprávnění:",
                                    it.vznikOpravneni,
                                    true
                                )
                                ObycPolozkaNadpisHodnota(
                                    "Obory:",
                                    " ",
                                    false
                                ) //tady je potřeba do hodnota dát " " a ne jen "", protože SelectionContainer házel chybu
                                it.obory.forEach {
                                    ObycPolozkaHodnota(it.toString(), true, false)

                                }

                            }
                        }


                    }
                }
            }

        }


        Spacer(modifier = Modifier.height(OdsazeniMensi))
    }
    Spacer(modifier = Modifier.height(OdsazeniMensi))
}

@Composable
fun SeznamPolozek(nazevSeznamuPolozek: String, seznamPolozek: MutableList<String>) {
    Card(
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
        SeznamPolozekBezCard2(
            nazevSeznamuPolozek = nazevSeznamuPolozek,
            seznamPolozek = seznamPolozek
        )
    }
    Spacer(modifier = Modifier.height(OdsazeniMensi))
}

@Composable
fun SeznamPolozekBezCard(nazevSeznamuPolozek: String, seznamPolozek: MutableList<String>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
    ) {
        if (!(nazevSeznamuPolozek == "")) {
            Text(
                text = nazevSeznamuPolozek,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 3.dp)
            )


        }
        SelectionContainer {
            Column(modifier = Modifier) {
                seznamPolozek.forEach {
                    Column {
                        Text(
                            "- " + it.toString(),
                            modifier = Modifier
                                .padding(vertical = 3.dp)
                                .background(color = PozadiTextu)
                                .fillMaxWidth()
                        )

                    }


                }
            }
        }

    }


    Spacer(modifier = Modifier.height(OdsazeniMensi))
}

@Composable
fun SeznamOsob(
    nazevSeznamuOsob: String,
    seznamOsob: MutableList<Osoba>,
    dalsiTextSeznam: MutableList<String> = mutableListOf<String>(),
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
                    text = nazevSeznamuOsob,
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
                Column(modifier = Modifier) {
                    seznamOsob.forEach {
                        Column {
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
                                    .fillMaxWidth(),

                                ) {
                                SelectionContainer {
                                    Column(
                                        modifier = Modifier
                                            .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                            .fillMaxWidth()
                                    ) {
                                        if (!(it.funkce == "")) {
                                            ObycPolozkaNadpisHodnota("Funkce:", it.funkce, true)
                                        }
                                        if (!(it.organizacniSlozka == "")) {
                                            ObycPolozkaNadpisHodnota(
                                                "Org. složka:",
                                                it.organizacniSlozka,
                                                true
                                            )
                                        }
                                        ObycPolozkaNadpisHodnota(
                                            "Jméno:", if (it.titulyPredJmenem == "") {
                                                it.jmeno + " " + it.prijmeni
                                            } else {
                                                it.titulyPredJmenem + " " + it.jmeno + " " + it.prijmeni
                                            }, true
                                        )
                                        ObycPolozkaNadpisHodnota("Dat. nar.:", it.datNar, true)
                                        ObycPolozkaNadpisHodnota("Bydliště:", it.adresa, true, true)
                                        if (!(it.clenstviOd == "")) ObycPolozkaNadpisHodnota(
                                            "Členství od:",
                                            it.clenstviOd,
                                            false
                                        )
                                        if (!(it.veFunkciOd == "")) ObycPolozkaNadpisHodnota(
                                            "Ve funkci od:",
                                            it.veFunkciOd,
                                            false
                                        )
                                        if (!(it.vklad == "")) ObycPolozkaNadpisHodnota(
                                            "Vklad:",
                                            it.vklad + " Kč",
                                            false
                                        )
                                        if (!(it.splaceno == "")) ObycPolozkaNadpisHodnota(
                                            "Splaceno:",
                                            it.splaceno + " %",
                                            false
                                        )
                                        if (!(it.obchodniPodil == "")) ObycPolozkaNadpisHodnota(
                                            "Obchodní podíl:",
                                            it.obchodniPodil,
                                            false
                                        )

                                        Column(modifier = Modifier) {
                                            it.poznamky.forEach {
                                                Column {
                                                    Text(
                                                        "- " + it.toString(),
                                                        modifier = Modifier.padding(vertical = 3.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }


                        }
                    }
                    if (!(dalsiTextSeznam.isEmpty())) {
                        SeznamPolozekBezCard("", dalsiTextSeznam)
                    }

                }
            }


        }
        Spacer(modifier = Modifier.height(OdsazeniMensi))
    }
}

@Composable
fun SeznamOsobAFirem(
    onClickedButtonIcoSubjekt: (String) -> Unit = {},
    nazevSeznamuOsobAFirem: String,
    seznamOsob: MutableList<Osoba>,
    seznamFirem: MutableList<Firma>,
    dalsiTextSeznam: MutableList<String> = mutableListOf<String>(),
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
                    text = nazevSeznamuOsobAFirem,
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
                Column(modifier = Modifier) {
                    seznamOsob.forEach {
                        Column {
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
                                    .fillMaxWidth(),

                                ) {
                                SelectionContainer {
                                    Column(
                                        modifier = Modifier
                                            .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                            .fillMaxWidth()
                                    ) {
                                        if (!(it.funkce == "")) {
                                            ObycPolozkaNadpisHodnota("Funkce:", it.funkce, true)
                                        }
                                        ObycPolozkaNadpisHodnota(
                                            "Jméno:", if (it.titulyPredJmenem == "") {
                                                it.jmeno + " " + it.prijmeni
                                            } else {
                                                it.titulyPredJmenem + " " + it.jmeno + " " + it.prijmeni
                                            }, true
                                        )
                                        ObycPolozkaNadpisHodnota("Dat. nar.:", it.datNar, true)
                                        ObycPolozkaNadpisHodnota("Bydliště:", it.adresa, true, true)
                                        if (!(it.clenstviOd == "")) ObycPolozkaNadpisHodnota(
                                            "Členství od:",
                                            it.clenstviOd,
                                            false
                                        )
                                        if (!(it.veFunkciOd == "")) ObycPolozkaNadpisHodnota(
                                            "Ve funkci od:",
                                            it.veFunkciOd,
                                            false
                                        )
                                        if (!(it.vklad == "")) ObycPolozkaNadpisHodnota(
                                            "Vklad:",
                                            it.vklad,
                                            false
                                        )
                                        if (!(it.splaceno == "")) ObycPolozkaNadpisHodnota(
                                            "Splaceno:",
                                            it.splaceno,
                                            false
                                        )
                                        if (!(it.obchodniPodil == "")) ObycPolozkaNadpisHodnota(
                                            "Obchodní podíl:",
                                            it.obchodniPodil,
                                            false
                                        )

                                        Column(modifier = Modifier) {
                                            it.poznamky.forEach {
                                                Column {
                                                    Text(
                                                        "- " + it.toString(),
                                                        modifier = Modifier.padding(vertical = 3.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }


                        }
                    }

                    seznamFirem.forEach {
                        Column {
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
                                    .fillMaxWidth(),

                                ) {
                                SelectionContainer {
                                    Column(
                                        modifier = Modifier
                                            .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                            .fillMaxWidth()
                                    ) {
/*                                    if (!(it.funkce=="")) {
                                        ObycPolozkaNadpisHodnota("Funkce:", it.funkce, true)
                                    }*/
                                        ObycPolozkaNadpisHodnota("Firma:", it.name, true)
                                        ObycPolozkaNadpisHodnota(
                                            "ICO:",
                                            it.ico,
                                            true,
                                            false,
                                            onClickedButtonIcoSubjekt
                                        )
                                        ObycPolozkaNadpisHodnota("Sídlo:", it.address, false, true)
                                        /*                                    if(!(it.clenstviOd=="")) ObycPolozkaNadpisHodnota("Členství od:", it.clenstviOd, false)
                                                                            if(!(it.veFunkciOd=="")) ObycPolozkaNadpisHodnota("Ve funkci od:", it.veFunkciOd, false)*/
                                        if (!(it.vklad == "")) ObycPolozkaNadpisHodnota(
                                            "Vklad:",
                                            it.vklad,
                                            false
                                        )
                                        if (!(it.splaceno == "")) ObycPolozkaNadpisHodnota(
                                            "Splaceno:",
                                            it.splaceno,
                                            false
                                        )
                                        if (!(it.obchodniPodil == "")) ObycPolozkaNadpisHodnota(
                                            "Obchodní podíl:",
                                            it.obchodniPodil,
                                            false
                                        )
                                        /*
                                                                            Column(modifier = Modifier) {
                                                                                it.poznamky.forEach {
                                                                                    Column {
                                                                                        Text(
                                                                                            "- "+it.toString(),
                                                                                            modifier = Modifier.padding(vertical = 3.dp)
                                                                                        )
                                                                                    }
                                                                                }
                                                                            }*/
                                    }
                                }
                            }


                        }
                    }

                    if (!(dalsiTextSeznam.isEmpty())) {
                        SeznamPolozekBezCard("", dalsiTextSeznam)
                    }

                }
            }


        }
        Spacer(modifier = Modifier.height(OdsazeniMensi))
    }
}

@Composable
fun ObycPolozkaNadpisHodnota(
    nadpis: String,
    hodnota: String,
    spodniOdsazeni: Boolean,
    buttonProMapy: Boolean = false,
    onClickedButtonIcoSubjekt: (String) -> Unit = {},
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = PozadiTextu)
    ) {
        Text(
            text = nadpis,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(bottom = 1.dp)
                .weight(0.3f)
            //      .fillMaxHeight()
            // .fontWeight(FontWeight.Bold)
        )
        Text(
            text = " ",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(bottom = 1.dp)
                .weight(0.02f)
        )
        if (buttonProMapy && hodnota != "") {
            Text(
                text = hodnota,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(bottom = 1.dp)
                    .weight(0.60f)
            )
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .padding(0.dp)
                    .weight(0.08f)
                    .background(color = PozadiTextu)

            ) {
                ButtonWithMapIcon(hodnota)
            }

        } else if (nadpis.equals("ICO:") && hodnota != "") {
            Text(
                text = hodnota,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(bottom = 1.dp)
                    .weight(0.60f)
            )
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .padding(0.dp)
                    .weight(0.08f)
                    .background(color = PozadiTextu)

            ) {
                ButtonWithSearchIcoIcon(onClickedButtonIcoSubjekt, hodnota)
            }
        } else {
            Text(
                text = hodnota,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(bottom = 1.dp)
                    .weight(0.68f)
            )
        }


    }


    if (spodniOdsazeni) Spacer(
        modifier = Modifier
            .height(VelikostSpodniOdsazeni)
            .background(color = Color.White)
    )

}

@Composable
fun ObycPolozkaHodnota(hodnota: String, spodniOdsazeni: Boolean, tucne: Boolean) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = PozadiTextu)
    ) {

        // SelectionContainer() {
        Text(
            text = hodnota,
            fontWeight = if (tucne) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = VelikostPaddingTextuHlavniZaznamy)
            //   .fillMaxHeight()
        )
        //    }

    }
    if (spodniOdsazeni) Spacer(modifier = Modifier.height(VelikostSpodniOdsazeni))
}

@Composable
fun ObycPolozkaJenNadpisUprostred(nadpis: String, spodniOdsazeni: Boolean) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(3.dp)
    ) {
        Text(
            text = nadpis,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 1.dp)
        )// .fontWeight(FontWeight.Bold)


    }

    if (spodniOdsazeni) Spacer(modifier = Modifier.height(VelikostSpodniOdsazeni))
}

//button je komplikovanější, protože kromě názvu, řeší také, jestli se pořád načítá (nacitani) a jestli se dá na button clicknout ()buttonClicked
//dále je komplikovanější modifier, kdy tvar se liší podle toho,  jestli se stále načítá, nebo už jsou údaje načtené
@Composable
fun CustomButton(
    nadpis: String, nacitani: Boolean, buttonClicked: Boolean,
    onClick: () -> Unit = {},
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(size = VelikostZakulaceniRohuButton),
        enabled = buttonClicked,

        modifier = if (nacitani) {
            Modifier
                .padding(PaddingVButtonu)
                .height(60.dp)
                .shadow(
                    elevation = VelikostElevation
                )
        } else {
            Modifier
                .padding(PaddingVButtonu)
                .width(250.dp)
                .height(60.dp)
                .shadow(
                    elevation = VelikostElevation
                )
        },
    ) {
        if (nacitani) {
            ProgressIndicatorLoading(
                progressIndicatorSize = ProgressIndicatorSizeButtons,
                progressIndicatorColor = ProgressIndicatorColor,

                )
        } else Text(nadpis)
    }

}

@Composable
fun ProgressIndicatorLoading(progressIndicatorSize: Dp, progressIndicatorColor: Color) {

    val infiniteTransition = rememberInfiniteTransition()

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
            }
        )
    )

    CircularProgressIndicator(
        progress = 1f,
        modifier = Modifier
            .size(progressIndicatorSize)
            .rotate(angle)
            .border(
                12.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        Color.White, // add background color first
                        progressIndicatorColor.copy(alpha = 0.1f),
                        progressIndicatorColor
                    )
                ),
                shape = CircleShape
            ),
        strokeWidth = 1.dp,
        color = Color.White // Set background color
    )
}

@Composable
fun SeznamDvoupolozekNace(nazevSeznamuDvoupolozek: String, seznamDvoupolozek: MutableList<Nace>) {
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = nazevSeznamuDvoupolozek,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
            )
            Column(modifier = Modifier) {
                seznamDvoupolozek.forEach {
                    Column {
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
                                .fillMaxWidth(),

                            ) {
                            SelectionContainer {
                                Column(
                                    modifier = Modifier
                                        .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                        .fillMaxWidth()
                                ) {
                                    ObycPolozkaNadpisHodnota(it.cisloNace, it.nazevNace, true)
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

@Composable
fun ButtonWithMapIcon(address: String) {
    val context = LocalContext.current

    IconButton(
        modifier = Modifier
            .padding(2.dp)
            .then(Modifier.size(24.dp)),
        onClick = {
            StringToGpsToMap.presmerujZAdresyNaMapy(address, context)
        },

        ) {
        Icon(
            imageVector = Icons.Default.Map, // Použití ikony mapy z material design
            contentDescription = null // Nepotřebujeme popis
        )
    }

    //Text(text = "Zobrazit na mapě"
    //)


}

//tohle dodělat, nastavit to k výpisu u položek, kde je ico
//a v onClick to udělat tak, že MainActivity bude automaticky hned vyhledávat to ICO
@Composable
fun ButtonWithSearchIcoIcon(
    onClickedButtonIcoSubjekt: (String) -> Unit = {},
    ico: String,
) {
    val context = LocalContext.current

    IconButton(
        modifier = Modifier
            .padding(2.dp)
            .then(Modifier.size(24.dp)),
        onClick = {
            onClickedButtonIcoSubjekt(ico)
        },

        ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null // Nepotřebujeme popis
        )
    }

    //Text(text = "Zobrazit na mapě"
    //)


}

@Composable
fun MyLinearProgressIndicator() {
    // Display LinearProgressIndicator with a small height to create a thin horizontal line
    LinearProgressIndicator(
        modifier = Modifier
            .padding(horizontal = PaddingLinearProgressIndicatoru)
            .fillMaxWidth()
            .height(4.dp) // Adjust the height as needed
            .clip(RoundedCornerShape(100)) //zakulacené rohy
    )
}

//nový expandable:

@Composable
fun SeznamPolozekBezCard2(nazevSeznamuPolozek: String, seznamPolozek: MutableList<String>) {
    var expanded by remember { mutableStateOf(true) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .animateContentSize( //efekt pro rozbalení
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .padding(2.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (!(nazevSeznamuPolozek == "")) {
                Text(
                    text = nazevSeznamuPolozek,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                )
            }
            ExpandableItemButton(
                expanded = expanded,
                onClick = { expanded = !expanded },
                modifier = Modifier
                    .padding(0.dp)

            )
        }

        if (expanded) {
            SelectionContainer {
                Column(modifier = Modifier) {
                    seznamPolozek.forEach {
                        Column {
                            Text(
                                "- " + it.toString(),
                                modifier = Modifier
                                    .padding(
                                        vertical = 3.dp,
                                        horizontal = VelikostPaddingMezeryMeziHlavnimiZaznamy
                                    )
                                    .background(color = PozadiTextu)
                                    .fillMaxWidth()
                            )

                        }


                    }
                }
            }
        }

    }


    Spacer(modifier = Modifier.height(OdsazeniMensi))
}

@Composable
fun ExpandableItemButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = stringResource(R.string.expandable_button)
        )
    }

}

@Composable
fun AlertDialogWrapperOpravneni(
    onClickPovolit: () -> Unit = {},
    onClickNe: () -> Unit = {},
    onDismissFunction: () -> Unit = {},

    ) {
    val context = LocalContext.current
    val buttonStatePovolit = remember { mutableStateOf(onClickPovolit) }
    // je to potřeba ošetřit takhle, protože: AlertDialog se zobrazí asynchronně a tím pádem by se onClickPovolit s dalšími funkcemi v bloku nevykonal. Kdyby byl sám bez bloku, tak by to nebylo třeba.
    val icon = Icons.Default.Info


    AlertDialog(
        onDismissRequest = { onDismissFunction() }, // co se má stát, když dá uživatel zpět
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(0.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null, // Content description můžete nastavit dle potřeby
                    tint = Color.Blue, // Barevná varianta ikony
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Potřebujeme povolení",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }
        },
        //  title = { Text("Potřebujeme povolení") },
        text = { Text("Pro ukládání souborů je třeba pro tuto aplikaci udělit oprávnění Přístup ke všem souborům. Klikněte na tlačítko Povolit pro pokračování.") },

        confirmButton = {
            Button(
                onClick = {
                    // onClickButton
                    buttonStatePovolit.value()
                    // Zde se spustí proces žádosti o oprávnění
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    ContextCompat.startActivity(context, intent, null)
                    //  LocalUriHandler.current.openUri(intent.toUri())
                }
            ) {
                Text("Povolit")
            }
        },
        dismissButton = {
            Button(
                onClick = onClickNe
            ) {
                Text("Ne")
            }
        }
    )


}


@Preview
@Composable
fun MyComposableFunctionPreview() {
    // Provide sample values for the arguments
    ObycPolozkaNadpisHodnota("aaaa", "bbbb", false, true)
}


/*@Composable
fun MyCard(name: String, ico: String, address: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Name: $name",
                fontSize = 20.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ICO: $ico",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Address: $address",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
        MyCard(name = "John Doe", ico = "12345678", address = "123 Main St")

}*/





