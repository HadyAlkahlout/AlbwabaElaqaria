package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class FeatureResponse {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: FeatureData? = null
}