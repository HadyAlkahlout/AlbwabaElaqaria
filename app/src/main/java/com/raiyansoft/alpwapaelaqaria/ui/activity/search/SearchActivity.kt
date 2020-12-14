package com.raiyansoft.alpwapaelaqaria.ui.activity.search

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.adapter.PropertyRecycler
import com.raiyansoft.alpwapaelaqaria.util.Common
import com.raiyansoft.alpwapaelaqaria.model.Building
import com.raiyansoft.alpwapaelaqaria.model.SpecialPropertiesResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.InterActivity
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.SettingsActivity
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.custom_dialog.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private lateinit var back: ImageView
    private lateinit var icon: ImageView
    private lateinit var filter: ImageView
    private lateinit var search: EditText
    private lateinit var properties: RecyclerView
    private lateinit var empty: TextView
    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog

    private var open = 0
    private var filterType = 0

    private lateinit var result: ArrayList<Building>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val shared = getSharedPreferences("shared", Context.MODE_PRIVATE)
        val lang = shared.getString("lang", "ar")!!
        currentLang = shared.getString("lang", "ar")!!
        token = shared.getString("userToken", "")!!
        Common.setLocale(lang, this)

        back = imgSearchBack
        icon = imgSearchIcon
        filter = imgSearchFilter
        search = edSearchText
        properties = rvSearchProperty
        empty = tvEmptyResult

        if (currentLang != "ar"){
            back.setImageResource(R.drawable.ic_arrow)
        }
        result = ArrayList()

        dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog)
        title = dialog.tvDialogTitle
        details = dialog.tvDialogText
        ok = dialog.btnOk
        dialog.setCancelable(false)
        ok.setOnClickListener {
            dialog.cancel()
        }

        loading = ProgressDialog(this)
        loading.setCancelable(false)
        loading.setMessage(getString(R.string.loading_data))

        var searchText = ""
        if (intent != null) {
            open = intent.getIntExtra("open", 0)
            if (intent.getStringExtra("search") != null) {
                searchText = intent.getStringExtra("search")!!
            }
        }

        if (searchText != ""){
            search.setText(searchText)
            getProperties()
        }

        icon.setOnClickListener {
            getProperties()
        }

        back.setOnClickListener {
            backClick()
        }

        filter.setOnClickListener { it ->
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.inflate(R.menu.search_filter)
            popupMenu.show()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.itemAll -> {
                filterType = 0
            }
            R.id.itemOld -> {
                filterType = 1
            }
            R.id.itemNew -> {
                filterType = 2
            }
            R.id.itemExpensive -> {
                filterType = 3
            }
            R.id.itemCheaper -> {
                filterType = 4
            }
        }
        getProperties()
        return true
    }

    private fun backClick() {
        val shared = getSharedPreferences("shared", Context.MODE_PRIVATE)
        val edit = shared.edit()
        if (open == 0) {
            edit.putInt("active", 0)
            edit.apply()
            val intent = Intent(this, InterActivity::class.java)
            intent.putExtra("open", 0)
            startActivity(intent)
            finish()
        }else if (open == 1){
            edit.putInt("active", 1)
            edit.apply()
            val intent = Intent(this, InterActivity::class.java)
            intent.putExtra("open", 1)
            startActivity(intent)
            finish()
        }else if (open == 3){
            edit.putInt("active", 3)
            edit.apply()
            val intent = Intent(this, InterActivity::class.java)
            intent.putExtra("open", 3)
            startActivity(intent)
            finish()
        }else if (open == 4){
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("open", 1)
            startActivity(intent)
            finish()
        }
    }

    private fun fillProperty(data: ArrayList<Building>) {
        if (data.isEmpty()) {
            properties.visibility = View.GONE
            empty.visibility = View.VISIBLE
        } else {
            properties.visibility = View.VISIBLE
            empty.visibility = View.GONE
            val adapter = PropertyRecycler(this, data)
            properties.adapter = adapter
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                properties.layoutManager =
                    LinearLayoutManager(this)
            } else {
                properties.layoutManager = GridLayoutManager(this, 2)
            }
        }
    }

    private fun getProperties(){
        if (search.text.isEmpty()){
            title.text = getString(R.string.attention)
            details.text = getString(R.string.empty_fields)
            dialog.show()
        }else{
            loading.show()
            result.clear()
            val service = ServiceBuilder.apis!!
            var call = service.getAllProperties(currentLang, "Bearer $token", search.text.toString())
            if (filterType != 0){
                call = service.getAllFilteredProperties(currentLang, "Bearer $token", search.text.toString(), filterType)
            }
            call.enqueue(object : Callback<SpecialPropertiesResponse> {
                override fun onFailure(call: Call<SpecialPropertiesResponse>, t: Throwable) {
                    title.text = getString(R.string.attention)
                    details.text = getString(R.string.somthing_wrong)
                    dialog.show()
                    loading.dismiss()
                }

                override fun onResponse(
                    call: Call<SpecialPropertiesResponse>,
                    response: Response<SpecialPropertiesResponse>
                ) {
                    if (response.body() != null) {
                        val body = response.body()
                        if (body!!.status!! && body.code == 200) {
                            val data = ArrayList<Building>()
                            for (i in body.data!!.data!!) {
                                data.add(i)
                            }
                            if (body.data!!.pages!! > 1) {
                                var int = 2
                                while (int <= body.data!!.pages!!) {
                                    val extraCall = service.getExtraAllProperties(
                                        currentLang,
                                        "Bearer $token",
                                        int
                                    )
                                    extraCall.enqueue(object : Callback<SpecialPropertiesResponse> {
                                        override fun onFailure(
                                            call: Call<SpecialPropertiesResponse>,
                                            t: Throwable
                                        ) {
                                            title.text = getString(R.string.attention)
                                            details.text = getString(R.string.somthing_wrong)
                                            dialog.show()
                                            loading.dismiss()
                                        }

                                        override fun onResponse(
                                            call: Call<SpecialPropertiesResponse>,
                                            response: Response<SpecialPropertiesResponse>
                                        ) {
                                            if (response.body() != null) {
                                                val extraBody = response.body()
                                                if (extraBody!!.status!! && body.code!! == 200) {
                                                    for (y in extraBody.data!!.data!!) {
                                                        data.add(y)
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
                            fillProperty(data)
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
    }
}