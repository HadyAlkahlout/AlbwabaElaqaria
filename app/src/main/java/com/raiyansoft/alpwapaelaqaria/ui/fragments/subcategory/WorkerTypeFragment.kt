package com.raiyansoft.alpwapaelaqaria.ui.fragments.subcategory

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.adapter.SellerRecycler
import com.raiyansoft.alpwapaelaqaria.ui.fragments.home.HomeFragment
import com.raiyansoft.alpwapaelaqaria.model.AllSellerResponse
import com.raiyansoft.alpwapaelaqaria.model.Seller
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_worker_type.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class WorkerTypeFragment : Fragment() {

    private lateinit var back: ImageView
    private lateinit var contentTitle: TextView
    private lateinit var workers: RecyclerView

    private var type = 0

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_worker_type, container, false)

        back = root.imgWorkersBack
        contentTitle = root.tvWorkersTitle
        workers = root.rcWorkers

        val sharedPreferences = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("userToken", "")!!
        currentLang = sharedPreferences.getString("lang", "ar")!!

        if (currentLang != "ar"){
            back.setImageResource(R.drawable.ic_back)
        }

        back.setOnClickListener {
            backClick()
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

        loading = ProgressDialog(activity)
        loading.setCancelable(false)
        loading.setMessage(getString(R.string.loading_data))


        val bundle = arguments
        if (bundle != null) {
            type = bundle.getInt("type")
        }
        getSellers()
        return root
    }

    private fun backClick() {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.flInterHolder, HomeFragment()).commit()
    }

    private fun fillWorkers(data: ArrayList<Seller>) {
        if (data.isEmpty()) {
            workers.visibility = View.GONE
        } else {
            val adapter = SellerRecycler(activity!!, data)
            workers.adapter = adapter
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                workers.layoutManager = GridLayoutManager(activity, 3)
            } else {
                workers.layoutManager = GridLayoutManager(activity, 6)
            }
        }
    }

    private fun getSellers() {
        loading.show()
        val service = ServiceBuilder.apis!!
        val call = service.getSpecialSeller(currentLang, "Bearer $token")
        if (type == 1) {
            contentTitle.text = getString(R.string.good_seller)
            call.enqueue(object : Callback<AllSellerResponse> {
                override fun onFailure(call: Call<AllSellerResponse>, t: Throwable) {
                    title.text = getString(R.string.attention)
                    details.text = getString(R.string.somthing_wrong)
                    dialog.show()
                    loading.dismiss()
                }

                override fun onResponse(
                    call: Call<AllSellerResponse>,
                    response: Response<AllSellerResponse>
                ) {
                    if (response.body() != null) {
                        val body = response.body()
                        if (body!!.status!! && body.code == 200) {
                            val data = ArrayList<Seller>()
                            for (i in body.data!!.data!!) {
                                data.add(i)
                            }
                            if (body.data!!.pages!! > 1) {
                                var int = 2
                                while (int <= body.data!!.pages!!) {
                                    val extraCall = service.getExtraSpecialSeller(
                                        currentLang,
                                        "Bearer $token",
                                        int
                                    )
                                    extraCall.enqueue(object : Callback<AllSellerResponse> {
                                        override fun onFailure(
                                            call: Call<AllSellerResponse>,
                                            t: Throwable
                                        ) {
                                            title.text = getString(R.string.attention)
                                            details.text = getString(R.string.somthing_wrong)
                                            dialog.show()
                                            loading.dismiss()
                                        }

                                        override fun onResponse(
                                            call: Call<AllSellerResponse>,
                                            response: Response<AllSellerResponse>
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
                            fillWorkers(data)
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
        } else {
            contentTitle.text = getString(R.string.new_seller)
            val allCall = service.getAllSeller(currentLang, "Bearer $token")
            allCall.enqueue(object : Callback<AllSellerResponse> {
                override fun onFailure(call: Call<AllSellerResponse>, t: Throwable) {
                    title.text = getString(R.string.attention)
                    details.text = getString(R.string.somthing_wrong)
                    dialog.show()
                    loading.dismiss()
                }

                override fun onResponse(
                    call: Call<AllSellerResponse>,
                    response: Response<AllSellerResponse>
                ) {
                    if (response.body() != null) {
                        val body = response.body()
                        if (body!!.status!! && body.code == 200) {
                            val data = ArrayList<Seller>()
                            for (i in body.data!!.data!!) {
                                data.add(i)
                            }
                            if (body.data!!.pages!! > 1) {
                                var int = 2
                                while (int <= body.data!!.pages!!) {
                                    val extraCall =
                                        service.getExtraAllSeller(currentLang, "Bearer $token", int)
                                    extraCall.enqueue(object : Callback<AllSellerResponse> {
                                        override fun onFailure(
                                            call: Call<AllSellerResponse>,
                                            t: Throwable
                                        ) {
                                            title.text = getString(R.string.attention)
                                            details.text = getString(R.string.somthing_wrong)
                                            dialog.show()
                                            loading.dismiss()
                                        }

                                        override fun onResponse(
                                            call: Call<AllSellerResponse>,
                                            response: Response<AllSellerResponse>
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
                            fillWorkers(data)
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