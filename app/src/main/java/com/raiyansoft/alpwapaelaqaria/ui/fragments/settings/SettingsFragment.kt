package com.raiyansoft.alpwapaelaqaria.ui.fragments.settings

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.raiyansoft.alpwapaelaqaria.*
import com.raiyansoft.alpwapaelaqaria.util.Common
import com.raiyansoft.alpwapaelaqaria.model.FavResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.InterActivity
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.StartActivity
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.language_dialoge.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsFragment : Fragment() {

    private lateinit var back: ImageView
    private lateinit var terms: LinearLayout
    private lateinit var favorite: LinearLayout
    private lateinit var promote: LinearLayout
    private lateinit var language: LinearLayout
    private lateinit var languageName: TextView
    private lateinit var asked: LinearLayout
    private lateinit var privacy: LinearLayout
    private lateinit var about: LinearLayout
    private lateinit var call: LinearLayout
    private lateinit var logout: LinearLayout
    private lateinit var line: View
    private lateinit var line2: View

    private var case = 0

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        back = root.imgSettingsBack
        terms = root.llTermsOfUse
        favorite = root.llFav
        promote = root.llPromote
        language = root.llAppLanguage
        languageName = root.tvLanguage
        asked = root.llAskedQuestions
        privacy = root.llPrivacyPolicy
        about = root.llAboutUs
        call = root.llCallUs
        logout = root.llLogout
        line = root.viewLine
        line2 = root.viewLine2

        sharedPreferences = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("userToken", "")!!
        currentLang = sharedPreferences.getString("lang", "ar")!!

        if (currentLang != "ar"){
            back.setImageResource(R.drawable.ic_back)
            root.imgIc_1.setImageResource(R.drawable.ic_back)
            root.imgIc_2.setImageResource(R.drawable.ic_back)
            root.imgIc_3.setImageResource(R.drawable.ic_back)
            root.imgIc_4.setImageResource(R.drawable.ic_back)
            root.imgIc_5.setImageResource(R.drawable.ic_back)
            root.imgIc_6.setImageResource(R.drawable.ic_back)
        }

        when (currentLang) {
            "en" -> {
                languageName.text = "English"
            }
            "ar" -> {
                languageName.text = "العربية"
            }
            "tr" -> {
                languageName.text = "Türkçe"
            }
        }

        val bundle = arguments
        if (bundle != null) {
            case = bundle.getInt("case", 0)
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

        if (case == 0) {
            favorite.visibility = View.GONE
            line.visibility = View.GONE
            line2.visibility = View.VISIBLE
            promote.visibility = View.VISIBLE
        } else {
            favorite.visibility = View.VISIBLE
            line.visibility = View.VISIBLE
            line2.visibility = View.GONE
            promote.visibility = View.GONE
        }

        back.setOnClickListener {
            backClick()
        }

        terms.setOnClickListener {
            termsClick()
        }

        favorite.setOnClickListener {
            favClick()
        }

        promote.setOnClickListener {
            promoteClick()
        }

        language.setOnClickListener {
            langClick()
        }

        asked.setOnClickListener {
            askedClick()
        }

        privacy.setOnClickListener {
            privacyClick()
        }

        about.setOnClickListener {
            aboutClick()
        }

        call.setOnClickListener {
            callClick()
        }

        logout.setOnClickListener {
            logoutClick()
        }

        return root
    }

    private fun backClick() {
        //Done
        val edit = sharedPreferences.edit()
        if (sharedPreferences.getString("lang", "ar") == "ar") {
            edit.putInt("active", 4)
        }else{
            edit.putInt("active", 0)
        }
        edit.apply()
        val intent = Intent(activity!!, InterActivity::class.java)
        intent.putExtra("open", 4)
        startActivity(intent)
        activity!!.finish()
    }

    private fun termsClick() {
        val fragment = TermsFragment()
        val bundle = Bundle()
        bundle.putInt("case", case)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flSettingsHolder, fragment).commit()
    }

    private fun favClick() {
        val fragment = FavoriteFragment()
        val bundle = Bundle()
        bundle.putInt("case", case)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flSettingsHolder, fragment).commit()
    }

    private fun promoteClick() {
        //Done
        loading.show()
        val service =  ServiceBuilder.apis!!
        val call = service.upgradeAccount(currentLang, "Bearer $token")
        call.enqueue(object : Callback<FavResponse> {
            override fun onResponse(call: Call<FavResponse>, response: Response<FavResponse>) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        loading.dismiss()
                        title.text = getString(R.string.attention)
                        details.text = getString(R.string.promote_done)
                        dialog.show()
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

    private fun langClick() {
        val editor = sharedPreferences.edit()
        val langDialog = Dialog(activity!!)
        langDialog.setContentView(R.layout.language_dialoge)
        langDialog.setCancelable(true)
        val pick = langDialog.btnPickLang
        val english = langDialog.rbEnglish
        val arabic = langDialog.rbArabic
        val turkey = langDialog.rbTurkey
        when (currentLang) {
            "en" -> {
                english.isChecked = true
                arabic.isChecked = false
                turkey.isChecked = false
            }
            "ar" -> {
                english.isChecked = false
                arabic.isChecked = true
                turkey.isChecked = false
            }
            "tr" -> {
                english.isChecked = false
                arabic.isChecked = false
                turkey.isChecked = true
            }
        }
        pick.setOnClickListener {
            if (english.isChecked) {
                editor.putString("lang", "en")
                Common.setLocale("en", activity!!)
                languageName.text = "English"
            } else if (arabic.isChecked) {
                editor.putString("lang", "ar")
                Common.setLocale("ar", activity!!)
                languageName.text = "العربية"
            } else if (turkey.isChecked) {
                editor.putString("lang", "tr")
                Common.setLocale("tr", activity!!)
                languageName.text = "Türkçe"
            } else {
                langDialog.dismiss()
            }
            editor.apply()
            langDialog.dismiss()
            val edit = sharedPreferences.edit()
            if (sharedPreferences.getString("lang", "ar") == "ar") {
                edit.putInt("active", 4)
            }else{
                edit.putInt("active", 0)
            }
            edit.apply()
            val intent = Intent(activity!!, InterActivity::class.java)
            intent.putExtra("open", 4)
            startActivity(intent)
            activity!!.finish()
        }
        langDialog.show()
    }

    private fun askedClick() {
        val fragment = AskedFragment()
        val bundle = Bundle()
        bundle.putInt("case", case)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flSettingsHolder, fragment).commit()
    }

    private fun privacyClick() {
        val fragment = PrivacyFragment()
        val bundle = Bundle()
        bundle.putInt("case", case)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flSettingsHolder, fragment).commit()
    }

    private fun aboutClick() {
        val fragment = AboutFragment()
        val bundle = Bundle()
        bundle.putInt("case", case)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flSettingsHolder, fragment).commit()
    }

    private fun callClick() {
        val fragment = CallUsFragment()
        val bundle = Bundle()
        bundle.putInt("case", case)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flSettingsHolder, fragment).commit()
    }

    private fun logoutClick() {
        loading.show()
        val service = ServiceBuilder.apis!!
        val call = service.logout(currentLang, "Bearer $token")
        call.enqueue(object : Callback<FavResponse> {
            override fun onResponse(call: Call<FavResponse>, response: Response<FavResponse>) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        loading.dismiss()
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("login", false)
                        editor.putString("userID", "")
                        editor.putString("userToken", "")
                        editor.apply()
                        val intent = Intent(activity!!, StartActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        activity!!.finish()
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