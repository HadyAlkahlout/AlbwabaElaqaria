package com.raiyansoft.alpwapaelaqaria.ui.fragments.home

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.ui.activity.search.SearchActivity
import com.raiyansoft.alpwapaelaqaria.adapter.SellerRecycler
import com.raiyansoft.alpwapaelaqaria.model.Seller
import com.raiyansoft.alpwapaelaqaria.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_worker.view.*
import java.util.*

class WorkerFragment : Fragment() {

    private lateinit var workers: RecyclerView
    private lateinit var search: ImageView

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
        val root = inflater.inflate(R.layout.fragment_worker, container, false)

        workers = root.rcWorkers
        search = root.imgWorkerSearch

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


        viewModel.workersLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            adapterWorkers.data.addAll(it.data!!.data!!)
            var x = 2
            while (x <= it.data!!.pages!!) {
                viewModel.getExtraWorkers(x)
                adapterWorkers.notifyDataSetChanged()

                x++
            }

        })
        viewModel.workersExtraLiveData.observe(viewLifecycleOwner, Observer {
            adapterWorkers.data.addAll(it.data!!.data!!)
            adapterWorkers.notifyDataSetChanged()

            fillWorkers(adapterWorkers.data)
        })

        search.setOnClickListener {
            serchClick()
        }

        fillWorkers(adapterWorkers.data)

        return root
    }

    val adapterWorkers by lazy {
        SellerRecycler(requireActivity(), ArrayList())
    }

    private fun fillWorkers(data: List<Seller>) {
        workers.visibility = View.VISIBLE
        workers.adapter = adapterWorkers
        workers.layoutManager = GridLayoutManager(activity, 3)
    }

    private fun serchClick() {
        val intent = Intent(activity, SearchActivity::class.java)
        intent.putExtra("open", 1)
        activity!!.startActivity(intent)
    }
}