package xyz.thingapps.rssliveslider.models

import android.os.Parcel
import android.os.Parcelable

data class GlideSize(val width: Int, val height: Int, var url: String = "") : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun toString(): String = "$width x $height"
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GlideSize> {
        override fun createFromParcel(parcel: Parcel): GlideSize {
            return GlideSize(parcel)
        }

        override fun newArray(size: Int): Array<GlideSize?> {
            return arrayOfNulls(size)
        }
    }

}