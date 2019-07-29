package xyz.thingapps.rssliveslider.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

fun <T> Call<T>.enqueue(success: (response: Response<T>) -> Unit, failure: (t: Throwable) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) = failure(t)
        override fun onResponse(call: Call<T>, response: Response<T>) = success(response)
    })
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }

    })
}

fun EditText.validate(message: String, validator: (String) -> Boolean) {
    this.afterTextChanged {
        this.error = if (validator(it)) null else message
    }
    this.error = if (validator(this.text.toString())) null else message
}

fun Context.millisToDate(millis: Long): String {
    val diff = Date().time - millis

    val sec = diff / 1000
    val min = diff / (60 * 1000)
    val hour = diff / (60 * 60 * 1000)
    val day = diff / (24 * 60 * 60 * 1000)
    val week = diff / (7 * 24 * 60 * 60 * 1000)
    val month = (diff / (30.5 * 24 * 60 * 60 * 1000)).toInt()
    //val year = day / 365

    return when {
        day >= 365 -> "${(day / 365)}년 전"
        month > 0 -> "${month}달 전"
        week > 0 -> "${week}주 전"
        day > 0 -> "${day}일 전"
        hour > 0 -> "${hour}시간 전"
        min > 0 -> "${min}분 전"
        else -> "${sec}초 전"
    }
}