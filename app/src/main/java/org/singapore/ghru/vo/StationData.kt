package org.singapore.ghru.vo

import androidx.room.ColumnInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StationData<T>(
    @field:SerializedName(value = "station")
    @Expose val station: T?,
    @Expose @field:SerializedName(value = "message")
    val message: String,
    @Expose @field:SerializedName(value = "error")
    val error: Boolean,
    @Expose @SerializedName("station_status")
    @ColumnInfo(name = "station_status")
    val stationStatus: Boolean
)