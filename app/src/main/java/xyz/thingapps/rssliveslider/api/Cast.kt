package xyz.thingapps.rssliveslider.api

import com.tickaroo.tikxml.annotation.*


@Xml(name = "rss")
data class Cast(
    @Path("channel") @PropertyElement(writeAsCData = true) val title: String = "",
    @Path("channel") @PropertyElement(writeAsCData = true) val description: String? = "",
    @Path("channel") @PropertyElement(writeAsCData = true) val link: String? = "",
    @Path("channel") @Element val items: List<Item> = mutableListOf()
)

@Xml(name = "item")
data class Item(
    @PropertyElement(writeAsCData = true) val title: String = "",
    @PropertyElement(writeAsCData = true) val description: String? = "",
    @Attribute val img: String? = "",
    @PropertyElement(writeAsCData = true) val link: String? = "",
    @PropertyElement(writeAsCData = true) val guid: String? = "",
    @PropertyElement(writeAsCData = true) val pubDate: String? = "",
    @PropertyElement(writeAsCData = true) val source: String? = "",
    @Element var media: Media? = Media()
)

@Xml(name = "enclosure")
data class Media(
    @Attribute val url: String = "",
    @Attribute val type: String = ""
)