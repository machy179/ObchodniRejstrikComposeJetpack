package com.machy1979.obchodnirejstrik.functions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat

//třída,která zjišťuje oprávnění udělená aplikaci
class PermissionsChecker {

    companion object {

         //zjištění oprávnění ukládání soubourů
        fun checkStoragePermissions(): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //Android is 11 (R) nebo vyšší
                Environment.isExternalStorageManager()
            } else {
                //nižší než android 11
                val write =
                    ContextCompat.checkSelfPermission(StringToPdfConvector.context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                val read =
                    ContextCompat.checkSelfPermission(StringToPdfConvector.context, Manifest.permission.READ_EXTERNAL_STORAGE)
                read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
            }
        }
    }


}