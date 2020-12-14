package com.raiyansoft.alpwapaelaqaria.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.model.Feature
import kotlinx.android.synthetic.main.feature_item.view.*

class FeatureRecycler(var activity: Activity, var data: List<Feature>, var click: FeatureClick) :
    RecyclerView.Adapter<FeatureRecycler.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.feature_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.feature.text = data[position].title!!
        holder.feature.setOnCheckedChangeListener { _, isChecked ->
            click.onClick(data[position].id!!, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var feature: CheckBox = item.cbFeature
    }

    interface FeatureClick{
        fun onClick(id: Int, isChecked: Boolean)
    }
}