package com.raiyansoft.alpwapaelaqaria.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.model.Question
import kotlinx.android.synthetic.main.qustions_item.view.*

class QuestionsRecycler(var activity: Activity, var data: List<Question>) :
    RecyclerView.Adapter<QuestionsRecycler.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.qustions_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.title.text = data[position].question
        holder.detail.text = data[position].answer
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var title: TextView = item.tvTermTitle
        var detail: TextView = item.tvTermDetail
    }
}