package com.raiyansoft.alpwapaelaqaria.ui.fragments.subcategory

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.InterActivity
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.adapter.CategoriesHeadRecycler
import com.raiyansoft.alpwapaelaqaria.adapter.PropertyRecycler
import com.raiyansoft.alpwapaelaqaria.model.Building
import com.raiyansoft.alpwapaelaqaria.model.CategoriesResponse
import com.raiyansoft.alpwapaelaqaria.model.Category
import com.raiyansoft.alpwapaelaqaria.model.SpecialPropertiesResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_categories_head.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesHeadFragment : Fragment(), CategoriesHeadRecycler.PhotoCliCk {

    private lateinit var back: ImageView
    private lateinit var heads: RecyclerView
    private lateinit var properties: RecyclerView

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog

    private var catId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_categories_head, container, false)

        val shared = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        currentLang = shared.getString("lang", "ar")!!
        token = shared.getString("userToken", "")!!

        back = root.imgCategoriesHeadBack
        heads = root.rvCategoriesHead
        properties = root.rvCatHeadProperty

        if (currentLang != "ar"){
            back.setImageResource(R.drawable.ic_back)
        }

        dialog = Dialog(activity!!)
        dialog.setContentView(R.layout.custom_dialog)
        title = dialog.tvDialogTitle
        details = dialog.tvDialogText
        ok = dialog.btnOk
        dialog.setCancelable(false)
        ok.setOnClickListener {
            dialog.cancel()
        }

        loading = ProgressDialog(activity!!)
        loading.setCancelable(false)
        loading.setMessage(getString(R.string.loading_data))

        if (arguments != null) {
            catId = arguments!!.getInt("id", 0)
        }

        getCategories()
        getProperties(-1)

        back.setOnClickListener {
            backClick()
        }

        return root
    }

    private fun backClick() {
        val intent = Intent(activity!!, InterActivity::class.java)
        intent.putExtra("open", 2)
        startActivity(intent)
        activity!!.finish()
    }

    private fun fillHeads(data: List<Category>) {
        val adapter = CategoriesHeadRecycler(activity!!, data, this)
        heads.adapter = adapter
        heads.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun fillProperty(data: ArrayList<Building>) {
        if (data.isEmpty()) {
            properties.visibility = View.GONE
        } else {
            properties.visibility = View.VISIBLE
            val adapter = PropertyRecycler(activity!!, data)
            properties.adapter = adapter
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                properties.layoutManager =
                    LinearLayoutManager(activity!!)
            } else {
                properties.layoutManager = GridLayoutManager(activity!!, 2)
            }
        }
    }

    override fun onClick(id: Int) {
        getProperties(id)
    }

    private fun getCategories() {
        loading.show()
        val service = ServiceBuilder.apis!!
        val call = service.getRTCategories(currentLang, "Bearer $token")
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
                        val data = java.util.ArrayList<Category>()
                        for (i in body.data!!.data!!) {
                            data.add(i)
                        }
                        if (body.data!!.pages!! > 1) {
                            var int = 2
                            while (int <= body.data!!.pages!!) {
                                val extraCall =
                                    service.getRTExtraCategories(currentLang, "Bearer $token", int)
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
                        fillHeads(data)
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

    private fun getProperties(id: Int) {
        if (id != -1){
            catId = id
        }
        loading.show()
        val service = ServiceBuilder.apis!!
        val call = service.getAllProperties(currentLang, "Bearer $token", catId)
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
                                    int,
                                    catId
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