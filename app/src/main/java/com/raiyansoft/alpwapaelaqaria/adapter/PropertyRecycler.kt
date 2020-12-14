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
import com.raiyansoft.alpwapaelaqaria.model.Building
import kotlinx.android.synthetic.main.property_style.view.*

class PropertyRecycler(var activity: Activity, var data: ArrayList<Building>) :
    RecyclerView.Adapter<PropertyRecycler.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.property_style, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.title.text = data[position].title
        holder.type.text = data[position].categoryTitle
        if (data[position].images!!.size > 0) {
            Glide
                .with(activity)
                .load(data[position].images!![0]!!.image)
                .centerCrop()
                .placeholder(R.drawable.property_placeholder)
                .into(holder.image)
        }

        holder.card.setOnClickListener {
            val intent = Intent(activity, SettingsActivity::class.java)
            intent.putExtra("propId", data[position].id)
            intent.putExtra("open", 3)
            activity.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var image: ImageView = item.imgBuildingImage
        var title: TextView = item.tvSellTitle
        var type: TextView = item.tvBuildingType
        var card: CardView = item.cvBuilding
    }
}