package com.nicha.furnier.models

import android.os.Parcel
import android.os.Parcelable

data class FurnitureCategory(
    val id: String,
    val name: String,
    val imageUrl: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FurnitureCategory> {
        override fun createFromParcel(parcel: Parcel): FurnitureCategory {
            return FurnitureCategory(parcel)
        }

        override fun newArray(size: Int): Array<FurnitureCategory?> {
            return arrayOfNulls(size)
        }
    }
} 