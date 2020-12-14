package com.raiyansoft.alpwapaelaqaria.ui.fragments.item

import android.app.Dialog
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.raiyansoft.alpwapaelaqaria.BuildConfig
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.adapter.PropertySliderAdapter
import com.raiyansoft.alpwapaelaqaria.model.Donation
import com.raiyansoft.alpwapaelaqaria.model.FavResponse
import com.raiyansoft.alpwapaelaqaria.model.Image
import com.raiyansoft.alpwapaelaqaria.model.PropertyDetailsResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_property_show.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class PropertyShowFragment : Fragment() {

    private lateinit var back: ImageView
    private lateinit var favorate: ImageView
    private lateinit var share: ImageView
    private lateinit var map: CardView
    private lateinit var slider: SliderView
    private lateinit var propertyTitle: TextView
    private lateinit var date: TextView
    private lateinit var price: TextView
    private lateinit var description: TextView
    private lateinit var feature: TextView
    private lateinit var location: TextView
    private lateinit var promote: Button
    private lateinit var call: Button
    private lateinit var whatsUp: Button
    private lateinit var promoteProp: LinearLayout

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog

    private var propertyId: Int = 0
    private var propertyTitles: String = ""
    private var propertyNote: String = ""
    private var fav = 0
    private var whatsNum = ""
    private var phoneNum = ""
    private var lat = "0.0"
    private var long = "0.0"
    private var promoteOpen = ""
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_property_show, container, false)

        val shared = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        currentLang = shared.getString("lang", "ar")!!
        token = shared.getString("userToken", "")!!
        promoteOpen = shared.getString("promote", "no")!!
        userId = shared.getString("userID", "no")!!

        back = root.imgPropertBack
        favorate = root.imgFav
        share = root.imgShare
        map = root.cvMapLocation
        slider = root.propertySlider
        propertyTitle = root.tvPropertyTitle
        date = root.tvPropertyOfferDate
        price = root.tvPropertyPrice
        description = root.tvPropertyDescription
        feature = root.tvPropertyFeature
        location = root.tvLocation
        promote = root.btnPropertyPromote
        call = root.btnPropertyCall
        whatsUp = root.btnWhatsUp
        promoteProp = root.llPromoteProp

        if (currentLang != "ar") {
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
            propertyId = arguments!!.getInt("id", 0)
        }

        back.setOnClickListener {
            backClick()
        }

        favorate.setOnClickListener {
            favorateClick()
        }

        share.setOnClickListener {
            shareClick()
        }

        map.setOnClickListener {
            mapClick()
        }

        promote.setOnClickListener {
            promoteClick()
        }

        call.setOnClickListener {
            callClick()
        }

        whatsUp.setOnClickListener {
            whatsUpClick()
        }

        fillPropertyData()

        return root
    }

    private fun backClick() {
        activity!!.onBackPressed()
    }

    private fun favorateClick() {
        loading.show()
        val service = ServiceBuilder.apis!!
        var call = service.addToFav(currentLang, "Bearer $token", propertyId)
        if (fav == 1) {
            call = service.removeFromFav(currentLang, "Bearer $token", propertyId)
        }
        call.enqueue(object : Callback<FavResponse> {
            override fun onFailure(call: Call<FavResponse>, t: Throwable) {
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
                loading.dismiss()
            }

            override fun onResponse(call: Call<FavResponse>, response: Response<FavResponse>) {
                if (response.body() != null) {
                    if (response.body()!!.status!! && response.body()!!.code!! == 200) {
                        if (fav == 1) {
                            fav = 0
                            Toast.makeText(
                                activity!!,
                                getString(R.string.remove_fav),
                                Toast.LENGTH_SHORT
                            ).show()
                            favorate.setImageResource(R.drawable.ic_fav_off)
                        } else {
                            fav = 1
                            Toast.makeText(
                                activity!!,
                                getString(R.string.add_fav),
                                Toast.LENGTH_SHORT
                            ).show()
                            favorate.setImageResource(R.drawable.ic_fav)
                        }
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

    private fun shareClick() {

        val dynamicLink = Firebase.dynamicLinks.shortLinkAsync { // or Firebase.dynamicLinks.shortLinkAsync
            link = Uri.parse("https://www.albawabaa.page.link/Aqar?aqarId=$propertyId")
            domainUriPrefix = "https://albawabaa.page.link"
            androidParameters("com.raiyansoft.alpwapaelaqaria"){}
            iosParameters("com.Raiyansoft.Aaqar") {
                appStoreId = "1529625100"
            }
            socialMetaTagParameters {
                title = propertyTitles
                description = propertyNote
            }
        }.addOnSuccessListener {
            Log.e("hdhd", "shareClick: ${it.shortLink}" )
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "البوابة العقارية")
            val shareMessage = "\nLet me recommend you this Property\n\n${it.shortLink}"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        }
    }

    private fun mapClick() {
        val gmmIntentUri = Uri.parse("geo:$lat,$long")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun promoteClick() {
        loading.show()
        val donation = Donation(propertyId)
        val service =  ServiceBuilder.apis!!
        val call = service.promoteProperty(currentLang, "Bearer $token", donation)
        call.enqueue(object : Callback<FavResponse> {
            override fun onResponse(call: Call<FavResponse>, response: Response<FavResponse>) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        loading.dismiss()
                        Toast.makeText(activity!!, "Promote Done Successfully", Toast.LENGTH_SHORT)
                            .show()
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

    private fun callClick() {
        val i = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${phoneNum}")
        }
        if (i.resolveActivity(activity!!.packageManager) != null) {
            startActivity(i)
        }
    }

    private fun whatsUpClick() {
        val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp")
        if (isWhatsappInstalled) {

            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
            sendIntent.putExtra(
                "jid",
                PhoneNumberUtils.stripSeparators(whatsNum) + "@s.whatsapp.net"
            )

            startActivity(sendIntent)
        } else {
            val uri = Uri.parse("market://details?id=com.whatsapp")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            Toast.makeText(
                activity!!, "WhatsApp not Installed",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(goToMarket)
        }
    }

    private fun whatsappInstalledOrNot(uri: String): Boolean {
        val pm = activity!!.packageManager
        var app_installed = false
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            app_installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            app_installed = false
        }
        return app_installed
    }

    private fun fillSlider(imgs: List<Image>) {
        slider.setSliderAdapter(PropertySliderAdapter(activity!!, imgs))
        slider.startAutoCycle()
        slider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        slider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
    }

    private fun fillPropertyData() {
        loading.show()
        val service = ServiceBuilder.apis!!
        val call = service.getPropertyDetails(currentLang, "Bearer $token", propertyId)
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
                        propertyTitles = body.data!!.title!!
                        propertyNote = body.data!!.note!!
                        if (body.data!!.fav!!) {
                            favorate.setImageResource(R.drawable.ic_fav)
                            fav = 1
                        } else {
                            favorate.setImageResource(R.drawable.ic_fav_off)
                            fav = 0
                        }

                        if (body.data!!.images!!.isNotEmpty()) {
                            fillSlider(body.data!!.images!!)
                        }

                        if (body.data!!.categoryTitle.toString() != "null" && body.data!!.categoryTitle.toString() != "") {
                            propertyTitle.text = body.data!!.categoryTitle
                        }

                        if (body.data!!.date.toString() != "null" && body.data!!.date.toString() != "") {
                            date.text = body.data!!.date
                        }

                        if (body.data!!.price.toString() != "null" && body.data!!.price.toString() != "") {
                            price.text = body.data!!.price
                        }

                        if (body.data!!.note.toString() != "null" && body.data!!.note.toString() != "") {
                        description.text = body.data!!.note
                        }

                        var allText = ""

                        if (body.data!!.advertise_type.toString() != "null" && body.data!!.advertise_type.toString() != "") {
                            var propType = getString(R.string.why_to_pay) + " : "
                            if (body.data!!.advertise_type == 1) {
                                propType += getString(R.string.for_sell)
                            } else {
                                propType += getString(R.string.for_rent)
                            }
                            allText += "$propType\n"
                        }

                        if (body.data!!.type.toString() != "null" && body.data!!.type.toString() != "") {
                            var propType = getString(R.string.type) + " : " + body.data!!.type!!
                            allText += "$propType\n"
                        }

                        if (body.data!!.size.toString() != "null" && body.data!!.size.toString() != "") {
                            var propSize = getString(R.string.area) + " : " + body.data!!.size!! + " " + getString(R.string.cm2)
                            allText += "$propSize\n"
                        }

                        if (body.data!!.rooms.toString() != "null" && body.data!!.rooms.toString() != "0" && body.data!!.rooms.toString() != "") {
                            var rooms =
                                getString(R.string.room_num) + " : " + body.data!!.rooms.toString()
                            allText += "$rooms\n"
                        }

                        if (body.data!!.bathes.toString() != "null" && body.data!!.bathes.toString() != "0" && body.data!!.bathes.toString() != "") {
                            var bath =
                                getString(R.string.bath_num) + " : " + body.data!!.bathes.toString()
                            allText += "$bath\n"
                        }

                        if (body.data!!.floor.toString() != "null" && body.data!!.floor.toString() != "0" && body.data!!.floor.toString() != "") {
                            var floor =
                                getString(R.string.flor) + " : " + body.data!!.floor.toString()
                            allText += "$floor\n"
                        }

                        if (body.data!!.floorNumbers.toString() != "null" && body.data!!.floorNumbers.toString() != "0" && body.data!!.floorNumbers.toString() != "") {
                            var floorNum =
                                getString(R.string.flor_num) + " : " + body.data!!.floorNumbers.toString()
                            allText += "$floorNum\n"
                        }

                        if (body.data!!.old.toString() != "null" && body.data!!.old.toString() != "0" && body.data!!.old.toString() != "") {
                            var age =
                                getString(R.string.old) + " : " + body.data!!.old.toString()
                            allText += "$age\n"
                        }

                        if (body.data!!.payment.toString() != "null" && body.data!!.payment.toString() != "0" && body.data!!.payment.toString() != "") {
                            var pay = getString(R.string.pay_way) + " : "
                            if (body.data!!.payment == 1) {
                                pay += getString(R.string.cash)
                            } else {
                                pay += getString(R.string.tacset)
                            }
                            allText += "$pay\n"
                        }

                        if (body.data!!.furniture.toString() != "null" && body.data!!.furniture.toString() != "0" && body.data!!.furniture.toString() != "") {
                            var furniture = getString(R.string.furnitur) + " : "
                            if (body.data!!.payment == 1) {
                                furniture += getString(R.string.furin)
                            } else {
                                furniture += getString(R.string.unfurn)
                            }
                            allText += "$furniture\n"
                        }

                        if (body.data!!.features!!.isNotEmpty()) {
                            var addtinalFeature = getString(R.string.additional_feature) + " : "
                            var index = 0
                            for (i in body.data!!.features!!) {
                                if (index == body.data!!.features!!.size - 1) {
                                    addtinalFeature += i.title
                                } else {
                                    addtinalFeature += i.title + " , "
                                }
                                index++
                            }
                            allText += "$addtinalFeature\n"
                        }

                        this@PropertyShowFragment.feature.text =
                            allText

                        location.text = "" + body.data!!.city + " , " + body.data!!.region
                        whatsNum = body.data!!.whats!!
                        phoneNum = body.data!!.phone!!
                        lat = body.data!!.lat!!
                        long = body.data!!.lng!!
                        if (userId == body.data!!.userId!!.toString()) {
                            if (promoteOpen == "no") {
                                promoteProp.visibility = View.GONE
                            } else {
                                promoteProp.visibility = View.VISIBLE
                            }
                        }
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