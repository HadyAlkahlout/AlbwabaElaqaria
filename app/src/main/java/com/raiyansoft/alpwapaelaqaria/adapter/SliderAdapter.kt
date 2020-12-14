package com.raiyansoft.alpwapaelaqaria.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.model.Slider
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.slider_image.view.*


class SliderAdapter(var context: Context, var data : List<Slider>) :
    SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.slider_image, parent, false)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {

        Glide
            .with(context)
            .load(data[position].image)
            .centerCrop()
            .placeholder(R.drawable.property_placeholder)
            .into(viewHolder.image)

        viewHolder.item.setOnClickListener {
            if (data[position].url != null && data[position].url != "") {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data[position].url))
                context.startActivity(browserIntent)
            }
        }
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return data.size
    }

    inner class SliderAdapterVH(itemView: View) :
        ViewHolder(itemView) {
        var image : ImageView = itemView.imgSlider
        var item = itemView
    }
}