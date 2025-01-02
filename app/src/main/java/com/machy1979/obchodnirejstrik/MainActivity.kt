package com.machy1979.obchodnirejstrik

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import com.machy1979.obchodnirejstrik.functions.GDPRManager
import com.machy1979.obchodnirejstrik.functions.deleteTCStringIfOutdated
import com.machy1979.obchodnirejstrik.navigation.ObchodniRejstrikNavigation
import com.machy1979.obchodnirejstrik.ui.theme.ObchodniRejstrikTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deleteTCStringIfOutdated(applicationContext)



        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            ObchodniRejstrikTheme {
                ObchodniRejstrikApp(activity = this)
            }
        }

    }


}

@Composable
fun ShowGDPRMessage(activity: Activity) {
    LaunchedEffect(Unit) {
        GDPRManager.makeGDPRMessage(activity)
    }
}

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun ObchodniRejstrikApp(activity: Activity?) {
    //je třeba to mít zde, když to bylo v MainActivity, tak rychlé telefony s Android 13 a výš to házelo chybu uživatelům
    if (activity != null) {
        ShowGDPRMessage(activity)
    }
    ObchodniRejstrikNavigation()
}
