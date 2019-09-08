package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR


fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))

    return dateFormat.format(this)
}

fun Date.shortFormat(): String {
    val pattern = if (this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY
    return day1 == day2
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
//        "second","seconds" -> value * SECOND
//        "minute","minutes" -> value * MINUTE
//        "hour","hours" -> value * HOUR
//        "day","days" -> value * DAY
//            else -> throw IllegalStateException("Ошибка")
    }
    this.time = time
    return this

}

enum class TimeUnits {
    SECOND,
    MINUTE,
    HOUR,
    DAY;

    fun plural(value: Int): String {
        var st1: String = "день"
        var st2: String = "дня"
        var st3: String = "дней"
        when (this) {
            TimeUnits.SECOND -> {
                st1 = "секунду"; st2 = "секунды"; st3 = "секунд"
            }
            TimeUnits.MINUTE -> {
                st1 = "минуту"; st2 = "минуты"; st3 = "минут"
            }
            TimeUnits.HOUR -> {
                st1 = "час"; st2 = "часа"; st3 = "часов"
            }
            TimeUnits.DAY -> {
                st1 = "день"; st2 = "дня"; st3 = "дней"
            }
        }



        if ((value % 100 > 4) && (value % 100 < 21)) {
            return value.toString() + " " + st3
        } else {
            if (value % 10 == 1) {
                return value.toString() + " " + st1
            } else if ((value % 10 > 1) && (value % 10 < 5)) {
                return value.toString() + " " + st2
            } else {
                return value.toString() + " " + st3
            }
        }

    }
}

fun Date.humanizeDiff(date: Date = Date()): String {
    var rez: String = "только что"
    var delt = date.time - this.time
    var tmp = if (delt > 0) delt else -delt
    rez = when (tmp) {
        in 0..SECOND -> "только что"
        in SECOND..45 * SECOND -> "несколько секунд"
        in 45 * SECOND..75 * SECOND -> "минуту"
        in 75 * SECOND..45 * MINUTE -> "${Padej((tmp / MINUTE).toInt(), TimeUnits.MINUTE)}"
        in 45 * MINUTE..75 * MINUTE -> "час"
        in 75 * MINUTE..22 * HOUR -> "${Padej((tmp / HOUR).toInt(), TimeUnits.HOUR)}"
        in 22 * HOUR..26 * HOUR -> "день"
        in 26 * HOUR..360 * DAY -> "${Padej((tmp / DAY).toInt(), TimeUnits.DAY)}"
        else -> if (delt > 0) "более года назад" else "более чем через год"
//        in 0..SECOND ->          >360д "более года назад"
    }
    if (rez != "только что" && rez != "более года назад" && rez != "более чем через год") {
        if (delt > 0)
            rez += " назад"
        else
            rez = "через " + rez
    }
    return rez
}

fun Padej(N: Int, units: TimeUnits = TimeUnits.DAY): String {
    var st1: String = "день"
    var st2: String = "дня"
    var st3: String = "дней"
    when (units) {
        TimeUnits.SECOND -> {
            st1 = "секунду"; st2 = "секунды"; st3 = "секунд"
        }
        TimeUnits.MINUTE -> {
            st1 = "минуту"; st2 = "минуты"; st3 = "минут"
        }
        TimeUnits.HOUR -> {
            st1 = "час"; st2 = "часа"; st3 = "часов"
        }
        TimeUnits.DAY -> {
            st1 = "день"; st2 = "дня"; st3 = "дней"
        }
    }



    if ((N % 100 > 4) && (N % 100 < 21)) {
        return N.toString() + " " + st3
    } else {
        if (N % 10 == 1) {
            return N.toString() + " " + st1
        } else if ((N % 10 > 1) && (N % 10 < 5)) {
            return N.toString() + " " + st2
        } else {
            return N.toString() + " " + st3
        }
    }
}
