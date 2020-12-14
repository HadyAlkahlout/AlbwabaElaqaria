package com.raiyansoft.alpwapaelaqaria.ui.fragments.settings

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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.InterActivity
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.ui.activity.search.SearchActivity
import com.raiyansoft.alpwapaelaqaria.adapter.PropertyRecycler
import com.raiyansoft.alpwapaelaqaria.model.Building
import com.raiyansoft.alpwapaelaqaria.model.FavoritResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_favorite.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FavoriteFragment : Fragment() {

    private lateinit var back: ImageView
    private lateinit var search: ImageView
    private lateinit var shop: Button
    private lateinit var products: RecyclerView
    private lateinit var emptyFav: LinearLayout

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog

    private var case = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_favorite, container, false)

        val shared = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        currentLang = shared.getString("lang", "ar")!!
        token = shared.getString("userToken", "")!!

        back = root.imgFavBack
        search = root.imgFavSearch
        shop = root.btnShop
        products = root.rcFavProduct
        emptyFav = root.llEmptyFav

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
            case = arguments!!.getInt("case", 0)
        }

        getFav()

        back.setOnClickListener {
            backClick()
        }

        search.setOnClickListener {
            searchClick()
        }

        shop.setOnClickListener {
            shopClick()
        }

        return root
    }

    private fun fillProducts(data: ArrayList<Building>) {
        if (data.isEmpty()) {
            products.visibility = View.GONE
            emptyFav.visibility = View.VISIBLE
        } else {
            products.visibility = View.VISIBLE
            emptyFav.visibility = View.GONE
            val adapter = PropertyRecycler(activity!!, data)
            products.adapter = adapter
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                products.layoutManager =
                    LinearLayoutManager(activity!!)
            } else {
                products.layoutManager = GridLayoutManager(activity!!, 2)
            }
        }
    }

    private fun backClick() {
        val fragment = SettingsFragment()
        val bundle = Bundle()
        bundle.putInt("case", case)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flSettingsHolder, fragment).commit()
    }

    private fun searchClick() {
        val intent = Intent(activity!!, SearchActivity::class.java)
        intent.putExtra("open", 4)
        startActivity(intent)
    }

    private fun shopClick() {
        val intent = Intent(activity!!, InterActivity::class.java)
        intent.putExtra("open", 2)
        startActivity(intent)
    }

    private fun getFav() {
        loading.show()
        val service = ServiceBuilder.apis!!
        val call = service.getFav(currentLang, "Bearer $token")
        call.enqueue(object : Callback<FavoritResponse> {
            override fun onFailure(call: Call<FavoritResponse>, t: Throwable) {
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
                loading.dismiss()
            }

            override fun onResponse(
                call: Call<FavoritResponse>,
                response: Response<FavoritResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        val data = ArrayList<Building>()
                        data.addAll(body.data!!.data!!)
                        fillProducts(data)
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