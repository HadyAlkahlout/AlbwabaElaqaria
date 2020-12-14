package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SetData {
    @SerializedName("android_version")
    @Expose
    var androidVersion: String? = null

    @SerializedName("ios_version")
    @Expose
    var iosVersion: String? = null

    @SerializedName("special")
    @Expose
    var special: String? = null

    @SerializedName("force_update")
    @Expose
    var forceUpdate: String? = null

    @SerializedName("force_close")
    @Expose
    var forceClose: String? = null

}