package xyz.thingapps.rssliveslider.models

import android.os.Parcel
import android.os.Parcelable
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml


@Xml(name = "rss")
data class Cast(
    @Path("channel") @PropertyElement(writeAsCData = true) val title: String? = "",
    @Path("channel") @PropertyElement(writeAsCData = true) val description: String? = "",
    @Path("channel") @PropertyElement(writeAsCData = true) val link: String? = "",
    @Path("channel") @Element val items: List<Item>? = mutableListOf()
) : Parcelable {
    var createdAt: Long = 0

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



