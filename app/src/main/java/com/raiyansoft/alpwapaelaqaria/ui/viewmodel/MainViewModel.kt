package com.raiyansoft.alpwapaelaqaria.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.raiyansoft.alpwapaelaqaria.model.*
import com.raiyansoft.alpwapaelaqaria.repository.MainActivityRepository

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
        dataHomeLiveData.value = response.body()
    }

    fun getDataProfile() {
        val response = repository.getProfile(currentLang, "Bearer $token")
        profileLiveData.value = response.body()
    }

    fun getDataFav() {
        val response = repository.getFav(currentLang, "Bearer $token")
        favLiveData.value = response.body()
    }

    fun getDataMap(lat: Double, lng: Double, radius: Int) {
        val response = repository.getMap(currentLang, "Bearer $token", lat, lng, radius)
        mapLiveData.value = response.body()
    }

    private fun getCategories() {
        val response = repository.getCategoies(currentLang, "Bearer $token")
        categoriesLiveData.value = response.body()
    }

    fun getExtraCategories(pageCategories: Int) {
        val response = repository.getCategoiesExtra(currentLang, "Bearer $token", pageCategories)
        categoriesExtraLiveData.value = response.body()
    }

    private fun getWorkers() {
        val response = repository.getWorkers(currentLang, "Bearer $token")
        workersLiveData.value = response.body()
    }

    fun getExtraWorkers(pageCategories: Int) {
        val response = repository.getWorkersExtra(currentLang, "Bearer $token", pageCategories)
        workersExtraLiveData.value = response.body()
    }

    init {
        getDataHome()
        getCategories()
        getWorkers()
        getDataProfile()
        getDataFav()
    }


}