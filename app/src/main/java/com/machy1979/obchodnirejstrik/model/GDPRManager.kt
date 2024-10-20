package com.machy1979.obchodnirejstrik.model

import android.app.Activity
import android.util.Log
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform

object GDPRManager {

    // Tato funkce se stará o žádost o aktualizaci informací o souhlasu
    fun makeGDPRContent(activity: Activity) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        val params = ConsentRequestParameters.Builder()
            .setConsentDebugSettings(
                ConsentDebugSettings.Builder(activity)
                    .build()
            )
            .setTagForUnderAgeOfConsent(false) // Pokud aplikace není pro děti, nastavte na false
            .build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                if (consentInformation.isConsentFormAvailable) {
                    // Zobrazte formulář, pokud je dostupný
                    loadConsentForm(activity)
                }
            },
            { error ->
                Log.d("ErrorStorky", "No consent form is available")
            }
        )
    }

    // Funkce pro načtení a zobrazení formuláře
    private fun loadConsentForm(activity: Activity) {
        UserMessagingPlatform.loadConsentForm(
            activity,
            { consentForm ->
                consentForm.show(activity) { formError ->
                    // Zpracování výsledku nebo chyby
                    if (formError == null) {
                        // Kontrola, zda uživatel udělil souhlas
                        if (UserMessagingPlatform.getConsentInformation(activity).consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                            // Uživatel musí udělit souhlas
                            loadConsentForm(activity)
                        }
                    } else {
                        Log.d("ErrorStorky", "Error showing consent form: ${formError.message}")
                    }
                }
            },
            { loadError ->
                Log.d("ErrorStorky", "Error loading consent form: ${loadError.message}")
            }
        )
    }
}