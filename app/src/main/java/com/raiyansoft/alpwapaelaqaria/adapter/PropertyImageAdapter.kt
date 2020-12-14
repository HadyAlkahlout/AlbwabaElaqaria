package com.raiyansoft.alpwapaelaqaria.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.model.Image
import kotlinx.android.synthetic.main.property_image.view.*


class PropertyImageAdapter(var context: Context, var data : List<Image>, var onClick: CancelClick) :
    RecyclerView.Adapter<PropertyImageAdapter.PropertyAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyAdapterVH {
        val inflate: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.property_image, parent, false)
        return PropertyAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: PropertyAdapterVH, position: Int) {

        Glide
            .with(context)
            .load(data[position].image)
            .centerCrop()
            .placeholder(R.drawable.property_placeholder)
            .into(viewHolder.image)

        viewHolder.cancel.setOnClickListener {
            onClick.cancelClick(position, data[position].id!!)
        }
    }

    override fun getItemCount(): Int {
        //slider view count could be dynamic size
        return data.size
    }

    inner class PropertyAdapterVH(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var image : ImageView = itemView.imgProperty
        var cancel : ImageView = itemView.imgCancel
    }

    interface CancelClick{
        fun cancelClick(position: Int, id: Int)
    }
}