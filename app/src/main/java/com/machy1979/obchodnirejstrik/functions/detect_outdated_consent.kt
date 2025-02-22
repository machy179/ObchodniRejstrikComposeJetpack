package com.machy1979.obchodnirejstrik.functions

import android.content.Context
// import android.preference.PreferenceManager
import android.util.Log

/**
 * This function checks the date of last consent, which is base64-encoded in digits 1..7 of a string that is stored
 * in SharedPreferences under the key "IABTCF_TCString".
 *
 * If this date is older than 365 days, the entry with that key will be removed from SharedPreferences. With the IABTCF
 * configuration now being invalid, the CMP should re-display the consent dialog the next time it is instantiated.
 *
 * This should avoid errors of any used ad solution, which is supposed to consider consent older than 13 months "outdated".
 */

public fun deleteTCStringIfOutdated(context: Context) {
    // IABTCF string is stored in SharedPreferences
    val sharedPrefs = context.getSharedPreferences(
        context.getPackageName() + "_preferences", Context.MODE_PRIVATE
    );

    // get IABTCF string containing creation timestamp;
    // fall back to string encoding timestamp 0 if nothing is currently stored
    val tcString = sharedPrefs
        .getString("IABTCF_TCString", "AAAAAAA") ?: "AAAAAAA"

    Log.i("daysAgo", "tcString: " + tcString)

    // base64 alphabet used to store data in IABTCF string
    val base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

    // date is stored in digits 1..7 of the IABTCF string
    val dateSubstring = tcString.subSequence(1, 7).toString()
    Log.i("daysAgo", "dateSubstring: " + dateSubstring)

    // interpret date substring as base64-encoded integer value
    var timestamp = 0L
    for (c in dateSubstring) {
        val value = base64.indexOfFirst { char -> char == c }
        timestamp = timestamp * 64 + value
    }

    // timestamp is given is deci-seconds, convert to milliseconds
    timestamp *= 100

    Log.i("daysAgo", "timestamp: " + timestamp.toString())

    // compare with current timestamp to get age in days
    val daysAgo = (System.currentTimeMillis() - timestamp) / (1000 * 60 * 60 * 24)

    Log.i("daysAgo", "daysAgo: " + daysAgo.toString())

    // delete TC string if age is over a year
    if (daysAgo > 365) {
        sharedPrefs.edit().remove("IABTCF_TCString").apply()
    }
}