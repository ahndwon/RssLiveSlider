package xyz.thingapps.rssliveslider.api

import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

fun provideNasaApi(): NasaApi = Retrofit.Builder().apply {
    baseUrl("https://www.nasa.gov/rss/")
    client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
    addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
    addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
}.build().create(NasaApi::class.java)


val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

