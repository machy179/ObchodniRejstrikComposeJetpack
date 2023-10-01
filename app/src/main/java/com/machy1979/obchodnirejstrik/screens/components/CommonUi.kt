package com.machy1979.obchodnirejstrik.screens.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.MapsHomeWork
import androidx.compose.material.icons.sharp.MapsHomeWork
import androidx.compose.material.icons.twotone.MapsHomeWork
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.machy1979.obchodnirejstrik.functions.StringToGpsToMap
import com.machy1979.obchodnirejstrik.model.Firma
import com.machy1979.obchodnirejstrik.model.Nace
import com.machy1979.obchodnirejstrik.model.Osoba
import com.machy1979.obchodnirejstrik.model.Zivnosti
import com.machy1979.obchodnirejstrik.ui.theme.*

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
        //  backgroundColor = Color.Blue,
        shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
        border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
        elevation = VelikostElevation,
        modifier = Modifier
            .padding(horizontal = VelikostPaddingCardHorizontal, vertical = VelikostPaddingCardVertical)
            .fillMaxWidth(),

        ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
        ) {
            if (!(nazevSeznamuPolozek=="")) {
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
                            //  backgroundColor = Color.Blue,
                            shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
                            border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
                            elevation = VelikostElevation,
                            modifier = Modifier
                                .padding(vertical = VelikostPaddingCardVertical)
                                .fillMaxWidth(),

                            )  {
                            SelectionContainer {
                            Column(modifier = Modifier
                                .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                .fillMaxWidth()) {

                                ObycPolozkaNadpisHodnota("Název:",it.nazevZivnosti, true)
                                ObycPolozkaNadpisHodnota("Druh:",it.druhZivnosti, true)
                                ObycPolozkaNadpisHodnota("Vznik oprávnění:",it.vznikOpravneni, true)
                                ObycPolozkaNadpisHodnota("Obory:","", false)
                                it.obory.forEach {
                                    ObycPolozkaHodnota(it.toString(),true,false)

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
        //  backgroundColor = Color.Blue,
        shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
        border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
        elevation = VelikostElevation,
        modifier = Modifier
            .padding(horizontal = VelikostPaddingCardHorizontal, vertical = VelikostPaddingCardVertical)
            .fillMaxWidth(),

        ) {
        SeznamPolozekBezCard(nazevSeznamuPolozek = nazevSeznamuPolozek, seznamPolozek = seznamPolozek)
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
            if (!(nazevSeznamuPolozek=="")) {
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
                            "- "+it.toString(),
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
fun SeznamOsob(nazevSeznamuOsob: String, seznamOsob: MutableList<Osoba>, dalsiTextSeznam: MutableList<String> =mutableListOf<String>()) {
    Card(
        //  backgroundColor = Color.Blue,
        shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
        border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
        elevation = VelikostElevation,
        modifier = Modifier
            .padding(horizontal = VelikostPaddingCardHorizontal, vertical = VelikostPaddingCardVertical)
            .fillMaxWidth(),

        ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = nazevSeznamuOsob,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
            )
            Column(modifier = Modifier) {
                seznamOsob.forEach {
                    Column {
                        Card(
                            //  backgroundColor = Color.Blue,
                            shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
                            border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
                            elevation = VelikostElevation,
                            modifier = Modifier
                                .padding(horizontal = VelikostPaddingCardHorizontal, vertical = VelikostPaddingCardVertical)
                                .fillMaxWidth(),

                            ) {
                            SelectionContainer {
                            Column(modifier = Modifier
                                .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                .fillMaxWidth()) {
                                if (!(it.funkce=="")) {
                                    ObycPolozkaNadpisHodnota("Funkce:", it.funkce, true)
                                }
                                ObycPolozkaNadpisHodnota("Jméno:", if(it.titulyPredJmenem=="") {
                                    it.jmeno+" "+it.prijmeni
                                } else {
                                    it.titulyPredJmenem+ " "+it.jmeno+" "+it.prijmeni
                                }, true)
                                ObycPolozkaNadpisHodnota("Dat. nar.:", it.datNar, true)
                                if(!(it.vklad=="")) ObycPolozkaNadpisHodnota("Bydliště:", it.adresa, true, true)
                                if(!(it.clenstviOd=="")) ObycPolozkaNadpisHodnota("Členství od:", it.clenstviOd, false)
                                if(!(it.veFunkciOd=="")) ObycPolozkaNadpisHodnota("Ve funkci od:", it.veFunkciOd, false)
                                if(!(it.vklad=="")) ObycPolozkaNadpisHodnota("Vklad:", it.vklad+" Kč", false)
                                if(!(it.splaceno=="")) ObycPolozkaNadpisHodnota("Splaceno:", it.splaceno+" %", false)
                                if(!(it.obchodniPodil=="")) ObycPolozkaNadpisHodnota("Obchodní podíl:", it.obchodniPodil, false)

                                Column(modifier = Modifier) {
                                    it.poznamky.forEach {
                                        Column {
                                            Text(
                                                "- "+it.toString(),
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
                if(!(dalsiTextSeznam.isEmpty())) {
                    SeznamPolozekBezCard("", dalsiTextSeznam)
            }

        }

    }
    Spacer(modifier = Modifier.height(OdsazeniMensi))
}
}

@Composable
fun SeznamOsobAFirem(nazevSeznamuOsobAFirem: String, seznamOsob: MutableList<Osoba>, seznamFirem: MutableList<Firma>, dalsiTextSeznam: MutableList<String> =mutableListOf<String>()) {
    Card(
        //  backgroundColor = Color.Blue,
        shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
        border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
        elevation = VelikostElevation,
        modifier = Modifier
            .padding(horizontal = VelikostPaddingCardHorizontal, vertical = VelikostPaddingCardVertical)
            .fillMaxWidth(),

        ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = nazevSeznamuOsobAFirem,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
            )
            Column(modifier = Modifier) {
                seznamOsob.forEach {
                    Column {
                        Card(
                            //  backgroundColor = Color.Blue,
                            shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
                            border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
                            elevation = VelikostElevation,
                            modifier = Modifier
                                .padding(horizontal = VelikostPaddingCardHorizontal, vertical = VelikostPaddingCardVertical)
                                .fillMaxWidth(),

                            ) {
                            SelectionContainer {
                                Column(modifier = Modifier
                                    .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                    .fillMaxWidth()) {
                                    if (!(it.funkce=="")) {
                                        ObycPolozkaNadpisHodnota("Funkce:", it.funkce, true)
                                    }
                                    ObycPolozkaNadpisHodnota("Jméno:", if(it.titulyPredJmenem=="") {
                                        it.jmeno+" "+it.prijmeni
                                    } else {
                                        it.titulyPredJmenem+ " "+it.jmeno+" "+it.prijmeni
                                    }, true)
                                    ObycPolozkaNadpisHodnota("Dat. nar.:", it.datNar, true)
                                    ObycPolozkaNadpisHodnota("Bydliště:", it.adresa, true, true)
                                    if(!(it.clenstviOd=="")) ObycPolozkaNadpisHodnota("Členství od:", it.clenstviOd, false)
                                    if(!(it.veFunkciOd=="")) ObycPolozkaNadpisHodnota("Ve funkci od:", it.veFunkciOd, false)
                                    if(!(it.vklad=="")) ObycPolozkaNadpisHodnota("Vklad:", it.vklad+" Kč", false)
                                    if(!(it.splaceno=="")) ObycPolozkaNadpisHodnota("Splaceno:", it.splaceno+" %", false)
                                    if(!(it.obchodniPodil=="")) ObycPolozkaNadpisHodnota("Obchodní podíl:", it.obchodniPodil, false)

                                    Column(modifier = Modifier) {
                                        it.poznamky.forEach {
                                            Column {
                                                Text(
                                                    "- "+it.toString(),
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
                            border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
                            elevation = VelikostElevation,
                            modifier = Modifier
                                .padding(horizontal = VelikostPaddingCardHorizontal, vertical = VelikostPaddingCardVertical)
                                .fillMaxWidth(),

                            ) {
                            SelectionContainer {
                                Column(modifier = Modifier
                                    .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                    .fillMaxWidth()) {
/*                                    if (!(it.funkce=="")) {
                                        ObycPolozkaNadpisHodnota("Funkce:", it.funkce, true)
                                    }*/
                                    ObycPolozkaNadpisHodnota("Firma:", it.name, true)
                                    ObycPolozkaNadpisHodnota("ICO:", it.ico, true)
                                    ObycPolozkaNadpisHodnota("Sídlo:", it.address, false, true)
/*                                    if(!(it.clenstviOd=="")) ObycPolozkaNadpisHodnota("Členství od:", it.clenstviOd, false)
                                    if(!(it.veFunkciOd=="")) ObycPolozkaNadpisHodnota("Ve funkci od:", it.veFunkciOd, false)*/
                                    if(!(it.vklad=="")) ObycPolozkaNadpisHodnota("Vklad:", it.vklad+" Kč", false)
                                    if(!(it.splaceno=="")) ObycPolozkaNadpisHodnota("Splaceno:", it.splaceno+" %", false)
                                    if(!(it.obchodniPodil=="")) ObycPolozkaNadpisHodnota("Obchodní podíl:", it.obchodniPodil, false)
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

                if(!(dalsiTextSeznam.isEmpty())) {
                    SeznamPolozekBezCard("", dalsiTextSeznam)
                }

            }

        }
        Spacer(modifier = Modifier.height(OdsazeniMensi))
    }
}

@Composable
fun ObycPolozkaNadpisHodnota(nadpis: String, hodnota: String, spodniOdsazeni: Boolean, buttonProMapy: Boolean=false) {

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
        Text(text = " ",
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(bottom = 1.dp)
                .weight(0.02f)
            //   .fillMaxHeight()
        )
       // SelectionContainer() {
            Text(text = hodnota,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(bottom = 1.dp)
                    .weight(0.68f)
                //   .fillMaxHeight()
            )
    //    }

    }

    if (buttonProMapy && hodnota != "") {
        ButtonWithMapIcon(hodnota)
    }

    if (spodniOdsazeni) Spacer(modifier = Modifier.height(VelikostSpodniOdsazeni)
        .background(color = Color.White))

}

@Composable
fun ObycPolozkaHodnota(hodnota: String, spodniOdsazeni: Boolean, tucne: Boolean) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = PozadiTextu)
    ) {

        // SelectionContainer() {
        Text(text = hodnota,
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

//button je komplikovanější, protože kromě názvu, řeší také, jestli se pořád načítá (nacitani) a jestli se dá na button clicknout ()buttonClickedOR
//dále je komplikovanější modifier, kdy tvar se liší podle toho,  jestli se stále načítá, nebo už jsou údaje načtené
@Composable
fun CustomButton(nadpis: String, nacitani: Boolean,buttonClickedOR: Boolean,
                 onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(size = VelikostZakulaceniRohuButton),
        enabled = buttonClickedOR,

        modifier = if (nacitani)
                { Modifier.padding(PaddingVButtonu).height(60.dp).shadow(
                    elevation = VelikostElevation)}
                else  {Modifier.padding(PaddingVButtonu).width(250.dp).height(60.dp).shadow(
                    elevation = VelikostElevation) },
            ) {
            if(nacitani) {
                ProgressIndicatorLoading(
                    progressIndicatorSize = ProgressIndicatorSizeButtons ,
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
            .padding(horizontal = VelikostPaddingCardHorizontal, vertical = VelikostPaddingCardVertical)
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
                            border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
                            elevation = VelikostElevation,
                            modifier = Modifier
                                .padding(horizontal = VelikostPaddingCardHorizontal, vertical = VelikostPaddingCardVertical)
                                .fillMaxWidth(),

                            ) {
                            SelectionContainer {
                                Column(modifier = Modifier
                                    .padding(VelikostPaddingMezeryMeziHlavnimiZaznamy)
                                    .fillMaxWidth()) {
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
fun AlertDialogWrapper(
    onClickPovolit: () -> Unit = {},
    onClickNe: () -> Unit = {},
    onDismissFunction: () -> Unit = {}

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


@Composable
fun ButtonWithMapIcon(address: String) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .background(color = PozadiTextu)

    ) {
        IconButton(
            modifier = Modifier.
            padding(2.dp).then(Modifier.size(24.dp)),
            onClick = {
                StringToGpsToMap.presmerujZAdresyNaMapy(address, context)
            },

        ) {
            Icon(
                imageVector = Icons.Rounded.Map, // Použití ikony mapy z material design
                contentDescription = null // Nepotřebujeme popis
            )
        }

        //Text(text = "Zobrazit na mapě"
         //)
    }

}

@Preview
@Composable
fun MyComposableFunctionPreview() {
    // Provide sample values for the arguments
    ObycPolozkaNadpisHodnota("aaaa", "bbbb", false, true)
}





