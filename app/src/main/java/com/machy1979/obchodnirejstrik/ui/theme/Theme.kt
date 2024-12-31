package com.machy1979.obchodnirejstrik.ui.theme


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource



private val DarkColorPalette = darkColors( //zatím nepoužito
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = barvaPrimarni,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = BarvaPozadi,


)

@Composable
fun ObchodniRejstrikTheme(

    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        LightColorPalette
    } else {
        LightColorPalette
    }

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Image(
                painter = painterResource(id = com.machy1979.obchodnirejstrik.R.drawable.pozadi9
                ),
                contentDescription = "headerImage",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box( // Překrývající Box s průhlednou barvou
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.2f)) // Černá s 50% průhledností
            )

            MaterialTheme(
                colors = colors,
                typography = Typography,
                shapes = Shapes,
                content = content

            )

        }
    }




    }
