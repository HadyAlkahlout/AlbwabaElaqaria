package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FaqData {
    @SerializedName("data")
    @Expose
    var data: List<Question>? = null
}

