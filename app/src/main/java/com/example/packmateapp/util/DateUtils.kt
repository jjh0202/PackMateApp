package com.example.packmateapp.util

import android.content.Context
import com.example.packmateapp.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

// minSdk 24라서 java.time 대신 Calendar / SimpleDateFormat 사용
object DateUtils {

    private const val DB_PATTERN = "yyyy-MM-dd"
    private const val MONTH_DAY_PATTERN = "MM/dd"
    private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000

    private fun dbFormat() = SimpleDateFormat(DB_PATTERN, Locale.KOREA).apply { isLenient = false }

    fun toDbString(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance().apply {
            clear()
            set(year, month, dayOfMonth)
        }
        return dbFormat().format(calendar.time)
    }

    fun toCalendar(dateText: String): Calendar? =
        try {
            dbFormat().parse(dateText)?.let { date -> Calendar.getInstance().apply { time = date } }
        } catch (e: ParseException) {
            null
        }

    // "2026-12-20" → "12/20"
    fun toMonthDay(dateText: String): String =
        toCalendar(dateText)?.let { SimpleDateFormat(MONTH_DAY_PATTERN, Locale.KOREA).format(it.time) }
            ?: dateText

    // 오늘부터 출발일까지 남은 날 수 (지난 여행은 음수)
    fun daysUntil(dateText: String): Int? {
        val start = toCalendar(dateText) ?: return null
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return ((start.timeInMillis - today.timeInMillis).toDouble() / MILLIS_PER_DAY).roundToInt()
    }

    // D-54 / D-Day / D+3
    fun dDayText(context: Context, dateText: String): String {
        val days = daysUntil(dateText) ?: return ""
        return when {
            days > 0 -> context.getString(R.string.dday_before, days)
            days == 0 -> context.getString(R.string.dday_today)
            else -> context.getString(R.string.dday_after, -days)
        }
    }
}
