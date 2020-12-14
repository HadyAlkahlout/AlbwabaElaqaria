package com.raiyansoft.alpwapaelaqaria.ui.fragments.splash

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.InterActivity
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.StartActivity
import com.raiyansoft.alpwapaelaqaria.model.SetResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_splash.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.exitProcess

class SplashFragment : Fragment() {private lateinit var loading: ProgressBar

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var updateDialog: Dialog
    private lateinit var updateTitle: TextView
    private lateinit var updateDetails: TextView
    private lateinit var updateOk: Button

    private lateinit var editDialog: Dialog
    private lateinit var editTitle: TextView
    private lateinit var editDetails: TextView
    private lateinit var editOk: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_splash, container, false)

        loading = root.pbWait

        val sharedPreferences = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        token = sharedPreferences.getString("userToken", "")!!

        dialog = Dialog(activity!!)
        dialog.setContentView(R.layout.custom_dialog)
        title = dialog.tvDialogTitle
        details = dialog.tvDialogText
        ok = dialog.btnOk
        dialog.setCancelable(false)
        ok.setOnClickListener {
            dialog.cancel()
        }

        updateDialog = Dialog(activity!!)
        updateDialog.setContentView(R.layout.custom_dialog)
        updateTitle = updateDialog.tvDialogTitle
        updateDetails = updateDialog.tvDialogText
        updateOk = updateDialog.btnOk
        updateDialog.setCancelable(false)
        updateOk.setOnClickListener {
            updateDialog.cancel()
            val appPackageName =
                activity!!.packageName // getPackageName() from Context or Activity object

            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
                activity!!.finish()
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
                activity!!.finish()
            }
        }

        editDialog = Dialog(activity!!)
        editDialog.setContentView(R.layout.custom_dialog)
        editTitle = editDialog.tvDialogTitle
        editDetails = editDialog.tvDialogText
        editOk = editDialog.btnOk
        editDialog.setCancelable(false)
        editOk.setOnClickListener {
            editDialog.cancel()
            exitProcess(0)
        }


        loading.visibility = View.VISIBLE

        if (token != "") {
            val pInfo: PackageInfo = activity!!.packageManager.getPackageInfo(activity!!.packageName, 0)
            val version = pInfo.versionCode
            val service = ServiceBuilder.apis!!
            val call = service.getSet(currentLang, "Bearer $token")
            call.enqueue(object : Callback<SetResponse> {
                override fun onResponse(call: Call<SetResponse>, response: Response<SetResponse>) {
                    if (response.body() != null) {
                        val body = response.body()
                        if (body!!.status!! && body.code!! == 200) {
                            if ((body.data!!.forceUpdate!! == "yes" || body.data!!.forceUpdate!! == "android") && body.data!!.androidVersion!! == version.toString()) {
                                loading.visibility = View.GONE
                                updateTitle.text = getString(R.string.attention)
                                updateDetails.text = getString(R.string.update)
                                updateDialog.show()
                            } else if (body.data!!.forceClose!! == "yes" || body.data!!.forceClose!! == "android") {
                                loading.visibility = View.GONE
                                editTitle.text = getString(R.string.attention)
                                editDetails.text = getString(R.string.edit)
                                editDialog.show()
                            } else if (body.data!!.forceUpdate!! == "no" && body.data!!.forceClose!! == "no") {
                                editor.putString("promote", body.data!!.special!!)
                                editor.apply()
                                val intent = Intent(activity!!, InterActivity::class.java)
                                loading.visibility = View.GONE
                                startActivity(intent)
                                activity!!.overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_right
                                )
                                activity!!.finish()
                            }
                        } else {
                            loading.visibility = View.GONE
                            title.text = getString(R.string.attention)
                            details.text = getString(R.string.somthing_wrong)
                            dialog.show()
                        }
                    }
                }

                override fun onFailure(call: Call<SetResponse>, t: Throwable) {
                    loading.visibility = View.GONE
                    title.text = getString(R.string.attention)
                    details.text = getString(R.string.somthing_wrong)
                    dialog.show()
                }
            })
        } else {
            Handler().postDelayed({
                loading.visibility = View.GONE
                val intent = Intent(activity!!, StartActivity::class.java)
                startActivity(intent)
                activity!!.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
                activity!!.finish()
            }, 3000)
        }

        return root
    }
}