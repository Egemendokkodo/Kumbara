package com.uygulamalarim.kumbara.Model

import android.os.Parcel
import android.os.Parcelable

data class GoalData(val description: String): Parcelable{
    constructor(parcel: Parcel) : this(parcel.readString().toString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GoalData> {
        override fun createFromParcel(parcel: Parcel): GoalData {
            return GoalData(parcel)
        }

        override fun newArray(size: Int): Array<GoalData?> {
            return arrayOfNulls(size)
        }
    }

}
