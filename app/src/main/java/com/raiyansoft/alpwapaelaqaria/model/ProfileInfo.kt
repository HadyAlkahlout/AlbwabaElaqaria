package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ProfileInfo {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("views")
    @Expose
    var views: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("note")
    @Expose
    var note: Any? = null

    @SerializedName("mobile")
    @Expose
    var mobile: String? = null

    @SerializedName("country_code")
    @Expose
    var countryCode: Any? = null

    @SerializedName("broker")
    @Expose
    var broker: Boolean? = null

    @SerializedName("send_notification")
    @Expose
    var send_notification: Boolean? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

}