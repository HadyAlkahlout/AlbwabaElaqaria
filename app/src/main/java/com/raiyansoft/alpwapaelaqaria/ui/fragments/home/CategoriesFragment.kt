package com.raiyansoft.alpwapaelaqaria.ui.fragments.home

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.adapter.CategoriesRecycler
import com.raiyansoft.alpwapaelaqaria.model.Category
import com.raiyansoft.alpwapaelaqaria.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_categories.view.*

class CategoriesFragment : Fragment() {

    private lateinit var categories: RecyclerView

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog


    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_categories, container, false)

        categories = root.rcCategories

        dialog = Dialog(activity!!)
        dialog.setContentView(R.layout.custom_dialog)
        title = dialog.tvDialogTitle
        details = dialog.tvDialogText
        ok = dialog.btnOk
        dialog.setCancelable(false)
        ok.setOnClickListener {
            dialog.cancel()
        }

        loading = ProgressDialog(activity)
        loading.setCancelable(false)
        loading.setMessage(getString(R.string.loading_data))

        val sharedPreferences = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("userToken", "")!!
        currentLang = sharedPreferences.getString("lang", "ar")!!


        viewModel.categoriesLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            adapterCate.data.addAll(it.data!!.data!!)
            var x = 2
            while (x <= it.data!!.pages!!) {
                viewModel.getExtraCategories(x)
                adapterCate.notifyDataSetChanged()

                x++
            }

        })

        viewModel.categoriesExtraLiveData.observe(viewLifecycleOwner, Observer {
            adapterCate.data.addAll(it.data!!.data!!)
            adapterCate.notifyDataSetChanged()

        })


        fillCategories(adapterCate.data)

        return root
    }

    val adapterCate by lazy {
        CategoriesRecycler(requireActivity(), ArrayList())
    }

    private fun fillCategories(data: List<Category>) {
        categories.adapter = adapterCate
        categories.layoutManager = LinearLayoutManager(activity)
    }


}