package com.raiyansoft.alpwapaelaqaria.ui.activity.function

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.adapter.FeatureRecycler
import com.raiyansoft.alpwapaelaqaria.adapter.FillFeatureRecycler
import com.raiyansoft.alpwapaelaqaria.adapter.PropertyImageAdapter
import com.raiyansoft.alpwapaelaqaria.model.*
import com.raiyansoft.alpwapaelaqaria.network.Api
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.InterActivity
import com.raiyansoft.alpwapaelaqaria.util.Common
import kotlinx.android.synthetic.main.activity_add_property.*
import kotlinx.android.synthetic.main.custom_dialog.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class AddPropertyActivity : AppCompatActivity(), FeatureRecycler.FeatureClick,
    PropertyImageAdapter.CancelClick {


    private lateinit var back: ImageView
    private lateinit var pick: ImageView
    private lateinit var imageRC: RecyclerView
    private lateinit var sellGoal: Spinner
    private lateinit var department: Spinner
    private lateinit var type: Spinner
    private lateinit var features: RecyclerView
    private lateinit var detail: EditText
    private lateinit var price: EditText
    private lateinit var area: EditText
    private lateinit var call: EditText
    private lateinit var whatsUp: EditText
    private lateinit var city: Spinner
    private lateinit var region: Spinner
    private lateinit var roomNum: EditText
    private lateinit var bathNum: EditText
    private lateinit var floor: EditText
    private lateinit var age: EditText
    private lateinit var floorNum: EditText
    private lateinit var payWay: Spinner
    private lateinit var furneture: Spinner
    private lateinit var location: LinearLayout
    private lateinit var add: Button

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog

    private lateinit var images: ArrayList<Image>
    private lateinit var imgs: ArrayList<Image>
    private lateinit var feature: ArrayList<String>
    private lateinit var categories: ArrayList<Category>
    private lateinit var types: ArrayList<Type>
    private lateinit var cities: ArrayList<City>
    private lateinit var regions: ArrayList<City>
    private lateinit var categoriesTitle: ArrayList<String>
    private lateinit var citiesTitle: ArrayList<String>
    private lateinit var regionsTitle: ArrayList<String>
    private lateinit var typesTitle: ArrayList<String>

    private var propLat = 0.0
    private var propLng = 0.0

    private var cityId = 0

    var case = 0
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_property)

        val shared = getSharedPreferences("shared", Context.MODE_PRIVATE)
        val lang = shared.getString("lang", "ar")!!
        currentLang = shared.getString("lang", "ar")!!
        Common.setLocale(lang, this)

        if (intent != null) {
            case = intent.getIntExtra("case", 0)
            id = intent.getIntExtra("id", 0)
        }

        back = imgAddBack
        pick = imgPick
        imageRC = rcImages
        sellGoal = spSellGoal
        department = spDepartment
        type = spType
        features = rcFeatures
        detail = edDetail
        price = edPrice
        area = edArea
        call = edCall
        whatsUp = edWhatsUp
        city = spCity
        region = spRegion
        roomNum = edRoomNum
        bathNum = edBathNum
        floor = edFloor
        age = edAge
        floorNum = edFloorNum
        payWay = spPayMethod
        furneture = spFun
        location = llLocation
        add = btnAdd

        images = ArrayList()
        imgs = ArrayList()
        feature = ArrayList()
        categories = ArrayList()
        types = ArrayList()
        cities = ArrayList()
        regions = ArrayList()
        categoriesTitle = ArrayList()
        citiesTitle = ArrayList()
        regionsTitle = ArrayList()
        typesTitle = ArrayList()

        if (currentLang != "ar") {
            back.setImageResource(R.drawable.ic_back)
        }

        val sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("userToken", "")!!

        loading = ProgressDialog(this)
        loading.setCancelable(false)
        loading.setMessage(getString(R.string.loading_data))

        dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog)
        title = dialog.tvDialogTitle
        details = dialog.tvDialogText
        ok = dialog.btnOk
        dialog.setCancelable(false)
        ok.setOnClickListener {
            dialog.cancel()
        }

        if (case != 0) {
            add.text = getString(R.string.editProp)
            fillPropertyInfo()
        } else {
            add.text = getString(R.string.add_ad)
            fillDepts()
        }

        back.setOnClickListener {
            backClick()
        }

        add.setOnClickListener {
            addClick()
        }

        pick.setOnClickListener {
            pickClick()
        }

        location.setOnClickListener {
            locationClick()
        }

        city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                for (i in cities) {
                    if (city.selectedItem.toString() == i.title) {
                        cityId = i.id!!
                        break
                    }
                }
                fillRegions()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@AddPropertyActivity, "Task Cancelled", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        department.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                for (i in categories) {
                    if (department.selectedItem.toString() == i.title) {
                        llType.visibility = View.GONE
                        llFeature.visibility = View.GONE
                        llRooms.visibility = View.GONE
                        llBaths.visibility = View.GONE
                        llFloor.visibility = View.GONE
                        llAge.visibility = View.GONE
                        llFloorNum.visibility = View.GONE
                        llPay.visibility = View.GONE
                        llFurniture.visibility = View.GONE
                        for (y in i.inputs!!) {
                            when (y) {
                                "type" -> {
                                    llType.visibility = View.VISIBLE
                                }
                                "features" -> {
                                    llFeature.visibility = View.VISIBLE
                                }
                                "rooms" -> {
                                    llRooms.visibility = View.VISIBLE
                                }
                                "bathes" -> {
                                    llBaths.visibility = View.VISIBLE
                                }
                                "floor" -> {
                                    llFloor.visibility = View.VISIBLE
                                }
                                "old" -> {
                                    llAge.visibility = View.VISIBLE
                                }
                                "floor_numbers" -> {
                                    llFloorNum.visibility = View.VISIBLE
                                }
                                "payment" -> {
                                    llPay.visibility = View.VISIBLE
                                }
                                "furniture" -> {
                                    llFurniture.visibility = View.VISIBLE
                                }
                            }
                        }
                        break
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@AddPropertyActivity, "Task Cancelled", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun fillPropertyInfo() {
        loading.show()
        val call = ServiceBuilder.apis!!.getPropertyDetails(currentLang, "Bearer $token", id)
        call.enqueue(object : Callback<PropertyDetailsResponse> {
            override fun onFailure(call: Call<PropertyDetailsResponse>, t: Throwable) {
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
                loading.dismiss()
            }

            override fun onResponse(
                call: Call<PropertyDetailsResponse>,
                response: Response<PropertyDetailsResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        for (y in body.data!!.categoryInputs!!) {
                            when (y) {
                                "type" -> {
                                    llType.visibility = View.VISIBLE
                                }
                                "features" -> {
                                    llFeature.visibility = View.VISIBLE
                                }
                                "rooms" -> {
                                    llRooms.visibility = View.VISIBLE
                                }
                                "bathes" -> {
                                    llBaths.visibility = View.VISIBLE
                                }
                                "floor" -> {
                                    llFloor.visibility = View.VISIBLE
                                }
                                "old" -> {
                                    llAge.visibility = View.VISIBLE
                                }
                                "floor_numbers" -> {
                                    llFloorNum.visibility = View.VISIBLE
                                }
                                "payment" -> {
                                    llPay.visibility = View.VISIBLE
                                }
                                "furniture" -> {
                                    llFurniture.visibility = View.VISIBLE
                                }
                            }
                        }
                        if (body.data!!.images!!.size > 0) {
                            for (i in body.data!!.images!!) {
                                imgs.add(i)
                            }
                            val adapter = PropertyImageAdapter(
                                this@AddPropertyActivity,
                                imgs,
                                this@AddPropertyActivity
                            )
                            imageRC.adapter = adapter
                            imageRC.layoutManager =
                                LinearLayoutManager(
                                    this@AddPropertyActivity,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                            imageRC.visibility = View.VISIBLE
                        }
                        if (body.data!!.features!!.size > 0) {
                            for (i in body.data!!.features!!) {
                                feature.add(i.id.toString())
                            }
                        }
                        detail.setText(body.data!!.note)
                        price.setText(body.data!!.price)
                        propLat = body.data!!.lat!!.toDouble()
                        propLng = body.data!!.lng!!.toDouble()
                        area.setText(body.data!!.size!!)
                        this@AddPropertyActivity.call.setText(body.data!!.phone!!)
                        whatsUp.setText(body.data!!.whats!!)
                        if (roomNum.isVisible && body.data!!.rooms != null) {
                            roomNum.setText(body.data!!.rooms!!.toString())
                        }
                        if (bathNum.isVisible && body.data!!.bathes != null) {
                            bathNum.setText(body.data!!.bathes!!.toString())
                        }
                        if (floor.isVisible && body.data!!.floor != null) {
                            floor.setText(body.data!!.floor!!.toString())
                        }
                        if (age.isVisible && body.data!!.old != null) {
                            age.setText(body.data!!.old!!.toString())
                        }
                        if (floorNum.isVisible && body.data!!.floorNumbers != null) {
                            floorNum.setText(body.data!!.floorNumbers!!.toString())
                        }
                        if (detail.isVisible && body.data!!.note != null) {
                            detail.setText(body.data!!.note!!.toString())
                        }
                        if (payWay.isVisible && body.data!!.payment != null) {
                            if (body.data!!.payment!! == 1) {
                                payWay.setSelection(0)
                            } else {
                                payWay.setSelection(1)
                            }
                        }
                        if (furneture.isVisible && body.data!!.furniture != null) {
                            if (body.data!!.furniture!! == 1) {
                                furneture.setSelection(0)
                            } else {
                                furneture.setSelection(1)
                            }
                        }
                        if (body.data!!.advertise_type!! == 1) {
                            sellGoal.setSelection(0)
                        } else {
                            sellGoal.setSelection(1)
                        }
                        fillDepts()
                    } else {
                        title.text = getString(R.string.attention)
                        details.text = getString(R.string.somthing_wrong)
                        dialog.show()
                        loading.dismiss()
                    }
                }
            }

        })
    }

    private fun backClick() {
        val shared = getSharedPreferences("shared", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putInt("active", 4)
        editor.apply()
        val intent = Intent(this, InterActivity::class.java)
        intent.putExtra("open", 4)
        startActivity(intent)
        finish()
    }

    private fun pickClick() {
        val bol = if (case == 0) {
            images.size < 4
        } else {
            imgs.size < 4
        }
        if (bol) {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        val i =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(i, 100)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Toast.makeText(
                            this@AddPropertyActivity,
                            "Task Cancelled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token!!.continuePermissionRequest()
                    }
                }).check()
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result: String = cursor.getString(column_index)
        cursor.close()
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 100 && data!!.data != null) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data.data
            val bol = if (case == 0) {
                images.size < 4
            } else {
                imgs.size < 4
            }
            if (bol) {
                val img = Image()
                img.image = fileUri.toString()
                img.id = images.size - 1
                images.add(img)
                imgs.add(img)
                val adapter = PropertyImageAdapter(this, imgs, this)
                imageRC.adapter = adapter
                imageRC.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                imageRC.visibility = View.VISIBLE
                if (images.size < 4) {
                    Toast.makeText(this, getString(R.string.photo_added), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.add_out), Toast.LENGTH_SHORT).show()
                }
            }
            
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 10 && data != null) {
            propLat = data.getDoubleExtra("lat", 0.0)
            propLng = data.getDoubleExtra("lng", 0.0)
        }
    }

    private fun fillDepts() {
        loading.show()
        val call = ServiceBuilder.apis!!.getRTCategories(currentLang, "Bearer $token")
        call.enqueue(object : Callback<CategoriesResponse> {
            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                loading.dismiss()
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
            }

            override fun onResponse(
                call: Call<CategoriesResponse>,
                response: Response<CategoriesResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        for (i in body.data!!.data!!) {
                            categories.add(i)
                        }
                        if (body.data!!.pages!! > 1) {
                            var int = 2
                            while (int <= body.data!!.pages!!) {
                                val extraCall =
                                    ServiceBuilder.apis!!.getRTExtraCategories(
                                        currentLang,
                                        "Bearer $token",
                                        int
                                    )
                                extraCall.enqueue(object : Callback<CategoriesResponse> {
                                    override fun onFailure(
                                        call: Call<CategoriesResponse>,
                                        t: Throwable
                                    ) {
                                        title.text = getString(R.string.attention)
                                        details.text = getString(R.string.somthing_wrong)
                                        dialog.show()
                                        loading.dismiss()
                                    }

                                    override fun onResponse(
                                        call: Call<CategoriesResponse>,
                                        response: Response<CategoriesResponse>
                                    ) {
                                        if (response.body() != null) {
                                            val extraBody = response.body()
                                            if (extraBody!!.status!! && body.code!! == 200) {
                                                for (y in extraBody.data!!.data!!) {
                                                    categories.add(y)
                                                }
                                            }
                                        } else {
                                            title.text = getString(R.string.attention)
                                            details.text = getString(R.string.somthing_wrong)
                                            dialog.show()
                                            loading.dismiss()
                                        }
                                    }

                                })
                                int++
                            }
                        }
                        for (i in categories) {
                            categoriesTitle.add(i.title!!)
                        }
                        department.adapter = ArrayAdapter<String>(
                            this@AddPropertyActivity,
                            android.R.layout.simple_list_item_1,
                            categoriesTitle
                        )
                        fillTypes()
                    } else {
                        title.text = getString(R.string.attention)
                        details.text = getString(R.string.somthing_wrong)
                        dialog.show()
                        loading.dismiss()
                    }
                }
            }
        })
    }

    private fun fillTypes() {
        val call = ServiceBuilder.apis!!.getType(currentLang, "Bearer $token")
        call.enqueue(object : Callback<TypeResponse> {
            override fun onFailure(call: Call<TypeResponse>, t: Throwable) {
                loading.dismiss()
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
            }

            override fun onResponse(
                call: Call<TypeResponse>,
                response: Response<TypeResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        for (i in body.data!!.data!!) {
                            typesTitle.add(i.title!!)
                            types.add(i)
                        }
                        type.adapter = ArrayAdapter<String>(
                            this@AddPropertyActivity,
                            android.R.layout.simple_list_item_1,
                            typesTitle
                        )
                        fillFeatures()
                    } else {
                        title.text = getString(R.string.attention)
                        details.text = getString(R.string.somthing_wrong)
                        dialog.show()
                        loading.dismiss()
                    }
                }
            }
        })
    }

    private fun fillFeatures() {
        val call = ServiceBuilder.apis!!.getFeatures(currentLang, "Bearer $token")
        call.enqueue(object : Callback<FeatureResponse> {
            override fun onFailure(call: Call<FeatureResponse>, t: Throwable) {
                loading.dismiss()
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
            }

            override fun onResponse(
                call: Call<FeatureResponse>,
                response: Response<FeatureResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        if (case == 0) {
                            val adapter = FeatureRecycler(
                                this@AddPropertyActivity,
                                body.data!!.data!!,
                                this@AddPropertyActivity
                            )
                            features.adapter = adapter
                            features.layoutManager = LinearLayoutManager(
                                this@AddPropertyActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        } else {
                            val fets = ArrayList<FillFeature>()
                            for (i in body.data!!.data!!) {
                                var exist = 0
                                for (y in feature) {
                                    if (i.id!!.toString() == y) {
                                        exist = 1
                                        break
                                    }
                                }
                                if (exist == 1) {
                                    fets.add(FillFeature(i.id!!, i.title!!, true))
                                } else {
                                    fets.add(FillFeature(i.id!!, i.title!!, false))
                                }
                            }
                            val adapter = FillFeatureRecycler(
                                this@AddPropertyActivity,
                                fets,
                                this@AddPropertyActivity
                            )
                            features.adapter = adapter
                            features.layoutManager = LinearLayoutManager(
                                this@AddPropertyActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        }
                        fillCities()
                    } else {
                        title.text = getString(R.string.attention)
                        details.text = getString(R.string.somthing_wrong)
                        dialog.show()
                        loading.dismiss()
                    }
                }
            }
        })
    }

    override fun onClick(id: Int, isChecked: Boolean) {
        if (isChecked) {
            feature.add(id.toString())
        } else {
            var index = 0
            var position = 0
            for (i in feature) {
                if (i == id.toString()) {
                    position = index
                }
                index++
            }
            feature.removeAt(position)
        }
    }

    private fun fillCities() {
        val call = ServiceBuilder.apis!!.getCities(currentLang)
        call.enqueue(object : Callback<CityResponse> {
            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                loading.dismiss()
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
            }

            override fun onResponse(
                call: Call<CityResponse>,
                response: Response<CityResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        for (i in body.data!!) {
                            citiesTitle.add(i.title!!)
                            cities.add(i)
                        }
                        city.adapter = ArrayAdapter<String>(
                            this@AddPropertyActivity,
                            android.R.layout.simple_list_item_1,
                            citiesTitle
                        )
                        cityId = cities[0].id!!
                        fillRegions()
                    } else {
                        title.text = getString(R.string.attention)
                        details.text = getString(R.string.somthing_wrong)
                        dialog.show()
                        loading.dismiss()
                    }
                }
            }
        })
    }

    private fun fillRegions() {
        loading.show()
        val call = ServiceBuilder.apis!!.getRegions(cityId, currentLang)
        call.enqueue(object : Callback<CityResponse> {
            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                loading.dismiss()
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
            }

            override fun onResponse(
                call: Call<CityResponse>,
                response: Response<CityResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        regionsTitle.clear()
                        for (i in body.data!!) {
                            regionsTitle.add(i.title!!)
                            regions.add(i)
                        }

                        region.adapter = ArrayAdapter<String>(
                            this@AddPropertyActivity,
                            android.R.layout.simple_list_item_1,
                            regionsTitle
                        )
                        loading.dismiss()
                    } else {
                        title.text = getString(R.string.attention)
                        details.text = getString(R.string.somthing_wrong)
                        dialog.show()
                        loading.dismiss()
                    }
                }
            }
        })
    }

    private fun locationClick() {
        val intent = Intent(this, PickLocationActivity::class.java)
        if (propLat != 0.0 && propLng != 0.0) {
            intent.putExtra("lat", propLat)
            intent.putExtra("lng", propLng)
        }
        startActivityForResult(intent, 10)
    }

    fun toRequestBody(value: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
    }

    override fun cancelClick(position: Int, id: Int) {
        if (case == 0) {
            images.removeAt(position)
            val adapter = PropertyImageAdapter(this, images, this)
            imageRC.adapter = adapter
            imageRC.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        } else {
            loading.show()
            val call = ServiceBuilder.apis!!.deleteImage("Bearer $token", currentLang, id)
            call.enqueue(object : Callback<FavResponse> {
                override fun onResponse(call: Call<FavResponse>, response: Response<FavResponse>) {
                    if (response.body() != null) {
                        val body = response.body()
                        if (body!!.status!! && body.code!! == 200) {
                            loading.dismiss()
                            imgs.removeAt(position)
                            val adapter = PropertyImageAdapter(
                                this@AddPropertyActivity,
                                imgs,
                                this@AddPropertyActivity
                            )
                            imageRC.adapter = adapter
                            imageRC.layoutManager =
                                LinearLayoutManager(
                                    this@AddPropertyActivity,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                        } else {
                            title.text = getString(R.string.attention)
                            details.text = getString(R.string.somthing_wrong)
                            dialog.show()
                            loading.dismiss()
                        }
                    }
                }

                override fun onFailure(call: Call<FavResponse>, t: Throwable) {
                    title.text = getString(R.string.attention)
                    details.text = getString(R.string.somthing_wrong)
                    dialog.show()
                    loading.dismiss()
                }
            })
        }
    }

    private fun addClick() {
        if (detail.text.isEmpty() || price.text.isEmpty() || call.text.isEmpty() ||
            whatsUp.text.isEmpty() || propLat == 0.0 || propLng == 0.0
        ) {
            title.text = getString(R.string.attention)
            details.text = getString(R.string.empty_fields)
            dialog.show()
        } else {
            var map: MutableMap<String, RequestBody> = HashMap()
            var mapimage = ArrayList<MultipartBody.Part>()

            loading.show()
            if (images.isNotEmpty()) {
                for ((i, image) in images.withIndex()) {
                    val imagefile = File(getRealPathFromURI(Uri.parse(image.image)))
                    val reqBody = RequestBody.create("images[$i]".toMediaTypeOrNull(), imagefile)
                    val partImage: MultipartBody.Part =
                        createFormData("images[$i]", imagefile.name, reqBody)
                    mapimage.add(partImage)
                }
            }

            var goal = if (sellGoal.selectedItemPosition == 0) {
                1
            } else {
                2
            }
            map["advertise_type"] = toRequestBody(goal.toString())
            map["lat"] = toRequestBody(propLat.toString())
            map["lng"] = toRequestBody(propLng.toString())
            var cat = 0
            for (i in categories) {
                if (department.selectedItem.toString() == i.title) {
                    cat = i.id!!
                    break
                }
            }
            map["cat_id"] = toRequestBody(cat.toString())
            map["price"] = toRequestBody(price.text.toString())
            map["size"] = toRequestBody(area.text.toString())
            map["phone"] = toRequestBody(call.text.toString())
            map["whats"] = toRequestBody(whatsUp.text.toString())
            var cityID = 0
            for (x in cities) {
                if (city.selectedItem.toString() == x.title) {
                    cityID = x.id!!
                    break
                }
            }
            map["city_id"] = toRequestBody(cityID.toString())
            var regionID = 0
            for (z in regions) {
                if (region.selectedItem.toString() == z.title) {
                    regionID = z.id!!
                    break
                }
            }
            map["region_id"] = toRequestBody(regionID.toString())
            map["note"] = toRequestBody(detail.text.toString())

            if (llFeature.isVisible && feature.isNotEmpty()) {
                for ((i, fet) in feature.withIndex()) {
                    map["features[$i]"] = toRequestBody(fet)
                }
            }

            if (llType.isVisible) {
                var typeID = 0
                for (y in types) {
                    if (type.selectedItem.toString() == y.title) {
                        typeID = y.id!!
                        break
                    }
                }
                map["type_id"] = toRequestBody(typeID.toString())
            }


            if (llRooms.isVisible) {
                map["rooms"] = toRequestBody(roomNum.text.toString())
            }

            if (llBaths.isVisible) {
                map["bathes"] = toRequestBody(bathNum.text.toString())
            }

            if (llFloor.isVisible) {
                map["floor"] = toRequestBody(floor.text.toString())
            }

            if (llAge.isVisible) {
                map["old"] = toRequestBody(age.text.toString())
            }

            if (llFloorNum.isVisible) {
                map["floor_numbers"] = toRequestBody(floorNum.text.toString())
            }

            if (llPay.isVisible) {
                var pay = if (payWay.selectedItemPosition == 0) {
                    1
                } else {
                    2
                }
                map["payment"] = toRequestBody(pay.toString())
            }

            if (llFurniture.isVisible) {
                var propFurniture = if (furneture.selectedItemPosition == 0) {
                    1
                } else {
                    2
                }
                map["furniture"] = toRequestBody(propFurniture.toString())
            }

            val retrofit = Retrofit.Builder()
                .baseUrl("https://albawabaa.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api =
                retrofit.create(Api::class.java)

            val intent =
                Intent(this@AddPropertyActivity, InterActivity::class.java)
            intent.putExtra("open", 4)

            if (case != 0) {
                map["id"] = toRequestBody(id.toString())
                val call =
                    api.updateProperty(
                        "Bearer $token", currentLang,
                        map, mapimage
                    )
                call.enqueue(object : Callback<FavResponse> {
                    override fun onResponse(
                        call: Call<FavResponse>,
                        response: Response<FavResponse>
                    ) {
                        loading.dismiss()
                        if (response.body()!!.status!!) {
                            Toast.makeText(
                                this@AddPropertyActivity,
                                getString(R.string.edit_done),
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onFailure(
                        call: Call<FavResponse>,
                        t: Throwable
                    ) {
                        loading.dismiss()
                        Toast.makeText(
                            this@AddPropertyActivity,
                            getString(R.string.somthing_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                val call =
                    api.uploadCreateProperty(
                        "Bearer $token", currentLang,
                        map, mapimage
                    )
                call.enqueue(object : Callback<FavResponse> {
                    override fun onResponse(
                        call: Call<FavResponse>,
                        response: Response<FavResponse>
                    ) {
                        loading.dismiss()
                        if (response.body()!!.status!!) {
                            Toast.makeText(
                                this@AddPropertyActivity,
                                getString(R.string.add_done),
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onFailure(
                        call: Call<FavResponse>,
                        t: Throwable
                    ) {
                        loading.dismiss()
                        Toast.makeText(
                            this@AddPropertyActivity,
                            getString(R.string.somthing_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }
}