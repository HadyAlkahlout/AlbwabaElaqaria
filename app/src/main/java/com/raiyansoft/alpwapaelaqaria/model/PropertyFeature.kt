package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PropertyFeature {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

}