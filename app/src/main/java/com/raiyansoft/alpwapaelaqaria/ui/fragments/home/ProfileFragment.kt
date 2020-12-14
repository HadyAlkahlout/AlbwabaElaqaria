package com.raiyansoft.alpwapaelaqaria.ui.fragments.home

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raiyansoft.alpwapaelaqaria.*
import com.raiyansoft.alpwapaelaqaria.adapter.ProfilePropertyRecycler
import com.raiyansoft.alpwapaelaqaria.adapter.PropertyRecycler
import com.raiyansoft.alpwapaelaqaria.model.Building
import com.raiyansoft.alpwapaelaqaria.model.FavResponse
import com.raiyansoft.alpwapaelaqaria.model.NotificationResponse
import com.raiyansoft.alpwapaelaqaria.ui.viewmodel.MainViewModel
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import com.raiyansoft.alpwapaelaqaria.ui.activity.function.AddPropertyActivity
import com.raiyansoft.alpwapaelaqaria.ui.activity.function.EditeProfileActivity
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.InterActivity
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.SettingsActivity
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.promote_dialoge.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment(), ProfilePropertyRecycler.PropertyClick {

    private lateinit var setting: ImageView
    private lateinit var profile: ImageView
    private lateinit var views: TextView
    private lateinit var name: TextView
    private lateinit var joinDate: TextView
    private lateinit var description: TextView
    private lateinit var head: TextView
    private lateinit var edit: Button
    private lateinit var addProperty: Button
    private lateinit var promote: LinearLayout
    private lateinit var favorate: RecyclerView
    private lateinit var layoutView: LinearLayout
    private lateinit var ntvIcon: ImageView
    private lateinit var ntvNum: TextView

    private var case = 0

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog

    private lateinit var promoteDialog: Dialog
    private lateinit var successDialog: Dialog
    private lateinit var monthPromote: LinearLayout
    private lateinit var monthPromotePrice: TextView
    private lateinit var weekPromote: LinearLayout
    private lateinit var weekPromotePrice: TextView
    private lateinit var dayPromote: LinearLayout
    private lateinit var dayPromotePrice: TextView

    private lateinit var sharedPreferences: SharedPreferences
    var type = 0

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        setting = root.imgProfileSettings
        profile = root.imgProfileImage
        views = root.tvProfileViewsNum
        name = root.tvProfileName
        joinDate = root.tvProfileJoinDate
        description = root.tvProfileDescription
        edit = root.btnEdit
        addProperty = root.btnAddProperty
        promote = root.llPromote
        favorate = root.rvMyInfoFav
        head = root.tvProfileHead
        layoutView = root.llView
        ntvIcon = root.imgProfileNotification
        ntvNum = root.tvNtvNum

        sharedPreferences = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("userToken", "")!!
        currentLang = sharedPreferences.getString("lang", "ar")!!
        val promoteOpen = sharedPreferences.getString("promote", "no")!!

        if (promoteOpen == "no") {
            promote.visibility = View.GONE
        }

        addProperty.visibility = View.GONE
        promote.visibility = View.GONE
        description.visibility = View.GONE
        layoutView.visibility = View.GONE

        loading = ProgressDialog(activity)
        loading.setCancelable(false)
        loading.setMessage(getString(R.string.loading_data))

        dialog = Dialog(activity!!)
        dialog.setContentView(R.layout.custom_dialog)
        title = dialog.tvDialogTitle
        details = dialog.tvDialogText
        ok = dialog.btnOk
        dialog.setCancelable(false)
        ok.setOnClickListener {
            dialog.cancel()
        }

        promoteDialog = Dialog(activity!!)
        promoteDialog.setContentView(R.layout.promote_dialoge)
        promoteDialog.setCancelable(true)
        monthPromote = promoteDialog.llMonthPromote
        monthPromotePrice = promoteDialog.tvMonthPromotePrice
        weekPromote = promoteDialog.llWeekPromote
        weekPromotePrice = promoteDialog.tvWeekPromotePrice
        dayPromote = promoteDialog.llDayPromote
        dayPromotePrice = promoteDialog.tvDayPromotePrice
        successDialog = Dialog(activity!!)
        successDialog.setContentView(R.layout.promote_done_dialog)
        successDialog.setCancelable(true)

        monthPromote.setOnClickListener {
            promoteOptionClick()
        }
        weekPromote.setOnClickListener {
            promoteOptionClick()
        }
        dayPromote.setOnClickListener {
            promoteOptionClick()
        }

        setting.setOnClickListener {
            settingClick()
        }

        edit.setOnClickListener {
            editClick()
        }

        addProperty.setOnClickListener {
            addClick()
        }

        promote.setOnClickListener {
            promoteClick()
        }

        ntvIcon.setOnClickListener {
            ntvClick()
        }

        viewModel.profileLiveData.observe(viewLifecycleOwner, Observer {

            sharedPreferences.edit()
                .putBoolean("send_notification", it.data!!.info!!.send_notification!!)
                .apply()
            Glide
                .with(activity!!)
                .load(it.data!!.info!!.image)
                .centerCrop()
                .placeholder(R.drawable.profile)
                .into(profile)
            name.text = it.data!!.info!!.name.toString()
            joinDate.text = it.data!!.info!!.date.toString()
            if (it.data!!.info!!.broker!!) {
                case = 1
                addProperty.visibility = View.VISIBLE
                addProperty.visibility = View.VISIBLE
                description.visibility = View.VISIBLE
                layoutView.visibility = View.VISIBLE
                if (it.data!!.info!!.note.toString() == "null") {
                    description.visibility = View.GONE
                } else {
                    description.text = it.data!!.info!!.note.toString()
                }
                views.text = it.data!!.info!!.views.toString()
                head.text = getString(R.string.my_properties)
                fillProp(it.data!!.properties!!)
                type = 1
            } else {
                promote.visibility = View.GONE
                addProperty.visibility = View.GONE
                description.visibility = View.GONE
                layoutView.visibility = View.GONE
                val param = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
                )
                edit.layoutParams = param
                head.text = getString(R.string.favorite)
            }
            if (type == 1) {
                getNotification()
            } else {
                viewModel.favLiveData.observe(viewLifecycleOwner, Observer {
                    adapterProperty.data.addAll(it.data!!.data!!)

                    fillFav(adapterProperty.data)
                })
            }


        })

        return root
    }

    val adapterProperty by lazy {
        PropertyRecycler(requireActivity(), java.util.ArrayList())
    }

    private fun ntvClick() {
        val intent = Intent(activity, SettingsActivity::class.java)
        intent.putExtra("open", 1)
        startActivity(intent)
    }


    private fun fillFav(data: ArrayList<Building>) {
        favorate.visibility = View.VISIBLE
        head.visibility = View.VISIBLE
        favorate.adapter = adapterProperty
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            favorate.layoutManager =
                LinearLayoutManager(activity)
        } else {
            favorate.layoutManager = GridLayoutManager(activity, 2)
        }
    }

    private fun getNotification() {
        val service = ServiceBuilder.apis!!
        val call = service.getNotification(currentLang, "Bearer $token")
        call.enqueue(object : Callback<NotificationResponse> {
            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
                loading.dismiss()
            }

            override fun onResponse(
                call: Call<NotificationResponse>,
                response: Response<NotificationResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        if (body.data!!.un_read!! > 0) {
                            ntvNum.text = body.data!!.un_read.toString()
                            ntvNum.visibility = View.VISIBLE
                        } else {
                            ntvNum.visibility = View.GONE
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

    private fun settingClick() {
        val intent = Intent(activity, SettingsActivity::class.java)
        intent.putExtra("case", case)
        intent.putExtra("open", 0)
        startActivity(intent)
    }

    private fun editClick() {
        val intent = Intent(activity, EditeProfileActivity::class.java)
        activity!!.startActivity(intent)
    }

    private fun addClick() {
        val intent = Intent(activity, AddPropertyActivity::class.java)
        startActivity(intent)
    }

    private fun promoteClick() {
        promoteDialog.show()
    }

    private fun promoteOptionClick() {
        loading.show()
        loading.dismiss()
        successDialog.show()
    }

    private fun fillProp(data: List<Building>) {
        if (data.isEmpty()) {
            favorate.visibility = View.GONE
            head.visibility = View.GONE
        } else {
            val adapter = ProfilePropertyRecycler(activity!!, data, this)
            favorate.adapter = adapter
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                favorate.layoutManager =
                    LinearLayoutManager(activity)
            } else {
                favorate.layoutManager = GridLayoutManager(activity, 2)
            }
        }
    }

    override fun deleteClick(id: Int) {
        loading.show()
        val service = ServiceBuilder.apis!!
        val call = service.deleteProperty(id, currentLang, "Bearer $token")
        call.enqueue(object : Callback<FavResponse> {
            override fun onFailure(call: Call<FavResponse>, t: Throwable) {
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
                loading.dismiss()
            }

            override fun onResponse(
                call: Call<FavResponse>,
                response: Response<FavResponse>
            ) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        loading.dismiss()
                        Toast.makeText(
                            activity,
                            getString(R.string.delete_done),
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(activity, InterActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
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

    override fun editClick(id: Int) {
        val intent = Intent(activity, AddPropertyActivity::class.java)
        intent.putExtra("case", 1)
        intent.putExtra("id", id)
        startActivity(intent)
    }
}