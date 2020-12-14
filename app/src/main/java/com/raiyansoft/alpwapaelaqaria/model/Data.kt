package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Data {
    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

}