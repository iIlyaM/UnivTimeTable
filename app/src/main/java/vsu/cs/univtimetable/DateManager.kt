package vsu.cs.univtimetable

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateManager {
    companion object {
        var SEPTEMBER = 9
        var WEEK_DAYS: LinkedHashSet<String> =
            linkedSetOf(
                "Понедельник",
                "Вторник",
                "Среда",
                "Четверг",
                "Пятница",
                "Суббота",
                "Воскресенье"
            )

        var TIME = arrayOf(
            "8:00-9:35",
            "9:45-11:20",
            "11:30-13:05",
            "13:25-15:00",
            "15:10-16:45",
            "16:55-18:30",
            "18:40-20:15"
        )


        @RequiresApi(Build.VERSION_CODES.O)
        fun checkWeekType(): String {
            val currentYear = LocalDate.now().year
            val currentDate = LocalDate.now()
            val startDate = LocalDate.of(currentYear, SEPTEMBER, 1)

            var weeks = ChronoUnit.WEEKS.between(startDate, currentDate)

            if (currentDate.dayOfWeek == DayOfWeek.MONDAY) {
                weeks++
            }
            if (weeks.toInt() % 2 == 0) {
                return "Знаменатель"
            }
            return "Числитель"
        }

        @SuppressLint("SimpleDateFormat")
        fun getDayOfWeek(day: String, weekPointer: Int): String {
            val week = WEEK_DAYS.toList()
            val now = Calendar.getInstance()
            val weekday = week.indexOf(day) + 1

            var days = 0
            if (weekday - 1 > weekPointer) {
                days = (Calendar.SATURDAY - (weekPointer + 1) - (Calendar.SATURDAY - weekday))
            } else if (weekday - 1 < weekPointer){
                days = (-(Calendar.SATURDAY - weekday)) % 7
            } else {
                days = 0
            }
            now.add(Calendar.DAY_OF_YEAR, days)

            val date: Date = now.time
            return SimpleDateFormat("d MMMM E", Locale("ru")).format(date)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun stringToLocalDate(time: String): LocalDate {
            val formatter = DateTimeFormatter.ofPattern("hh:mm");
            return  LocalDate.parse(time, formatter);
        }
    }
}