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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.adapter.QuestionsRecycler
import com.raiyansoft.alpwapaelaqaria.model.FaqResponse
import com.raiyansoft.alpwapaelaqaria.model.Question
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_asked.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AskedFragment : Fragment() {

    private lateinit var back: ImageView
    private lateinit var qustions: RecyclerView

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
        val root = inflater.inflate(R.layout.fragment_asked, container, false)

        val shared = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        currentLang = shared.getString("lang", "ar")!!
        token = shared.getString("userToken", "")!!

        back = root.imgAskedBack
        qustions = root.rcAsked

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

        getFaq()

        back.setOnClickListener {
            backClick()
        }

        return root
    }

    private fun fillAsked(data: List<Question>) {
        if (data.size > 0) {
            qustions.visibility = View.VISIBLE
            val adapter = QuestionsRecycler(activity!!, data)
            qustions.adapter = adapter
            qustions.layoutManager = LinearLayoutManager(activity!!)
        }else{
            qustions.visibility = View.GONE
        }
    }

    private fun backClick(){
        val fragment = SettingsFragment()
        val bundle = Bundle()
        bundle.putInt("case", case)
        fragment.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.flSettingsHolder, fragment).commit()
    }

    private fun getFaq(){
        loading.show()
        val service = ServiceBuilder.apis!!
        val call = service.getFaq(currentLang, "Bearer $token")
        call.enqueue(object : Callback<FaqResponse> {
            override fun onResponse(call: Call<FaqResponse>, response: Response<FaqResponse>) {
                if (response.body() != null) {
                    val body = response.body()
                    if (body!!.status!! && body.code!! == 200) {
                        fillAsked(body.data!!.data!!)
                        loading.dismiss()
                    } else {
                        title.text = getString(R.string.attention)
                        details.text = getString(R.string.somthing_wrong)
                        dialog.show()
                        loading.dismiss()
                    }
                }
            }

            override fun onFailure(call: Call<FaqResponse>, t: Throwable) {
                title.text = getString(R.string.attention)
                details.text = getString(R.string.somthing_wrong)
                dialog.show()
                loading.dismiss()
            }
        })
    }
}