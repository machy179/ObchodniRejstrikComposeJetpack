package com.machy1979.obchodnirejstrik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.machy1979.obchodnirejstrik.functions.GDPRManager
import com.machy1979.obchodnirejstrik.functions.deleteTCStringIfOutdated
import com.machy1979.obchodnirejstrik.ui.theme.ObchodniRejstrikTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deleteTCStringIfOutdated(applicationContext)

        setContent {
            ObchodniRejstrikTheme {
                // A surface container using the 'background' color from the theme
                ObchodniRejstrikApp2()
            }
        }

        //je třeba to mít zde, když to bylo nahoře, tak rychlé telefony s Android 13 a výš to házelo chybu uživatelům
        GDPRManager.makeGDPRMessage(this)
    }



}
