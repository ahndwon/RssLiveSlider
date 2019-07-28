package xyz.thingapps.rssliveslider

import org.jsoup.Jsoup
import org.junit.Test

class JSoupTest {

    @Test
    fun shouldParseHTML() {
        //1. Fetching the HTML from a given URL
        Jsoup.connect("https://www.google.co.in/search?q=this+is+a+test").get().run {
            //2. Parses and scrapes the HTML response
            select("div.rc").forEachIndexed { index, element ->
                val titleAnchor = element.select("a")
                val title = titleAnchor.text()
                val url = titleAnchor.attr("href")
                //3. Dumping Search Index, Title and URL on the stdout.
                println("$index. $title ($url)")
            }
        }
    }
}