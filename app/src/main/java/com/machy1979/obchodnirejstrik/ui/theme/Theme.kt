package com.machy1979.obchodnirejstrik.ui.theme

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.machy1979.obchodnirejstrik.R


private val DarkColorPalette = darkColors(
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
    val view = LocalView.current
    val window = (view.context as Activity).window
 //   window.statusBarColor = StatusBarColor.toArgb() - nakonec  je to řešené přes themes.xml
 //   window.navigationBarColor = NavigationBarColor.toArgb() - nakonec  je to řešené přes themes.xml


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
                // painter = painterResource(id = com.machy1979.obchodnirejstrik.R.mipmap.background),
                painter = painterResource(id = com.machy1979.obchodnirejstrik.R.drawable.pozadi9
                ),
                contentDescription = "headerImage",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
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

@Composable
fun ObchodniRejstrikTheme2( //nepoužito, je tam jen pozadí barvy, musím jí ještě specifikovat v Color.kt

    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val window = (view.context as Activity).window
//    window.statusBarColor = StatusBarColor.toArgb()
//    window.navigationBarColor = NavigationBarColor.toArgb()


    val colors = if (darkTheme) {
        LightColorPalette
    } else {
        LightColorPalette
    }



    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )


}
