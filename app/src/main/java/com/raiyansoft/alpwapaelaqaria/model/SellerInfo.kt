package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SellerInfo {
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
    var note: String? = null

    @SerializedName("mobile")
    @Expose
    var mobile: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

}