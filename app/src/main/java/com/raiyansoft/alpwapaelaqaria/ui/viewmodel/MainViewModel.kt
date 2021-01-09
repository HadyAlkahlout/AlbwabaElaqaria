package com.raiyansoft.alpwapaelaqaria.ui.viewmodel

import android.app.Application
import android.content.Context
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.model.*
import com.raiyansoft.alpwapaelaqaria.repository.MainActivityRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MainActivityRepository()

    val dataHomeLiveData = MutableLiveData<HomeResponse>()
    val categoriesLiveData = MutableLiveData<CategoriesResponse>()
    val categoriesExtraLiveData = MutableLiveData<CategoriesResponse>()
    val workersLiveData = MutableLiveData<AllSellerResponse>()
    val workersExtraLiveData = MutableLiveData<AllSellerResponse>()
    val mapLiveData = MutableLiveData<MapResponse>()
    val profileLiveData = MutableLiveData<ProfileResponse>()
    val favLiveData = MutableLiveData<FavoritResponse>()

    val sharedPreferences =
        application.baseContext.getSharedPreferences("shared", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("userToken", "")!!
    val currentLang = sharedPreferences.getString("lang", "ar")!!

    private fun getDataHome() {
        val response = repository.getHome(currentLang, "Bearer $token")
        response.enqueue(object : Callback<HomeResponse> {
            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                dataHomeLiveData.value = null
            }

            override fun onResponse(
                call: Call<HomeResponse>,
                response: Response<HomeResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    dataHomeLiveData.value = body
                }
            }
        })
    }

    fun getDataProfile() {
        val response = repository.getProfile(currentLang, "Bearer $token")
        response.enqueue(object : Callback<ProfileResponse> {
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                profileLiveData.value = null
            }

            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    profileLiveData.value = body
                }
            }
        })
    }

    fun getDataFav() {
        val response = repository.getFav(currentLang, "Bearer $token")
        response.enqueue(object : Callback<FavoritResponse> {
            override fun onFailure(call: Call<FavoritResponse>, t: Throwable) {
                favLiveData.value = null
            }

            override fun onResponse(
                call: Call<FavoritResponse>,
                response: Response<FavoritResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    favLiveData.value = body
                }
            }
        })
    }

    fun getDataMap(lat: Double, lng: Double, radius: Int) {
        val response = repository.getMap(currentLang, "Bearer $token", lat, lng, radius)
        response.enqueue(object : Callback<MapResponse> {
            override fun onFailure(call: Call<MapResponse>, t: Throwable) {
                mapLiveData.value = null
            }

            override fun onResponse(
                call: Call<MapResponse>,
                response: Response<MapResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    mapLiveData.value = body
                }
            }
        })
    }

    private fun getCategories() {
        val response = repository.getCategoies(currentLang, "Bearer $token")
        response.enqueue(object : Callback<CategoriesResponse> {
            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                categoriesLiveData.value = null
            }

            override fun onResponse(
                call: Call<CategoriesResponse>,
                response: Response<CategoriesResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    categoriesLiveData.value = body
                }
            }
        })
    }

    fun getExtraCategories(pageCategories: Int) {
        val response = repository.getCategoiesExtra(currentLang, "Bearer $token", pageCategories)
        response.enqueue(object : Callback<CategoriesResponse> {
            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                categoriesExtraLiveData.value = null
            }

            override fun onResponse(
                call: Call<CategoriesResponse>,
                response: Response<CategoriesResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    categoriesExtraLiveData.value = body
                }
            }
        })
    }

    private fun getWorkers() {
        val response = repository.getWorkers(currentLang, "Bearer $token")
        response.enqueue(object : Callback<AllSellerResponse> {
            override fun onFailure(call: Call<AllSellerResponse>, t: Throwable) {
                workersLiveData.value = null
            }

            override fun onResponse(
                call: Call<AllSellerResponse>,
                response: Response<AllSellerResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    workersLiveData.value = body
                }
            }
        })
    }

    fun getExtraWorkers(pageCategories: Int) {
        val response = repository.getWorkersExtra(currentLang, "Bearer $token", pageCategories)
        response.enqueue(object : Callback<AllSellerResponse> {
            override fun onFailure(call: Call<AllSellerResponse>, t: Throwable) {
                workersExtraLiveData.value = null
            }

            override fun onResponse(
                call: Call<AllSellerResponse>,
                response: Response<AllSellerResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    workersExtraLiveData.value = body
                }
            }
        })
    }

    init {
        getDataHome()
        getCategories()
        getWorkers()
        getDataProfile()
        getDataFav()
    }


}