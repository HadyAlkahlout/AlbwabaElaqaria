package com.raiyansoft.alpwapaelaqaria.ui.fragments.login

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.hbb20.CountryCodePicker
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.model.RegisterResponse
import com.raiyansoft.alpwapaelaqaria.model.User
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import retrofit2.Call
import retrofit2.Callback

class LoginFragment : Fragment() {

    private var currentLang = ""

    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var inter: CircularProgressButton
    private lateinit var countryCode: CountryCodePicker

    private lateinit var nameCheck: ImageView
    private lateinit var emailCheck: ImageView
    private lateinit var phoneCheck: ImageView

    private lateinit var numbers: String

    private var check = false

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private var deviceToken = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_login, container, false)

        val down = AnimationUtils.loadAnimation(activity, R.anim.down_move_anim)
        val up = AnimationUtils.loadAnimation(activity, R.anim.top_move_anim)
        val left = AnimationUtils.loadAnimation(activity, R.anim.lefttoright)

        val sharedPreferences = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        currentLang = sharedPreferences.getString("lang", "ar")!!

        root.llLogo.startAnimation(down)
        root.llInfo.startAnimation(up)
        root.tvInfo.startAnimation(left)

        username = root.edLoginUsername
        email = root.edLoginEmail
        phone = root.edLoginPhone
        inter = root.btnInter
        nameCheck = root.imgNameCheck
        emailCheck = root.imgEmailCheck
        phoneCheck = root.imgPhoneCheck
        countryCode = root.cpCountry

        dialog = Dialog(activity!!)
        dialog.setContentView(R.layout.custom_dialog)
        title = dialog.tvDialogTitle
        details = dialog.tvDialogText
        ok = dialog.btnOk
        dialog.setCancelable(false)
        ok.setOnClickListener {
            dialog.cancel()
        }

        getFCMToken(activity!!)

        inter.setOnClickListener {
            inter.startAnimation {
                interClick()
            }
        }

        return root
    }

    private fun interClick() {
        val username = username.text.toString()
        val email = email.text.toString()
        val phone = phone.text.toString()

        if (username == "" || phone == "" || email == "") {
            title.text = getString(R.string.login_faild)
            details.text = getString(R.string.empty_fields)
            dialog.show()
            inter.revertAnimation() {
                inter.text = getString(R.string.try_again)
            }
        } else {
            nameCheck.setColorFilter(Color.argb(255, 0, 255, 0))
            if (phoneCheck(phone)) {
                phoneCheck.setColorFilter(Color.argb(255, 0, 255, 0))
                if (emailCheck(email)){
                    emailCheck.setColorFilter(Color.argb(255, 0, 255, 0))
                    sendData()
                }else{
                    title.text = getString(R.string.login_faild)
                    details.text = getString(R.string.wrong_email)
                    dialog.show()
                    inter.revertAnimation() {
                        inter.text = getString(R.string.try_again)
                    }
                }
            } else {
                title.text = getString(R.string.login_faild)
                details.text = getString(R.string.wrong_phone)
                dialog.show()
                inter.revertAnimation() {
                    inter.text = getString(R.string.try_again)
                }
            }
        }
    }

    private fun phoneCheck(phone: String): Boolean {
        val code = countryCode.selectedCountryNameCode
        val phoneValidator = PhoneNumberUtil.createInstance(activity)
        val number = phoneValidator.parse(phone, code)
        numbers = "00" + countryCode.selectedCountryCode + "" + phone
        return phoneValidator.isValidNumber(number)
    }

    private fun emailCheck(email: String): Boolean {
        if (email.contains("@") && email.contains(".")){
            return true
        }
        return false
    }

    private fun sendData() {
        val service =  ServiceBuilder.apis!!
        val user = User(
            username.text.toString(),
            numbers,
            email.text.toString(),
            "android",
            deviceToken,
            countryCode.selectedCountryCode
        )
        val call =
            service.addUser(currentLang, user)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                title.text = getString(R.string.login_faild)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
                inter.revertAnimation() {
                    inter.text = getString(R.string.try_again)
                }
            }

            override fun onResponse(
                call: Call<RegisterResponse>,
                response: retrofit2.Response<RegisterResponse>
            ) {
                if (response.body() == null) {
                    title.text = getString(R.string.login_faild)
                    details.text = getString(R.string.somthing_wrong)
                    dialog.show()
                    inter.revertAnimation() {
                        inter.text = getString(R.string.try_again)
                    }
                } else {
                    val res = response.body()
                    if (res!!.status!! && res.code!! == 200) {
                        val shared = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
                        val editor = shared.edit()
                        editor.putString("userID", res.data!!.userId.toString())
                        editor.putString("userToken", res.data!!.token.toString())
                        editor.apply()
                        check = true
                        if (check) {
                            inter.revertAnimation()
                            activity!!.supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentStartContainer, VarficationFragment())
                                .commit()
                        }
                    } else {
                        title.text = getString(R.string.login_faild)
                        details.text = getString(R.string.somthing_wrong)
                        dialog.show()
                        inter.revertAnimation() {
                            inter.text = getString(R.string.try_again)
                        }
                    }
                }
            }

        })
    }

    private fun getFCMToken(context: Context) {
        FirebaseMessaging.getInstance().subscribeToTopic("allUsers").addOnSuccessListener {
            Log.e("hdhd", "getFCMToken: subscribing the topic done successfully")
        }
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                deviceToken = task.result?.token!!
                activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE).edit()
                    .putString("deviceToken", task.result?.token!!).apply()
            })
    }

}