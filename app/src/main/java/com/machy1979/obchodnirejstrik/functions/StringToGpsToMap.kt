package com.machy1979.obchodnirejstrik.functions

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri

import com.google.android.gms.maps.model.LatLng

class StringToGpsToMap {
    companion object {
        private lateinit var context: Context

        fun presmerujZAdresyNaMapy(address: String, context: Context) {
            this.context = context
            println("GPS-mapa .....2")
            if (address != "") {
                println("GPS-mapa .....3")
                val gps = getGPSCoordinatesFromAddress(address)
                println("GPS-mapa .....4")
                println("GPS-mapa .....4=" + gps.toString())
                openMapsAppWithGPSLocation(gps, address)
                println("GPS-mapa .....5")
            }
        }

        fun openMapsAppWithGPSLocation3(coordinates: LatLng) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:${coordinates.latitude},${coordinates.longitude}?q=my location")
            )
            intent.setPackage("com.google.android.apps.maps")
            context.startActivity(intent)


        }

        fun openMapsAppWithGPSLocation(coordinates: LatLng, label: String) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:${coordinates.latitude},${coordinates.longitude}?q=${coordinates.latitude},${coordinates.longitude}($label)")
            )
            intent.setPackage("com.google.android.apps.maps")
            context.startActivity(intent)
        }

        fun getGPSCoordinatesFromAddress(address: String): LatLng {
            val geocoder = Geocoder(context)
            val addresses = geocoder.getFromLocationName(address, 1)
            val latitude = addresses?.get(0)?.latitude
            val longitude = addresses?.get(0)?.longitude

            return LatLng(latitude!!, longitude!!)
        }


    }
}