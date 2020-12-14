package com.raiyansoft.alpwapaelaqaria.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.model.Category
import kotlinx.android.synthetic.main.category_head.view.*

class CategoriesHeadRecycler(var activity: Activity, var data: List<Category>, var onClick: PhotoCliCk) :
    RecyclerView.Adapter<CategoriesHeadRecycler.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.category_head, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {

        holder.name.text = data[position].title

        holder.image.setOnClickListener {
            onClick.onClick(data[position].id!!)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var image: ImageView = item.imgPhoto
        var name: TextView = item.tvName
    }

    interface PhotoCliCk{
        fun onClick(id: Int)
    }
}