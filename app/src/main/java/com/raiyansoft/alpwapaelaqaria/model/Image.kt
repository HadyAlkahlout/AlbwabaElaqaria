package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Image {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

}