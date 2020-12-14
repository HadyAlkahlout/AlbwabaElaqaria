package com.raiyansoft.alpwapaelaqaria.repository

import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder

class MainActivityRepository {

    fun getHome(
        lang: String,
        Authorization: String
    ) = ServiceBuilder.apis!!.getHome(lang, Authorization)


    fun getCategoies(
        lang: String,
        Authorization: String
    ) = ServiceBuilder.apis!!.getCategories(lang, Authorization)

    fun getCategoiesExtra(
        lang: String,
        Authorization: String,
        page: Int
    ) = ServiceBuilder.apis!!.getExtraCategories(lang, Authorization, page)


    fun getWorkers(
        lang: String,
        Authorization: String
    ) = ServiceBuilder.apis!!.getRXAllSeller(lang, Authorization)

    fun getWorkersExtra(
        lang: String,
        Authorization: String,
        page: Int
    ) = ServiceBuilder.apis!!.getRXExtraAllSeller(lang, Authorization, page)

    fun getMap(
        lang: String,
        Authorization: String,
        lat: Double,
        lng: Double,
        radius: Int
    ) = ServiceBuilder.apis!!.getMap(lang, Authorization, lat, lng, radius)

    fun getProfile(
        lang: String,
        Authorization: String
    ) = ServiceBuilder.apis!!.getRXProfile(lang, Authorization)

    fun getFav(
        lang: String,
        Authorization: String
    ) = ServiceBuilder.apis!!.getRXFav(lang, Authorization)

}