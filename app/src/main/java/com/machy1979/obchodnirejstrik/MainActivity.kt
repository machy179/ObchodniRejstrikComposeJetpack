package com.machy1979.obchodnirejstrik

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.machy1979.obchodnirejstrik.model.GDPRManager
import com.machy1979.obchodnirejstrik.ui.theme.ObchodniRejstrikTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GDPRManager.makeGDPRContent(this)
        setContent {
            ObchodniRejstrikTheme {
                // A surface container using the 'background' color from the theme
                ObchodniRejstrikApp2()
            }
        }
    }

}
