package xyz.thingapps.rssliveslider.api

import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

const val DUMMY_BASE_URL = "https://google.com"

fun provideRssApi(): RssApi = Retrofit.Builder().apply {
    baseUrl(DUMMY_BASE_URL)
    client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
    addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
    addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
}.build().create(RssApi::class.java)


val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

