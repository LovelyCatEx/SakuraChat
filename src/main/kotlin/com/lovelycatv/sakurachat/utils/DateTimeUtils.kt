/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.utils

import com.nlf.calendar.Solar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.math.abs

object DateTimeUtils {
    fun getSpecialDay(date: LocalDate): String? {
        val fixed = getEnhancedFixedHoliday(date)
        if (fixed != null) return fixed

        val solar = Solar.fromYmd(date.year, date.monthValue, date.dayOfMonth)
        val lunar = solar.lunar

        val lunarMonth = lunar.month
        val lunarDay = lunar.day
        val isLeap = lunarMonth < 0

        return when (abs(lunarMonth) to lunarDay) {
            12 to 29 -> if (isLeap) null else "除夕"
            12 to 30 -> "除夕"
            1 to 1 -> "春节"
            1 to 15 -> "元宵节"
            5 to 5 -> "端午节"
            8 to 15 -> "中秋节"
            else -> null
        }
    }

    private val fixedHolidays = mapOf(
        "01-01" to "元旦",
        "01-06" to "小寒",
        "01-20" to "大寒",

        "02-14" to "情人节",
        "02-02" to "世界湿地日",
        "02-04" to "立春",
        "02-19" to "雨水",

        "03-08" to "妇女节",
        "03-12" to "植树节",
        "03-15" to "消费者权益日",
        "03-05" to "惊蛰",
        "03-20" to "春分",

        "04-01" to "愚人节",
        "04-04" to "清明节",
        "04-05" to "清明节",
        "04-22" to "地球日",
        "04-20" to "谷雨",

        "05-01" to "劳动节",
        "05-04" to "青年节",
        "05-12" to "护士节",
        "05-20" to "520情人节",
        "05-05" to "立夏",
        "05-21" to "小满",

        "06-01" to "儿童节",
        "06-06" to "全国爱眼日",
        "06-05" to "芒种",
        "06-21" to "夏至",

        "07-01" to "建党节",
        "07-07" to "七七事变纪念日",
        "07-07" to "小暑",
        "07-22" to "大暑",

        "08-01" to "建军节",
        "08-08" to "全民健身日",
        "08-07" to "立秋",
        "08-23" to "处暑",

        "09-10" to "教师节",
        "09-08" to "白露",
        "09-22" to "秋分",
        "09-28" to "孔子诞辰",

        "10-01" to "国庆节",
        "10-04" to "世界动物日",
        "10-08" to "寒露",
        "10-23" to "霜降",

        "11-11" to "光棍节",
        "11-07" to "立冬",
        "11-22" to "小雪",

        "12-25" to "圣诞节",
        "12-31" to "跨年夜",
        "12-07" to "大雪",
        "12-21" to "冬至"
    )

    fun getFixedHoliday(month: Int, day: Int): String? {
        val key = "%02d-%02d".format(month, day)
        return fixedHolidays[key]
    }

    fun getEnhancedFixedHoliday(date: LocalDate): String? {
        val month = date.monthValue
        val day = date.dayOfMonth

        val fixed = getFixedHoliday(month, day)
        if (fixed != null) return fixed

        return when {
            isSecondSundayOfMonth(date, 5) -> "母亲节"

            isThirdSundayOfMonth(date, 6) -> "父亲节"

            isFourthThursdayOfMonth(date, 11) -> "感恩节"

            else -> null
        }
    }

    private fun isNthSundayOfMonth(date: LocalDate, month: Int, n: Int): Boolean {
        if (date.monthValue != month) return false
        if (date.dayOfWeek != DayOfWeek.SUNDAY) return false

        val firstDayOfMonth = date.withDayOfMonth(1)
        val firstSunday = firstDayOfMonth.with(TemporalAdjusters.firstInMonth(DayOfWeek.SUNDAY))

        val weekOfMonth = ((date.dayOfMonth - firstSunday.dayOfMonth) / 7) + 1
        return weekOfMonth == n
    }

    private fun isSecondSundayOfMonth(date: LocalDate, month: Int) =
        isNthSundayOfMonth(date, month, 2)

    private fun isThirdSundayOfMonth(date: LocalDate, month: Int) =
        isNthSundayOfMonth(date, month, 3)

    private fun isFourthThursdayOfMonth(date: LocalDate, month: Int): Boolean {
        if (date.monthValue != month) return false
        if (date.dayOfWeek != DayOfWeek.THURSDAY) return false

        val firstDayOfMonth = date.withDayOfMonth(1)
        val firstThursday = firstDayOfMonth.with(TemporalAdjusters.firstInMonth(DayOfWeek.THURSDAY))

        val weekOfMonth = ((date.dayOfMonth - firstThursday.dayOfMonth) / 7) + 1
        return weekOfMonth == 4
    }
}