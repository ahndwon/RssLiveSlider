package xyz.thingapps.rssliveslider.models

import android.os.Parcel
import android.os.Parcelable
import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "item")
data class Item(
    @PropertyElement(writeAsCData = true) val title: String? = "",
    @PropertyElement(writeAsCData = true) var description: String? = "",
    @Attribute val img: String? = "",
    @PropertyElement(writeAsCData = true) val link: String? = "",
    @PropertyElement(writeAsCData = true) val guid: String? = "",
    @PropertyElement(writeAsCData = true) val pubDate: String? = "",
    @PropertyElement(writeAsCData = true) val source: String? = "",
    @Element var media: Media? = Media(),
    @Attribute var recognition: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Media::class.java.classLoader),
        parcel.readString()
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
        parcel.writeString(recognition)
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