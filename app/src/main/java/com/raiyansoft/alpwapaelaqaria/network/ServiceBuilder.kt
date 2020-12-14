package com.raiyansoft.alpwapaelaqaria.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class ServiceBuilder {

    companion object {

        private val baseURL = "https://albawabaa.com/api/"
        var apis: Api? = null


        init {
            val client = OkHttpClient.Builder()
                .build()
            apis = getRetrofitInstance(client).create(Api::class.java)
        }

        fun getRetrofitInstance(client:OkHttpClient): Retrofit =
             Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                 .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()

    }

}