package com.raiyansoft.alpwapaelaqaria.ui.fragments.settings

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.model.CallUs
import com.raiyansoft.alpwapaelaqaria.model.FavResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_call_us.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallUsFragment : Fragment() {

    private lateinit var back: ImageView
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var message: EditText
    private lateinit var send: Button

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
        val root = inflater.inflate(R.layout.fragment_call_us, container, false)

        val shared = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        currentLang = shared.getString("lang", "ar")!!
        token = shared.getString("userToken", "")!!

        back = root.imgCallUsBack
        name = root.edCallUsFullName
        email = root.edCallUsEmail
        phone = root.edCallUsPhone
        message = root.edCallUsMessage
        send = root.btnSendRequest

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

        back.setOnClickListener {
            backClick()
        }

        send.setOnClickListener {
            sendClick()
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

    private fun sendClick(){
        if (name.text.isEmpty() || phone.text.isEmpty() || email.text.isEmpty() || message.text.isEmpty()){
            title.text = getString(R.string.attention)
            details.text = getString(R.string.empty_fields)
            dialog.show()
            loading.dismiss()
        }else{
            loading.show()
            val callInfo = CallUs(name.text.toString(), phone.text.toString(), email.text.toString(), message.text.toString())
            val service = ServiceBuilder.apis!!
            val call = service.callUs(currentLang, "Bearer $token", callInfo)
            call.enqueue(object : Callback<FavResponse> {
                override fun onResponse(call: Call<FavResponse>, response: Response<FavResponse>) {
                    if (response.body() != null) {
                        val body = response.body()
                        if (body!!.status!! && body.code!! == 200) {
                            Toast.makeText(activity!!, getString(R.string.call_success), Toast.LENGTH_SHORT).show()
                            loading.dismiss()
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
}