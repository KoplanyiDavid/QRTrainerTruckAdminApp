package com.vicegym.qrtrainertruckadminapp.data

import android.os.Parcel
import android.os.Parcelable

data class TraineeData(
    val name: String? = null,
    val mobile: String? = null,
    val email: String? = null,
    val rank: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(mobile)
        parcel.writeString(email)
        parcel.writeString(rank)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TraineeData> {
        override fun createFromParcel(parcel: Parcel): TraineeData {
            return TraineeData(parcel)
        }

        override fun newArray(size: Int): Array<TraineeData?> {
            return arrayOfNulls(size)
        }
    }
}