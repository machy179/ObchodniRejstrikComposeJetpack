package com.machy1979.obchodnirejstrik.components

import android.util.Log
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.machy1979.obchodnirejstrik.BuildConfig
import com.machy1979.obchodnirejstrik.R
import com.machy1979.obchodnirejstrik.ui.theme.ColorBorderStroke
import com.machy1979.obchodnirejstrik.ui.theme.VelikostBorderStrokeCard
import com.machy1979.obchodnirejstrik.ui.theme.VelikostElevation
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardHorizontal
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardVertical
import com.machy1979.obchodnirejstrik.ui.theme.VelikostZakulaceniRohu
import com.machy1979.obchodnirejstrik.utils.Constants

@Composable
fun ORNativeAdWrapped() {
    ORNativeAdLayout(wrapped = true) { isLoaded ->
    }
}

@Composable
fun ORNativeAdLayout(
    wrapped: (Boolean) = false,
    onAdLoaded: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var isAdLoading by remember { mutableStateOf(true) } // Stav pro sledování načítání reklamy
    val adUnitIdNative =
        if (BuildConfig.DEBUG) Constants.AD_UNIT_ID_NATIVE_TEST else Constants.AD_UNIT_ID_NATIVE_OR

    LaunchedEffect(Unit) {
        val adLoader = AdLoader.Builder(context, adUnitIdNative)
            .forNativeAd { ad: NativeAd ->
                nativeAd = ad
                isAdLoading = false
                onAdLoaded(true)
                Log.i("OR_AD", "onAdLoaded: true")
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.i("NativeAd", "Ad failed to load: ${adError.message}")
                    isAdLoading = false
                    onAdLoaded(false)
                    Log.i("OR_AD", "onAdLoaded: true")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    if (!isAdLoading) {
        val backgroundColor = if (nativeAd != null) Color.White else Color.Transparent

        if (wrapped) {
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
                        .padding(2.dp)
                        .fillMaxWidth()
                ) {
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                //   .heightIn(max = maxHeight) // Nastavení maximální výšky
                                .padding(horizontal = 16.dp)
                                .background(backgroundColor, shape = RoundedCornerShape(16.dp))
                                .padding(
                                    start = 4.dp,
                                    end = 4.dp,
                                    top = 0.dp,
                                    bottom = 4.dp
                                ) // Inner padding for the content
                        ) {
                            nativeAd?.let { ORNativeAdLayout(it) }
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    //   .heightIn(max = maxHeight) // Nastavení maximální výšky
                    .padding(horizontal = 16.dp)
                    .background(backgroundColor, shape = RoundedCornerShape(16.dp))
                    .padding(
                        start = 4.dp,
                        end = 4.dp,
                        top = 0.dp,
                        bottom = 4.dp
                    ) // Inner padding for the content
            ) {
                nativeAd?.let { ORNativeAdLayout(it) }
            }
        }


    }


}


@Composable
fun ORNativeAdLayout(nativeAd: NativeAd) {
    val contentViewId by remember { mutableIntStateOf(View.generateViewId()) }
    val adViewId by remember { mutableIntStateOf(View.generateViewId()) }

    AndroidView(
        factory = { context ->
            val contentView = ComposeView(context).apply {
                id = contentViewId
            }
            NativeAdView(context).apply {
                id = adViewId
                addView(contentView)
            }
        },
        update = { view ->
            val adView = view.findViewById<NativeAdView>(adViewId)
            val contentView = view.findViewById<ComposeView>(contentViewId)

            adView.setNativeAd(nativeAd)
            adView.callToActionView = contentView

            contentView.setContent {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ad_badge),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp) // Adjust size according to your SVG

                    )
                    nativeAd.advertiser?.let { advertiser ->

                        Text(
                            text = advertiser ?: "",
                            style = MaterialTheme.typography.h5,
                            color = MaterialTheme.colors.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }



                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        nativeAd.icon?.drawable?.let { iconDrawable ->

                            Image(
                                painter = rememberAsyncImagePainter(iconDrawable),
                                contentDescription = null,

                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(end = 8.dp)
                            )
                        }


                        Text(
                            text = nativeAd.headline ?: "",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    nativeAd.body?.let { bodyText ->
                        Text(
                            text = bodyText,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    nativeAd.callToAction?.let { callToAction ->
                        Button(
                            onClick = {
                                adView.callToActionView?.performClick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(text = callToAction)
                        }
                    }

                    nativeAd.images.firstOrNull()?.drawable?.let { imageDrawable ->
                        Image(
                            painter = rememberAsyncImagePainter(imageDrawable),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit // Tohle by mělo zajistit, že se obrázek vejde bez oříznutí
                        )
                    }


                    nativeAd.starRating?.let { rating ->
                        Text(
                            text = "Rating: $rating",
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    )
}





