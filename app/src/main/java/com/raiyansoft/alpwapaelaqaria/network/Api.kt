package com.raiyansoft.alpwapaelaqaria.network

import com.raiyansoft.alpwapaelaqaria.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface Api {

    @POST("user/register")
    fun addUser(
        @Header("lang") lang: String,
        @Body user: User
    ): Call<RegisterResponse>

    @POST("user/activateAccount")
    fun activateAccount(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Body code: Activation
    ): Call<VerifyResponse>

    @POST("user/resendActivation")
    fun resendActivation(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<ResendResponse>

    @GET("user/profile")
    fun getProfile(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<ProfileResponse>

    @GET("user/profile")
    fun getRXProfile(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Response<ProfileResponse>

    @GET("home")
    fun getHome(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Response<HomeResponse>

    @GET("properties/categories")
    fun getCategories(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Response<CategoriesResponse>

    @GET("properties/categories")
    fun getExtraCategories(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("page") page: Int
    ): Response<CategoriesResponse>

    @GET("properties/categories")
    fun getRTCategories(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<CategoriesResponse>

    @GET("properties/categories")
    fun getRTExtraCategories(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("page") page: Int
    ): Call<CategoriesResponse>

    @GET("properties/getFav")
    fun getFav(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<FavoritResponse>

    @GET("properties/getFav")
    fun getRXFav(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Response<FavoritResponse>

    @GET("properties/index/special")
    fun getSpecialProperties(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<SpecialPropertiesResponse>

    @GET("properties/index/special")
    fun getExtraSpecialProperties(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("page") page: Int
    ): Call<SpecialPropertiesResponse>

    @GET("properties/index/all")
    fun getAllProperties(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<SpecialPropertiesResponse>

    @GET("properties/index/all")
    fun getAllProperties(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("keyword") keyword: String
    ): Call<SpecialPropertiesResponse>

    @GET("properties/index/all")
    fun getAllFilteredProperties(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("keyword") keyword: String,
        @Query("order") order: Int
    ): Call<SpecialPropertiesResponse>

    @GET("properties/index/all")
    fun getAllProperties(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("cat_id") cat_id: Int
    ): Call<SpecialPropertiesResponse>

    @GET("properties/index/all")
    fun getExtraAllProperties(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("page") page: Int,
        @Query("cat_id") cat_id: Int
    ): Call<SpecialPropertiesResponse>

    @GET("properties/index/all")
    fun getExtraAllProperties(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("page") page: Int
    ): Call<SpecialPropertiesResponse>

    @GET("properties/users/special")
    fun getSpecialSeller(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<AllSellerResponse>

    @GET("properties/users/special")
    fun getExtraSpecialSeller(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("page") page: Int
    ): Call<AllSellerResponse>

    @GET("properties/users/all")
    fun getAllSeller(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<AllSellerResponse>

    @GET("properties/users/all")
    fun getExtraAllSeller(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("page") page: Int
    ): Call<AllSellerResponse>

    @GET("properties/users/all")
    fun getRXAllSeller(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Response<AllSellerResponse>

    @GET("properties/users/all")
    fun getRXExtraAllSeller(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("page") page: Int
    ): Response<AllSellerResponse>

    @GET("properties/details/{id}")
    fun getPropertyDetails(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Path("id") userId: Int
    ): Call<PropertyDetailsResponse>

    @GET("properties/broker/{id}")
    fun getSellerDetails(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Path("id") userId: Int
    ): Call<SellerDetailsResponse>

    @POST("properties/addFav")
    fun addToFav(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("id") id: Int
    ): Call<FavResponse>

    @POST("properties/deleteFav")
    fun removeFromFav(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("id") id: Int
    ): Call<FavResponse>

    @GET("properties/map")
    fun getMap(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Int
    ): Response<MapResponse>

    @Multipart
    @POST("user/update")
    fun editProfileImage(
        @Header("Authorization") Authorization: String,
        @Header("lang") lang: String,
        @Part image: MultipartBody.Part
    ): Call<EditProfileResponse>

    @POST("user/update")
    fun editProfileName(
        @Header("Authorization") Authorization: String,
        @Header("lang") lang: String,
        @Query("name") name: String
    ): Call<EditProfileResponse>

    @GET("user/upgrade")
    fun upgradeAccount(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<FavResponse>

    @GET("conditions")
    fun getTerms(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<TermResponse>

    @GET("faq")
    fun getFaq(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<FaqResponse>

    @GET("privacy")
    fun getPrivacy(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<PrivacyResponse>

    @GET("about")
    fun getAbout(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<AboutResponse>

    @POST("contactUs")
    fun callUs(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Body call: CallUs
    ): Call<FavResponse>

    @GET("setting")
    fun getSet(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<SetResponse>

    @GET("setting")
    fun getSet(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Query("device_token") device_token: String
    ): Call<SetResponse>

    @GET("user/notification")
    fun getNotification(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<NotificationResponse>

    @GET("user/read/{id}")
    fun readNotification(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String,
        @Path("id") id: Int
    ): Call<FavResponse>

    @GET("types")
    fun getType(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<TypeResponse>

    @GET("features")
    fun getFeatures(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<FeatureResponse>

    @GET("cities")
    fun getCities(
        @Header("lang") lang: String
    ): Call<CityResponse>

    @GET("regions/{id}")
    fun getRegions(
        @Path("id") cityId: Int,
        @Query("lang") lang: String
    ): Call<CityResponse>

    @GET("properties/deleteProperty/{id}")
    fun deleteProperty(
        @Path("id") id: Int,
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<FavResponse>

    @GET("user/logout")
    fun logout(
        @Header("lang") lang: String,
        @Header("Authorization") Authorization: String
    ): Call<FavResponse>

    @Multipart
    @POST("properties/createProperty")
    fun uploadCreateProperty(
        @Header("Authorization") Authorization: String,
        @Header("lang") lang: String,
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part>
    ): Call<FavResponse>

    @Multipart
    @POST("properties/updateProperty")
    fun updateProperty(
        @Header("Authorization") Authorization: String,
        @Header("lang") lang: String,
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part>
    ): Call<FavResponse>

    @GET("properties/deleteImage/{id}")
    fun deleteImage(
        @Header("Authorization") Authorization: String,
        @Header("lang") lang: String,
        @Path("id") id: Int
    ): Call<FavResponse>

    @POST("user/addDonation")
    fun promoteProperty(
        @Header("Authorization") Authorization: String,
        @Header("lang") lang: String,
        @Body id: Donation
    ): Call<FavResponse>

}