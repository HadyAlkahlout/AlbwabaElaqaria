package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MapLocation {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("lat")
    @Expose
    var lat: String? = null

    @SerializedName("lng")
    @Expose
    var lng: String? = null

    @SerializedName("distance")
    @Expose
    var distance: Double? = null

}