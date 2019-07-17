package xyz.thingapps.rssliveslider.models

import android.os.Parcel
import android.os.Parcelable

data class RssItem(val title: String = "",
                   val link: String = "",
                   val description: String = "",
                   val pubdate: String = "",
                   val image: String = "",
                   val guid: String = "") : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(link)
        parcel.writeString(description)
        parcel.writeString(pubdate)
        parcel.writeString(image)
        parcel.writeString(guid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RssItem> {
        override fun createFromParcel(parcel: Parcel): RssItem {
            return RssItem(parcel)
        }

        override fun newArray(size: Int): Array<RssItem?> {
            return arrayOfNulls(size)
        }
    }
}