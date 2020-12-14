package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HomeData {
    @SerializedName("up_banner")
    @Expose
    var upBanner: List<Slider>? = null

    @SerializedName("special_users")
    @Expose
    var specialUsers: List<Seller>? = null

    @SerializedName("middle_banner")
    @Expose
    var middleBanner: List<Slider>? = null

    @SerializedName("special_properties")
    @Expose
    var specialProperties: List<Building>? = null

    @SerializedName("down_banner")
    @Expose
    var downBanner: List<Slider>? = null

    @SerializedName("new_users")
    @Expose
    var newUsers: List<Seller>? = null

    @SerializedName("new_properties")
    @Expose
    var newProperties: List<Building>? = null

}