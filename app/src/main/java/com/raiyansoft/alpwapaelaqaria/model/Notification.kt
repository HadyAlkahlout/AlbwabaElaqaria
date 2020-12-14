package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Notification {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("read")
    @Expose
    var read: Int? = null
}