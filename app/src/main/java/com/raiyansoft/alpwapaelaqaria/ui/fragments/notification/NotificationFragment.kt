package com.raiyansoft.alpwapaelaqaria.ui.fragments.notification

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.InterActivity
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.adapter.NotificationRecycler
import com.raiyansoft.alpwapaelaqaria.model.Notification
import com.raiyansoft.alpwapaelaqaria.model.NotificationResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_notification.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationFragment : Fragment() {

    private lateinit var back: ImageView
    private lateinit var settings: ImageView
    private lateinit var notification: RecyclerView

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
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_notification, container, false)

        val shared = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        currentLang = shared.getString("lang", "ar")!!
        token = shared.getString("userToken", "")!!

        back = root.imgNotificationBack
        settings = root.imgNotificationSettings
        notification = root.rcNotifications

        if (currentLang != "ar"){
            back.setImageResource(R.drawable.ic_back)
        }

        loading = ProgressDialog(activity!!)
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

        getNotification()

        back.setOnClickListener {
            backClick()
        }

        settings.setOnClickListener {
            settingsClick()
        }

        return root
    }

    private fun fillNotifications(data: List<Notification>) {
        if(data.size > 0) {
            val adapter = NotificationRecycler(activity!!, data)
            notification.adapter = adapter
            notification.layoutManager = LinearLayoutManager(activity!!)
            notification.visibility = View.VISIBLE
        }else{
            notification.visibility = View.GONE
        }
    }

    private fun backClick() {
        val intent = Intent(activity!!, InterActivity::class.java)
        intent.putExtra("open", 4)
        startActivity(intent)
        activity!!.finish()
    }

    private fun settingsClick() {
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flSettingsHolder, NTFSettingsFragment()).commit()
    }

    private fun getNotification() {
        val service =  ServiceBuilder.apis!!
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
                        val ntv = ArrayList<Notification>()
                        for (i in body.data!!.data!!) {
                            ntv.add(i)
                        }
                        if (body.data!!.pages!! > 1) {
                            var int = 2
                            while (int <= body.data!!.pages!!) {
                                val calls = service.getNotification(currentLang, "Bearer $token")
                                calls.enqueue(object : Callback<NotificationResponse> {
                                    override fun onFailure(
                                        call: Call<NotificationResponse>,
                                        t: Throwable
                                    ) {
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
                                            val bodys = response.body()
                                            if (bodys!!.status!! && bodys.code!! == 200) {
                                                for (i in bodys.data!!.data!!) {
                                                    ntv.add(i)
                                                }
                                            } else {
                                                title.text = getString(R.string.attention)
                                                details.text = getString(R.string.somthing_wrong)
                                                dialog.show()
                                                loading.dismiss()
                                            }
                                        }
                                    }
                                })
                                int++
                            }
                        }
                        fillNotifications(ntv)
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