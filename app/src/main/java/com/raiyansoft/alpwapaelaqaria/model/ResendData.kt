package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ResendData {
    @SerializedName("resend_code_count")
    @Expose
    var resendCodeCount: Int? = null

}