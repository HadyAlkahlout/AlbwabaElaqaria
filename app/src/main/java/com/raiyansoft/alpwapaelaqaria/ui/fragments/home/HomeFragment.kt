package com.raiyansoft.alpwapaelaqaria.ui.fragments.home

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.ui.activity.search.SearchActivity
import com.raiyansoft.alpwapaelaqaria.adapter.BuildingRecycler
import com.raiyansoft.alpwapaelaqaria.adapter.SellerRecycler
import com.raiyansoft.alpwapaelaqaria.adapter.SliderAdapter
import com.raiyansoft.alpwapaelaqaria.ui.fragments.subcategory.PropertyFragment
import com.raiyansoft.alpwapaelaqaria.ui.fragments.subcategory.WorkerTypeFragment
import com.raiyansoft.alpwapaelaqaria.model.Building
import com.raiyansoft.alpwapaelaqaria.model.ProfileResponse
import com.raiyansoft.alpwapaelaqaria.model.Seller
import com.raiyansoft.alpwapaelaqaria.model.Slider
import com.raiyansoft.alpwapaelaqaria.ui.viewmodel.MainViewModel
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    private lateinit var topSlider: SliderView
    private lateinit var medSlider: SliderView
    private lateinit var botSlider: SliderView
    private lateinit var goodSeller: RecyclerView
    private lateinit var newSeller: RecyclerView
    private lateinit var goodBuilding: RecyclerView
    private lateinit var newBuilding: RecyclerView
    private lateinit var profileImg: RoundedImageView
    private lateinit var name: TextView
    private lateinit var searchText: EditText
    private lateinit var searchIcon: ImageView

    private lateinit var allGoodSeller: TextView
    private lateinit var allGoodBuilding: TextView
    private lateinit var allNewSeller: TextView
    private lateinit var allNewBuilding: TextView

    private lateinit var clGoodSeller: ConstraintLayout
    private lateinit var clGoodBuilding: ConstraintLayout
    private lateinit var clNewSeller: ConstraintLayout
    private lateinit var clNewBuilding: ConstraintLayout

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        topSlider = root.topImageSlider
        medSlider = root.mediamImageSlider
        botSlider = root.bottomImageSlider
        goodSeller = root.rcGoodSeller
        newSeller = root.rcNewSeller
        goodBuilding = root.rcGoodBuilding
        newBuilding = root.rcNewBuilding
        profileImg = root.imgProfile
        name = root.tvUserName
        searchText = root.edSearch
        searchIcon = root.imgSearch

        allGoodSeller = root.tvAllGoodSeller
        allGoodBuilding = root.tvAllGoodBuilding
        allNewSeller = root.tvAllNewSeller
        allNewBuilding = root.tvAllNweBuilding

        clGoodSeller = root.clGoodSeller
        clGoodBuilding = root.clGoodBuilding
        clNewSeller = root.clNewSeller
        clNewBuilding = root.clNewBuilding

        val anim = AnimationUtils.loadAnimation(activity, R.anim.down_move_anim)
        root.clHome.startAnimation(anim)

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

        val sharedPreferences = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("userToken", "")!!
        currentLang = sharedPreferences.getString("lang", "ar")!!

        viewModel.dataHomeLiveData.observe(viewLifecycleOwner, Observer { data ->

            fillTopSlider(data.data!!.upBanner!!)
            fillMedSlider(data.data!!.middleBanner!!)
            fillBottomSlider(data.data!!.downBanner!!)
            val goodSeller = ArrayList<Seller>()
            goodSeller.addAll(data.data!!.specialUsers!!)
            fillGoodSeller(goodSeller)
            val newSeller = ArrayList<Seller>()
            newSeller.addAll(data.data!!.newUsers!!)
            fillNewSeller(newSeller)
            fillGoodBuilding(data.data!!.specialProperties!!)
            fillNewBuilding(data.data!!.newProperties!!)
            fillProfile()


        })

        searchIcon.setOnClickListener {
            searchClick()
        }

        allGoodSeller.setOnClickListener {
            allGoodSeller()
        }

        allGoodBuilding.setOnClickListener {
            allGoodBuilding()
        }

        allNewSeller.setOnClickListener {
            allNewSeller()
        }

        allNewBuilding.setOnClickListener {
            allNewBuilding()
        }

        return root
    }

    private fun fillTopSlider(imgs: List<Slider>) {
        if (imgs.isEmpty()) {
            topSlider.visibility = View.GONE
        } else {
            topSlider.setSliderAdapter(SliderAdapter(activity!!, imgs))
            topSlider.startAutoCycle()
            topSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
            topSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        }
    }

    private fun fillMedSlider(imgs: List<Slider>) {
        if (imgs.isEmpty()) {
            medSlider.visibility = View.GONE
        } else {
            medSlider.setSliderAdapter(SliderAdapter(activity!!, imgs))
            medSlider.startAutoCycle()
            medSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
            medSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        }
    }

    private fun fillBottomSlider(imgs: List<Slider>) {
        if (imgs.isEmpty()) {
            botSlider.visibility = View.GONE
        } else {
            botSlider.setSliderAdapter(SliderAdapter(activity!!, imgs))
            botSlider.startAutoCycle()
            botSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
            botSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        }
    }

    private fun fillGoodSeller(data: ArrayList<Seller>) {
        if (data.isEmpty()) {
            clGoodSeller.visibility = View.GONE
            goodSeller.visibility = View.GONE
        } else {
            val adapter = SellerRecycler(activity!!, data)
            goodSeller.adapter = adapter
            goodSeller.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun fillNewSeller(data: ArrayList<Seller>) {
        if (data.isEmpty()) {
            clNewSeller.visibility = View.GONE
            newSeller.visibility = View.GONE
        } else {
            val adapter = SellerRecycler(activity!!, data)
            newSeller.adapter = adapter
            newSeller.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun fillGoodBuilding(data: List<Building>) {
        if (data.isEmpty()) {
            clGoodBuilding.visibility = View.GONE
            goodBuilding.visibility = View.GONE
        } else {
            val adapter = BuildingRecycler(activity!!, data)
            goodBuilding.adapter = adapter
            goodBuilding.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun fillNewBuilding(data: List<Building>) {
        if (data.isEmpty()) {
            clNewBuilding.visibility = View.GONE
            newBuilding.visibility = View.GONE
        } else {
            val adapter = BuildingRecycler(activity!!, data)
            newBuilding.adapter = adapter
            newBuilding.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun searchClick() {
        //Search activity
        if (searchText.text.isEmpty()) {
            title.text = getString(R.string.attention)
            details.text = getString(R.string.empty_fields)
            dialog.show()
        } else {
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra("open", 0)
            intent.putExtra("search", searchText.text.toString())
            activity!!.startActivity(intent)
        }
    }

    private fun allGoodSeller() {
        val fragment = WorkerTypeFragment()
        val bundle = Bundle()
        bundle.putInt("type", 1)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flInterHolder, fragment)
            .commit()
    }

    private fun allNewSeller() {
        val fragment = WorkerTypeFragment()
        val bundle = Bundle()
        bundle.putInt("type", 0)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flInterHolder, fragment)
            .commit()
    }

    private fun allGoodBuilding() {
        val fragment = PropertyFragment()
        val bundle = Bundle()
        bundle.putInt("type", 1)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flInterHolder, fragment)
            .commit()
    }

    private fun allNewBuilding() {
        val fragment = PropertyFragment()
        val bundle = Bundle()
        bundle.putInt("type", 0)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flInterHolder, fragment)
            .commit()
    }

    private fun fillProfile() {
        val service = ServiceBuilder.apis!!
        val call2 = service.getProfile(currentLang, "Bearer $token")
        call2.enqueue(object : Callback<ProfileResponse> {
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
                loading.dismiss()
            }

            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code == 200) {
                        if (activity != null){
                            Glide
                                .with(activity!!)
                                .load(body.data!!.info!!.image)
                                .centerCrop()
                                .placeholder(R.drawable.profile)
                                .into(profileImg)
                        }
                        name.text = body.data!!.info!!.name
                    } else {
                        title.text = getString(R.string.attention)
                        details.text = getString(R.string.somthing_wrong)
                        dialog.show()
                        loading.cancel()
                    }
                }
                loading.dismiss()
            }

        })
    }

}