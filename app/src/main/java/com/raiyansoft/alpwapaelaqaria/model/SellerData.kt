package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SellerData {
    @SerializedName("info")
    @Expose
    var info: SellerInfo? = null

    @SerializedName("properties")
    @Expose
    var properties: List<Building>? = null

}