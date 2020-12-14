package com.raiyansoft.alpwapaelaqaria.ui.fragments.login

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.poovam.pinedittextfield.PinField.OnTextCompleteListener
import com.poovam.pinedittextfield.SquarePinField
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.InterActivity
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.model.Activation
import com.raiyansoft.alpwapaelaqaria.model.ResendResponse
import com.raiyansoft.alpwapaelaqaria.model.VerifyResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_varfication.view.*
import org.jetbrains.annotations.NotNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VarficationFragment : Fragment() {

    private var currentLang = ""

    private lateinit var codeHolder: SquarePinField
    private lateinit var minutes: TextView
    private lateinit var seconds: TextView
    private lateinit var resend: TextView
    private lateinit var inter : CircularProgressButton

    private var done = 0
    private var check = false
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button
    private lateinit var activation_code : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_varfication, container, false)

        val down = AnimationUtils.loadAnimation(activity, R.anim.down_move_anim)
        val up = AnimationUtils.loadAnimation(activity, R.anim.top_move_anim)
        val left = AnimationUtils.loadAnimation(activity, R.anim.lefttoright)

        root.llVerifyLogo.startAnimation(down)
        root.llVerifyInfo.startAnimation(up)
        root.tvVerifyInfo.startAnimation(left)

        codeHolder = root.squareField
        minutes = root.tvMinutesTimer
        seconds = root.tvSecondTimer
        resend = root.tvResend
        inter = root.btnVerify
        activation_code = ""

        dialog = Dialog(activity!!)
        dialog.setContentView(R.layout.custom_dialog)
        title = dialog.tvDialogTitle
        details = dialog.tvDialogText
        ok = dialog.btnOk
        dialog.setCancelable(false)
        ok.setOnClickListener {
            dialog.cancel()
        }

        val sharedPreferences = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("userToken", "")!!
        currentLang = sharedPreferences.getString("lang", "ar")!!

        if (currentLang != "ar"){
            root.imgVerifyBack.setImageResource(R.drawable.ic_back)
        }

        codeHolder.onTextCompleteListener = object : OnTextCompleteListener {
            override fun onTextComplete(@NotNull enteredText: String): Boolean {
                activation_code = enteredText
                inter.startAnimation{
                    activeAccount()
                }
                return true // Return false to keep the keyboard open else return true to close the keyboard
            }
        }

        counter()

        inter.setOnClickListener {
            inter.startAnimation{
                activeAccount()
            }
        }

        root.tvResend.setOnClickListener {
            resendClick()
        }

        root.imgVerifyBack.setOnClickListener {
            backClick()
        }

        return root
    }

    private fun confirmClick(){
        if (check) {
            val shared = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
            val editor = shared.edit()
            editor.putBoolean("login", true)
            editor.apply()
            val intent = Intent(activity, InterActivity::class.java)
            activity!!.startActivity(intent)
            activity!!.finish()
        }else{
            title.text = getString(R.string.login_faild)
            details.text = getString(R.string.wrong_code)
            dialog.show()
            inter.revertAnimation() {
                inter.text = getString(R.string.try_again)
            }
        }
    }

    private fun resendClick(){
        val service =  ServiceBuilder.apis!!
        val call = service.resendActivation(currentLang, "Bearer $token")
        call.enqueue(object : Callback<ResendResponse> {
            override fun onFailure(call: Call<ResendResponse>, t: Throwable) {
                title.text = getString(R.string.login_faild)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
                inter.revertAnimation() {
                    inter.text = getString(R.string.try_again)
                }
            }

            override fun onResponse(call: Call<ResendResponse>, response: Response<ResendResponse>) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200){
                        title.text = getString(R.string.attention)
                        details.text = getString(R.string.resend_messsage)
                        dialog.show()
                        counter()
                    }else if (!body.status!! && body.code!! == 112){
                        title.text = getString(R.string.login_faild)
                        details.text = getString(R.string.time_out)
                        dialog.show()
                    }else{
                        title.text = getString(R.string.login_faild)
                        details.text = getString(R.string.somthing_wrong)
                        dialog.show()
                    }
                }
            }
        })
    }

    private fun backClick(){
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.fragmentStartContainer, LoginFragment()).commit()
    }

    private fun counter(){

        resend.setTextColor(resources.getColor(R.color.gray))
        resend.isEnabled = false
        resend.isClickable = false
        minutes.text = "01"
         object : CountDownTimer(60000, 999) {
            override fun onTick(millisUntilFinished: Long) {
                if ((millisUntilFinished/1000) < 10){
                    seconds.text = "0${millisUntilFinished / 1000}"
                }else {
                    seconds.text = "${millisUntilFinished / 1000}"
                }
            }

            override fun onFinish() {
                if (done == 0) {
                    minutes.text = "00"
                    object : CountDownTimer(60000, 999){
                        override fun onFinish() {
                            resend.setTextColor(resources.getColor(R.color.colorPrimary))
                            resend.isEnabled = true
                            resend.isClickable = true
                        }

                        override fun onTick(millisUntilFinished: Long) {
                            if ((millisUntilFinished/1000) < 10){
                                seconds.text = "0${millisUntilFinished / 1000}"
                            }else {
                                seconds.text = "${millisUntilFinished / 1000}"
                            }
                        }

                    }.start()
                }
            }
        }.start()
    }

    private fun activeAccount(){
        val service = ServiceBuilder.apis!!
        val activate = Activation(activation_code)
        val call = service.activateAccount(currentLang, "Bearer $token", activate)
        call.enqueue(object : Callback<VerifyResponse> {
            override fun onFailure(call: Call<VerifyResponse>, t: Throwable) {
                title.text = getString(R.string.login_faild)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
                inter.revertAnimation() {
                    inter.text = getString(R.string.try_again)
                }
            }

            override fun onResponse(call: Call<VerifyResponse>, response: Response<VerifyResponse>) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200){
                        check = true
                        confirmClick()
                    }else{
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

    override fun onPause() {
        super.onPause()
        done = 1
    }

    override fun onDestroy() {
        super.onDestroy()
        done = 1
    }

}