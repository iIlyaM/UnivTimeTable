package vsu.cs.univtimetable

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
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
        var WEEK_DAYS_SHORT =
            mutableMapOf(
                "Понедельник" to "пн",
                "Вторник" to "вт",
                "Среда" to "ср",
                "Четверг" to "чт",
                "Пятница" to "пт",
                "Суббота" to "сб",
                "Воскресенье" to "вс"
            )

        var TIME = arrayOf(
            "08:00:00-9:35:00",
            "09:45:00-11:20:00",
            "11:30:00-13:05:00",
            "13:25:00-15:00:00",
            "15:10:00-16:45:00",
            "16:55:00-18:30:00",
            "18:40:00-20:15:00"
        )

        @RequiresApi(Build.VERSION_CODES.O)
        fun checkWeekType(): String {
            val prevYear = LocalDate.now().year - 1
            val currentDate = LocalDate.now()
            val startDate = LocalDate.of(prevYear, SEPTEMBER, 1)

            val weeks = ChronoUnit.WEEKS.between(startDate, currentDate)

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

            val days = (Calendar.SATURDAY - (weekPointer + 1) - (Calendar.SATURDAY - weekday))
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