package xyz.thingapps.rssliveslider.api.dao

import com.tickaroo.tikxml.annotation.*

@Xml(name = "rss")
data class Cast(
    @Path("channel") @PropertyElement val title : String = "",
    @Path("channel") @PropertyElement val description : String = "",
    @Path("channel") @PropertyElement val link : String = "",
    @Path("channel") @Element val items: List<Item> = mutableListOf()
)

@Xml(name = "item")
data class Item(
    @PropertyElement val title: String = "",
    @PropertyElement val description: String = "",
    @PropertyElement val link: String = "",
    @PropertyElement val guid: String = "",
    @PropertyElement val pubDate: String = "",
    @PropertyElement val source: String = "",
    @Element val media: Media? = Media()
)

@Xml(name = "enclosure")
data class Media(
    @Attribute val url: String = "",
    @Attribute val type: String = ""
)