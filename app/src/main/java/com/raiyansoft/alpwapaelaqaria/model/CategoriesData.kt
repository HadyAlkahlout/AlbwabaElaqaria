package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CategoriesData {
    @SerializedName("count_total")
    @Expose
    var countTotal: Int? = null

    @SerializedName("nextPageUrl")
    @Expose
    var nextPageUrl: Any? = null

    @SerializedName("pages")
    @Expose
    var pages: Int? = null

    @SerializedName("data")
    @Expose
    var data: List<Category>? = null

}