package com.klifora.shop.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.regex.Pattern

class Utility {
    companion object {
        public fun getColoredSpanned(text: String, color: String): String {
            val input = "<font color=$color>$text</font>"
            return input
        }


        public fun isValidEmailId(email: String): Boolean {
            return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matcher(email).matches()
        }

    }


    fun getDate(ourDate: String): String {
        var ourDate = ourDate
        try {
            val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
            val value: Date = formatter.parse(ourDate)

            val dateFormatter: SimpleDateFormat =
                SimpleDateFormat("yyyy-MM-dd HH:mm a") //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault())
            ourDate = dateFormatter.format(value)

            //Log.d("ourDate", ourDate);
        } catch (e: Exception) {
            ourDate = "0000-00-00 00:00:00"
        }
        return ourDate
    }
}