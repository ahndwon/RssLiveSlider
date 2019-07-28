package xyz.thingapps.rssliveslider.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import xyz.thingapps.rssliveslider.utils.JSoupConverterFactory

fun provideJSoupApi(): JSoupApi = Retrofit.Builder().apply {
    baseUrl(DUMMY_BASE_URL)
    addConverterFactory(JSoupConverterFactory)
    addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
}.build().create(JSoupApi::class.java)