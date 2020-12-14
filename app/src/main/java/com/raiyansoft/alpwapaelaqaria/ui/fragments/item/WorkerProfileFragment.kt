package com.raiyansoft.alpwapaelaqaria.ui.fragments.item

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.adapter.PropertyRecycler
import com.raiyansoft.alpwapaelaqaria.model.Building
import com.raiyansoft.alpwapaelaqaria.model.SellerDetailsResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_worker_profile.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkerProfileFragment : Fragment() {

    private lateinit var back: ImageView
    private lateinit var profile: ImageView
    private lateinit var call: CardView
    private lateinit var pageTitle: TextView
    private lateinit var views: TextView
    private lateinit var name: TextView
    private lateinit var joinDate: TextView
    private lateinit var description: TextView
    private lateinit var property: RecyclerView

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog

    private var propertyId: Int = 0

    private var sellerPhone = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_worker_profile, container, false)

        val shared = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        currentLang = shared.getString("lang", "ar")!!
        token = shared.getString("userToken", "")!!

        back = root.imgSellerInfoBack
        profile = root.imgSellerImage
        call = root.cvCall
        pageTitle = root.tvSellerInfoTitle
        views = root.tvSellerViewsNum
        name = root.tvSellerName
        joinDate = root.tvSellerJoinDate
        description = root.tvSellerDescription
        property = root.rvSellerProperty

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
            propertyId = arguments!!.getInt("id", 0)
        }

        fillInfo()

        back.setOnClickListener {
            backClick()
        }

        call.setOnClickListener {
            callClick()
        }

        return root
    }

    private fun fillInfo() {
        loading.show()
        val service =  ServiceBuilder.apis!!
        val call = service.getSellerDetails(currentLang, "Bearer $token", propertyId)
        call.enqueue(object : Callback<SellerDetailsResponse> {
            override fun onFailure(call: Call<SellerDetailsResponse>, t: Throwable) {
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
                loading.dismiss()
            }

            override fun onResponse(
                call: Call<SellerDetailsResponse>,
                response: Response<SellerDetailsResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        Glide
                            .with(activity!!)
                            .load(body.data!!.info!!.image)
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(profile)
                        name.text = body.data!!.info!!.name.toString()
                        joinDate.text = body.data!!.info!!.date.toString()
                        if (body.data!!.info!!.note.toString() == "null"){
                            description.visibility = View.GONE
                        }else {
                            description.text = body.data!!.info!!.note.toString()
                        }
                        views.text = body.data!!.info!!.views.toString()
                        pageTitle.text = name.text.toString()
                        sellerPhone = body.data!!.info!!.mobile!!.toString()
                        val data = ArrayList<Building>()
                        data.addAll(body.data!!.properties!!)
                        fillProperties(data)
                        loading.dismiss()
                    }else{
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
        activity!!.onBackPressed()
    }

    private fun callClick() {
        val i = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${sellerPhone}")
        }
        if (i.resolveActivity(activity!!.packageManager) != null) {
            startActivity(i)
        }
    }

    private fun fillProperties(data: ArrayList<Building>) {
        if (data.isEmpty()) {
            property.visibility = View.GONE
        } else {
            val adapter = PropertyRecycler(activity!!, data)
            property.adapter = adapter
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                property.layoutManager =
                    LinearLayoutManager(activity!!)
            } else {
                property.layoutManager = GridLayoutManager(activity!!, 2)
            }
            property.visibility = View.VISIBLE
        }
    }
}