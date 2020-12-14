package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PropertyDetailsData {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("advertise_type")
    @Expose
    var advertise_type: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("note")
    @Expose
    var note: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("category_id")
    @Expose
    var categoryId: String? = null

    @SerializedName("category_title")
    @Expose
    var categoryTitle: String? = null

    @SerializedName("category_inputs")
    @Expose
    var categoryInputs: List<String>? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("lat")
    @Expose
    var lat: String? = null

    @SerializedName("lng")
    @Expose
    var lng: String? = null

    @SerializedName("rooms")
    @Expose
    var rooms: Int? = null

    @SerializedName("bathes")
    @Expose
    var bathes: Int? = null

    @SerializedName("size")
    @Expose
    var size: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("whats")
    @Expose
    var whats: String? = null

    @SerializedName("floor")
    @Expose
    var floor: String? = null

    @SerializedName("old")
    @Expose
    var old: String? = null

    @SerializedName("payment")
    @Expose
    var payment: Int? = null

    @SerializedName("furniture")
    @Expose
    var furniture: Int? = null

    @SerializedName("floor_numbers")
    @Expose
    var floorNumbers: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("type_id")
    @Expose
    var typeId: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("region")
    @Expose
    var region: String? = null

    @SerializedName("features")
    @Expose
    var features: List<Feature>? = null

    @SerializedName("fav")
    @Expose
    var fav: Boolean? = null

    @SerializedName("images")
    @Expose
    var images: List<Image>? = null

}