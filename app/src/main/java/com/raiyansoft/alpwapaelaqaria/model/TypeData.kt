package com.raiyansoft.alpwapaelaqaria.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class TypeData {
    @SerializedName("data")
    @Expose
    var data: List<Type>? = null
}