package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Category {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("properties")
    @Expose
    var properties: Int? = null

    @SerializedName("subCategory")
    @Expose
    var subCategory: Int? = null

    @SerializedName("inputs")
    @Expose
    var inputs: List<String>? = null

}