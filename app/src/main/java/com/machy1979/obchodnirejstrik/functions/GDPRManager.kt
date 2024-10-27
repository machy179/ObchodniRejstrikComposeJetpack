package com.machy1979.obchodnirejstrik.functions

import android.app.Activity
import android.util.Log
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.ConsentForm
import com.google.android.ump.UserMessagingPlatform

class GDPRManager private constructor() { // Make the constructor private to prevent instantiation

    companion object {
        private lateinit var consentInformation: ConsentInformation
        private lateinit var consentForm: ConsentForm

        // Static-like function to request GDPR consent
        fun makeGDPRMessage(activity: Activity) {
            // Set tag for underage of consent. false means users are not underage.
            val params = ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false)
                .build()

            // Initialize consentInformation
            consentInformation = UserMessagingPlatform.getConsentInformation(activity)

            consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                {
                    // The consent information state was updated.
                    // You are now ready to check if a form is available.
                    if (consentInformation.isConsentFormAvailable) {
                        loadForm(activity)
                    }
                },
                { error ->
                    // Handle the error.
                    Log.e("GDPR", "Consent info update failed: ${error.message}")
                }
            )
        }

        // Static-like private function to load the consent form
        private fun loadForm(activity: Activity) {
            UserMessagingPlatform.loadConsentForm(
                activity,
                { consentForm ->
                    // Assign the loaded form to consentForm
                    this.consentForm = consentForm
                    if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                        consentForm.show(activity) {
                            // Handle dismissal by reloading form.
                            loadForm(activity)
                        }
                    }
                },
                { error ->
                    // Handle the error.
                    Log.e("GDPR", "Consent form load failed: ${error.message}")
                }
            )
        }
    }
}
