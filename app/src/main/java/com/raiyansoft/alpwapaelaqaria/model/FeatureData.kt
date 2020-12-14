package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class FeatureData {
    @SerializedName("data")
    @Expose
    var data: List<Feature>? = null
}