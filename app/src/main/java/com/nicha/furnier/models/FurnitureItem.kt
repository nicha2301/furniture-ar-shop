package com.nicha.furnier.models

import android.os.Parcel
import android.os.Parcelable

data class FurnitureItem(
    val id: String,
    val name: String,
    val imageUrl: String,
    val categoryId: String,
    val categoryName: String,
    val modelUrl: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(imageUrl)
        parcel.writeString(categoryId)
        parcel.writeString(categoryName)
        parcel.writeString(modelUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FurnitureItem> {
        override fun createFromParcel(parcel: Parcel): FurnitureItem {
            return FurnitureItem(parcel)
        }

        override fun newArray(size: Int): Array<FurnitureItem?> {
            return arrayOfNulls(size)
        }
    }
}