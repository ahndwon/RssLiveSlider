package xyz.thingapps.rssliveslider.api

import android.os.Parcel
import android.os.Parcelable
import com.tickaroo.tikxml.annotation.*


@Xml(name = "rss")
data class Cast(
    @Path("channel") @PropertyElement(writeAsCData = true) val title: String = "",
    @Path("channel") @PropertyElement(writeAsCData = true) val description: String? = "",
    @Path("channel") @PropertyElement(writeAsCData = true) val link: String? = "",
    @Path("channel") @Element val items: List<Item> = mutableListOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(Item) ?: ArrayList<Item>()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(link)
        parcel.writeTypedList(items)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Cast> {
        override fun createFromParcel(parcel: Parcel): Cast {
            return Cast(parcel)
        }

        override fun newArray(size: Int): Array<Cast?> {
            return arrayOfNulls(size)
        }
    }

}

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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Media::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(img)
        parcel.writeString(link)
        parcel.writeString(guid)
        parcel.writeString(pubDate)
        parcel.writeString(source)
        parcel.writeParcelable(media, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}

@Xml(name = "enclosure")
data class Media(
    @Attribute val url: String = "",
    @Attribute val type: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Media> {
        override fun createFromParcel(parcel: Parcel): Media {
            return Media(parcel)
        }

        override fun newArray(size: Int): Array<Media?> {
            return arrayOfNulls(size)
        }
    }
}