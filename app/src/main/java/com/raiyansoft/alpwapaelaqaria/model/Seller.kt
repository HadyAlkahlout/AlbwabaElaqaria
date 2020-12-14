package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class Seller{
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

}