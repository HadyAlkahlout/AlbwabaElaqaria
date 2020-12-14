package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class Building {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("size")
    @Expose
    var size: String? = null

    @SerializedName("rooms")
    @Expose
    var rooms: Int? = null

    @SerializedName("bathes")
    @Expose
    var bathes: Int? = null

    @SerializedName("category_id")
    @Expose
    var categoryId: String? = null

    @SerializedName("category_title")
    @Expose
    var categoryTitle: String? = null

    @SerializedName("images")
    @Expose
    var images: List<Image?>? = null

}