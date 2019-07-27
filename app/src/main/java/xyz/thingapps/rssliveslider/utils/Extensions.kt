package xyz.thingapps.rssliveslider.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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