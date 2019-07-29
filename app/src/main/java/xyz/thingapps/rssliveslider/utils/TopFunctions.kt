package xyz.thingapps.rssliveslider.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.threeten.bp.DateTimeException
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import org.threeten.bp.temporal.UnsupportedTemporalTypeException
import java.util.*

inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(aClass: Class<T>): T = f() as T
    }

fun isValidFormat(format: String, value: String, locale: Locale): Boolean {
    val formatter = DateTimeFormatter.ofPattern(format, locale)

    try {
        val localDate = LocalDateTime.parse(value, formatter)
        val result = localDate?.format(formatter)
        return result == value
    } catch (e: DateTimeParseException) {
        try {
            val localDate = LocalDate.parse(value, formatter)
            val result = localDate.format(formatter)
            return result == value
        } catch (exp: DateTimeParseException) {
            try {
                val localTime = LocalTime.parse(value, formatter)
                val result = localTime.format(formatter)
                return result == value
            } catch (e2: DateTimeParseException) {
                // Debugging purposes
                e2.printStackTrace()
            }

        }

    } catch (e: UnsupportedTemporalTypeException) {
        e.printStackTrace()
    } catch (e: DateTimeException) {
        e.printStackTrace()
    }

    return false
}