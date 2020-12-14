package com.raiyansoft.alpwapaelaqaria.ui.fragments.settings

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
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.model.PrivacyResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_privacy.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrivacyFragment : Fragment() {

    private lateinit var back: ImageView
    private lateinit var privacy: TextView

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
        val root = inflater.inflate(R.layout.fragment_privacy, container, false)

        val shared = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        currentLang = shared.getString("lang", "ar")!!
        token = shared.getString("userToken", "")!!

        back = root.imgPrivacyBack
        privacy = root.tvPrivacy

        if (currentLang != "ar"){
            back.setImageResource(R.drawable.ic_back)
        }

        if (arguments != null) {
            case = arguments!!.getInt("case", 0)
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

        getPrivacy()

        back.setOnClickListener {
            backClick()
        }

        return root
    }

    private fun backClick(){
        val fragment = SettingsFragment()
        val bundle = Bundle()
        bundle.putInt("case", case)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flSettingsHolder, fragment).commit()
    }

    private fun getPrivacy(){
        loading.show()
        val service = ServiceBuilder.apis!!
        val call = service.getPrivacy(currentLang, "Bearer $token")
        call.enqueue(object : Callback<PrivacyResponse> {
            override fun onResponse(call: Call<PrivacyResponse>, response: Response<PrivacyResponse>) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        if (body.data!!.privacy!! != "" && body.data!!.privacy != null ) {
                            privacy.visibility = View.VISIBLE
                            privacy.text = body.data!!.privacy!!
                        }else{
                            privacy.visibility = View.GONE
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

            override fun onFailure(call: Call<PrivacyResponse>, t: Throwable) {
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
                loading.dismiss()
            }
        })
    }
}