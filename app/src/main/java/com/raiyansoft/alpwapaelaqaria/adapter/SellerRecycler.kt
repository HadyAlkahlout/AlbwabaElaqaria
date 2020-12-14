package com.raiyansoft.alpwapaelaqaria.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.SettingsActivity
import com.raiyansoft.alpwapaelaqaria.model.Seller
import kotlinx.android.synthetic.main.seller_view.view.*

class SellerRecycler(var activity: Activity, var data: ArrayList<Seller> ) :
    RecyclerView.Adapter<SellerRecycler.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.seller_view, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        Glide
            .with(activity)
            .load(data[position].image)
            .centerCrop()
            .placeholder(R.drawable.profile)
            .into(holder.image)
        if (data[position].name!!.length > 17){
            holder.name.text = "${data[position].name!!.subSequence(0..16)}..."
        }else {
            holder.name.text = data[position].name
        }
        holder.card.setOnClickListener {
            val intent = Intent(activity, SettingsActivity::class.java)
            intent.putExtra("workerId", data[position].id)
            intent.putExtra("open", 4)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var image: ImageView = item.imgSellerPhoto
        var name: TextView = item.tvSellerName
        var card: CardView = item.cvSeller
    }
}