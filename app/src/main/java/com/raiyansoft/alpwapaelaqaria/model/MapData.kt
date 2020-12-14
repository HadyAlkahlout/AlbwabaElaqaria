package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MapData {
    @SerializedName("data")
    @Expose
    var data: List<MapLocation>? = null
}