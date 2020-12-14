package com.raiyansoft.alpwapaelaqaria.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.SettingsActivity
import com.raiyansoft.alpwapaelaqaria.model.Category
import kotlinx.android.synthetic.main.category_item.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

class CategoriesRecycler(var activity: Activity, var data: ArrayList<Category>) :
    RecyclerView.Adapter<CategoriesRecycler.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.category_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.title.text = data[position].title
        holder.num.text = data[position].properties.toString()

        holder.category.setOnClickListener {
            val intent = Intent(activity, SettingsActivity::class.java)
            intent.putExtra("catId", data[position].id)
            intent.putExtra("open", 2)
            activity.startActivity(intent)
        }

        val shared = activity.getSharedPreferences("shared", Context.MODE_PRIVATE)
        val currentLang = shared.getString("lang", "ar")!!

        if (currentLang != "ar"){
            holder.go.setImageResource(R.drawable.ic_back)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var title: TextView = item.tvTitle
        var num: TextView = item.tvNum
        var go: ImageView = item.imgGo
        var category: ConstraintLayout = item.clCategoryItem
    }
}