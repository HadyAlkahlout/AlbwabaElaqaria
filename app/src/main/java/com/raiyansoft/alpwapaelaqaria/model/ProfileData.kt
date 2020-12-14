package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ProfileData {
    @SerializedName("info")
    @Expose
    var info: ProfileInfo? = null

    @SerializedName("properties")
    @Expose
    var properties: List<Building>? = null

}