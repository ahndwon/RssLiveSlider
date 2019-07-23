package xyz.thingapps.rssliveslider.api

import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

const val NASA_RSS = "https://www.nasa.gov/rss/dyn/"
const val JTBC_RSS = "http://fs.jtbc.joins.com//RSS/"

fun provideNasaApi(): NasaApi = Retrofit.Builder().apply {
    baseUrl(NASA_RSS)
    client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
    addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
    addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
}.build().create(NasaApi::class.java)

fun provideJtbcApi(): JtbcApi = Retrofit.Builder().apply {
    baseUrl(JTBC_RSS)
    client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
    addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
    addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
}.build().create(JtbcApi::class.java)


val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

