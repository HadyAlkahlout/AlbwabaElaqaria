package com.raiyansoft.alpwapaelaqaria.adapter

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.model.FavResponse
import com.raiyansoft.alpwapaelaqaria.model.Notification
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.notification_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationRecycler(var activity: Activity, var data: List<Notification>) :
    RecyclerView.Adapter<NotificationRecycler.MyViewHolder>() {

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.notification_item, parent, false)

        val sharedPreferences = activity.getSharedPreferences("shared", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("userToken", "")!!
        currentLang = sharedPreferences.getString("lang", "ar")!!

        loading = ProgressDialog(activity)
        loading.setCancelable(false)
        loading.setMessage(activity.getString(R.string.loading_data))

        dialog = Dialog(activity)
        dialog.setContentView(R.layout.custom_dialog)
        title = dialog.tvDialogTitle
        details = dialog.tvDialogText
        ok = dialog.btnOk
        dialog.setCancelable(false)
        ok.setOnClickListener {
            dialog.cancel()
        }
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.title.text = data[position].title
        holder.detail.text = data[position].message
        holder.time.text = data[position].createdAt

        if (data[position].read!! == 0){
            holder.seen.visibility = View.VISIBLE
        }else{
            holder.seen.visibility = View.GONE
        }

        holder.notification.setOnClickListener {
            if (data[position].read!! == 0){
                loading.show()
                val service =  ServiceBuilder.apis!!
                val call = service.readNotification(currentLang, "Bearer $token", data[position].id!!)
                call.enqueue(object : Callback<FavResponse> {
                    override fun onResponse(call: Call<FavResponse>, response: Response<FavResponse>) {
                        if (response.body() != null) {
                            val body = response.body()
                            if (body!!.status!! && body.code!! == 200) {
                                holder.seen.visibility = View.GONE
                                loading.dismiss()
                            } else {
                                title.text = activity.getString(R.string.attention)
                                details.text = activity.getString(R.string.somthing_wrong)
                                dialog.show()
                                loading.dismiss()
                            }
                        }
                    }

                    override fun onFailure(call: Call<FavResponse>, t: Throwable) {
                        title.text = activity.getString(R.string.attention)
                        details.text = activity.getString(R.string.somthing_wrong)
                        dialog.show()
                        loading.dismiss()
                    }
                })
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var notification: ConstraintLayout = item.clNotification
        var title: TextView = item.tvNotificationTitle
        var detail: TextView = item.tvNotificationDetail
        var time: TextView = item.tvNotificationTime
        var seen: CardView = item.cvNotificationSeen
    }
}