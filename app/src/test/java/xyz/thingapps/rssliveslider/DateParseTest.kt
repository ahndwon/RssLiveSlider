package xyz.thingapps.rssliveslider

import org.junit.Test
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

class DateParseTest {

    @Test
    fun parseDate() {

        val date = "Thu, 28 Mar 2019 14:00 EDT"
        val format = "E, dd MMM yyyy HH:mm zzz"
        val locale = Locale.ENGLISH
//        println("isValid -  $date = " + isValidFormat("E, dd MMM yyyy HH:mm zzz", date, Locale.ENGLISH));
        val formatter = SimpleDateFormat(format, locale)
        formatter.applyLocalizedPattern(format)

//        val form = DateTimeFormatter.ofPattern(format, locale)
//        val odt = OffsetDateTime.parse(date, form)

//        println("isValid -  $date = " + isValidFormat(format, date, locale))
//        println("isValid -  $date = " + formatter.parse(date))
//        println("isValid -  $date = " + odt)

        val formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS zzz")
            .withZone(ZoneId.of("America/New_York"))
        val now = Instant.now()
        println(formatter2.format(now))
    }
}