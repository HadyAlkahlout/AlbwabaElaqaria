package com.raiyansoft.alpwapaelaqaria.ui.fragments.notification

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.switchmaterial.SwitchMaterial
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.model.FavResponse
import com.raiyansoft.alpwapaelaqaria.model.SetResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_ntf_settings.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NTFSettingsFragment : Fragment() {

    private lateinit var back: ImageView
    private lateinit var pushNotification: SwitchMaterial

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
        val root = inflater.inflate(R.layout.fragment_ntf_settings, container, false)

        val shared = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        val send_notification = shared.getBoolean("send_notification", false)
        val deviseToken = shared.getString("deviceToken", "")!!
        val currentLang = shared.getString("lang", "ar")!!
        val token = shared.getString("userToken", "ar")!!

        back = root.imgNFCBack
        pushNotification = root.smPushNotification

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


        pushNotification.isChecked = send_notification

        back.setOnClickListener {
            backClick()
        }
        val service = ServiceBuilder.apis!!
        pushNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            loading.show()
            if (isChecked){
                val call = service.getSet(currentLang, "Bearer $token", deviseToken)
                call.enqueue(object : Callback<SetResponse> {
                    override fun onFailure(call: Call<SetResponse>, t: Throwable) {
                        title.text = getString(R.string.attention)
                        details.text = getString(R.string.somthing_wrong)
                        dialog.show()
                        loading.dismiss()
                    }

                    override fun onResponse(
                        call: Call<SetResponse>,
                        response: Response<SetResponse>
                    ) {
                        if (response.body() != null) {
                            val body = response.body()
                            if (body!!.status!! && body.code!! == 200) {
                                loading.dismiss()
                                Toast.makeText(activity!!, resources.getText(R.string.active), Toast.LENGTH_SHORT).show()
                            } else {
                                title.text = getString(R.string.attention)
                                details.text = getString(R.string.somthing_wrong)
                                dialog.show()
                                loading.dismiss()
                            }
                        }
                    }
                })
            }else{
                loading.show()
                val call = service.logout(currentLang, "Bearer $token")
                call.enqueue(object : Callback<FavResponse> {
                    override fun onResponse(call: Call<FavResponse>, response: Response<FavResponse>) {
                        if (response.body() != null) {
                            val body = response.body()
                            if (body!!.status!! && body.code!! == 200) {
                                loading.dismiss()
                                Toast.makeText(activity!!, resources.getText(R.string.diactive), Toast.LENGTH_SHORT).show()
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


        return root
    }

    private fun backClick(){
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flSettingsHolder, NotificationFragment()).commit()
    }
}