package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EditInfo {
    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("mobile")
    @Expose
    var mobile: String? = null

    @SerializedName("country_code")
    @Expose
    var countryCode: Any? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null
}