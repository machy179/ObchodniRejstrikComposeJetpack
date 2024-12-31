package com.machy1979.obchodnirejstrik.functions

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class BillingManagerOR @Inject constructor(
    application: Application,
    private val sharedPreferences: SharedPreferences,
) {

    private val billingClient: BillingClient = BillingClient.newBuilder(application)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                purchases.forEach { purchase ->
                    handlePurchase(purchase)
                }
            }
            // Reset isPurchaseInProgress when purchase flow finishes
            isPurchaseInProgress = false
        }
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    private val _adsDisabled = MutableStateFlow(true)
    val adsDisabled: StateFlow<Boolean> = _adsDisabled.asStateFlow()

    // Track whether a purchase is in progress
    @Volatile
    private var isPurchaseInProgress = false

    init {
        _adsDisabled.value = sharedPreferences.getBoolean("ads_disabled", false)
        Log.d("BillingManager", "_adsDisabled.value: " + _adsDisabled.value.toString())
        startConnection()
    }

    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                startConnection() // Znovu se pokusí o připojení
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryPurchases()
                }
            }
        })
    }

    private fun queryPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val purchased =
                    purchases.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }

                if (purchased != _adsDisabled.value) {
                    _adsDisabled.value = !purchased
                    savePurchaseStateToPreferences(purchased)
                }

                if (false) { //IMPORTANT, it is just for debuging billing - here is purchase deactivated after new open app - so it can be testing purchase again
                    purchases.forEach { purchase ->
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            val consumeParams = ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.purchaseToken)
                                .build()

                            billingClient.consumeAsync(consumeParams) { consumeResult, purchaseToken ->
                                if (consumeResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                    Log.d("BillingManager", "Purchase consumed: $purchaseToken")
                                    savePurchaseStateToPreferences(adsRemoved = false)
                                } else {
                                    Log.e(
                                        "BillingManager",
                                        "Failed to consume purchase: ${consumeResult.debugMessage}"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun startPurchase(activity: Activity) {
        if (isPurchaseInProgress) {
            Log.d("BillingManager", "Purchase already in progress, ignoring subsequent request.")
            return
        }

        isPurchaseInProgress = true

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("buy_app_remove_ads")
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList != null) {
                val flowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetailsList[0])
                                .build()
                        )
                    )
                    .build()
                billingClient.launchBillingFlow(activity, flowParams)
            } else {
                // Reset the flag if queryProductDetailsAsync fails
                isPurchaseInProgress = false
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _adsDisabled.value = true
                    savePurchaseStateToPreferences(true)
                }
            }
        }
    }

    private fun savePurchaseStateToPreferences(adsRemoved: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("ads_disabled", adsRemoved)
            apply()
        }
    }

    fun endConnection() {
        billingClient.endConnection()
    }
}