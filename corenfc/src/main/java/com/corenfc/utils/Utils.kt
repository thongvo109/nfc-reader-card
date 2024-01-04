package com.corenfc.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class Utils {
    companion object {


        @SuppressLint("SimpleDateFormat")
        fun convertDateToView(data: String, pattern: String = "yyMMdd" , outputPattern: String = "dd/MM/yyyy" ) : String {
            val inputDateFormat = SimpleDateFormat(pattern)
            val date: Date = inputDateFormat.parse(data) ?: return  ""
            val outputDateFormat = SimpleDateFormat(outputPattern)
            return  outputDateFormat.format(date)
        }


        fun convertDate(data: String): String {
            val currentTime = LocalDate.parse(data, DateTimeFormatter.ofPattern("ddMMyyyy"))
            return currentTime.format(DateTimeFormatter.ofPattern("yyMMdd"))
        }

        fun calculateExpiredDate(data: String): String {
            val birthday = LocalDate.parse(data, DateTimeFormatter.ofPattern("ddMMyyyy"));
            val currentDate = LocalDate.now();
            val age = Period.between(birthday, currentDate)
            val years = age.years;
            val month = convertMonth(birthday.monthValue)
            return if (years <= 40) {
                "${
                    (birthday.year + 40).toString().takeLast(2)
                }$month${birthday.dayOfMonth}"
            } else if (years >= 41) {
                "${
                    (birthday.year + 20).toString().takeLast(2)
                }$month${birthday.dayOfMonth}"
            } else {
                ""
            }
        }

        private fun convertMonth(month: Int): String {
            return if (month < 10) {
                "0$month"
            } else {
                month.toString()
            }
        }
    }
}