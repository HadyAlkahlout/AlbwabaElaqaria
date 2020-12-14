package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class Slider {
    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null
}