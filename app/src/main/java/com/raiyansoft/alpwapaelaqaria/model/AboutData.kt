package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class AboutData {
    @SerializedName("about")
    @Expose
    var about: String? = null
}