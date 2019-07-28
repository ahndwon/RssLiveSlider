package xyz.thingapps.rssliveslider

import io.reactivex.Observable
import org.jsoup.Jsoup
import org.junit.Test

class JSoupRxJavaTest {

    @Test
    fun shouldParseHTML() {
        Observable.just(Jsoup.connect("https://news.joins.com/article/23537646?cloc=rss|news|total_list").get())
            .subscribe({
                it.select("img").forEachIndexed { index, element ->

                    if (element.hasAttr("src")) {
                        val url = element.attr("src")
                        println("$index ($url)")
                    } else if (element.hasAttr("data-src")) {
                        val url = element.attr("data-src")
                        println("$index ($url)")
                    }

                }
            }, { e ->
                println("error $e")
            })
    }
}