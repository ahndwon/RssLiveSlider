package xyz.thingapps.rssliveslider.utils

/**
 * https://gist.github.com/gmazzo/16802d34d4a8bf3694c1b4489a24f99b
 * A Jsoup body converter for Retrofit
 *
 * Sample usage (with Rx):
 * Retrofit.Builder()
 *   .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
 *   .addConverterFactory(JsoupConverterFactory)
 *   .build()
 *
 * Then you can declare in you Retrofit interface return type:
 * - Call<Document>
 * - Single<Document>
 * - Maybe<Document>
 * - etc
 */

import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.nio.charset.Charset

object JSoupConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        return when (type) {
            Document::class.java -> JSoupConverter(retrofit!!.baseUrl().toString())
            else -> null
        }
    }

    private class JSoupConverter(val baseUri: String) : Converter<ResponseBody, Document?> {

        override fun convert(value: ResponseBody?): Document? {
            val charset = value?.contentType()?.charset() ?: Charset.forName("UTF-8")

            val parser = when (value?.contentType().toString()) {
                "application/xml", "text/xml" -> Parser.xmlParser()
                else -> Parser.htmlParser()
            }

            return Jsoup.parse(value?.byteStream(), charset.name(), baseUri, parser)
        }

    }

}