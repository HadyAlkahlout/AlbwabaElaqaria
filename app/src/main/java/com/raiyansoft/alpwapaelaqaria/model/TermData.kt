package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class TermData {
    @SerializedName("conditions")
    @Expose
    var conditions: String? = null
}