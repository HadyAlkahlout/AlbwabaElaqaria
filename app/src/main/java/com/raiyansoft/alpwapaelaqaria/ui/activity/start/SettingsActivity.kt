package com.raiyansoft.alpwapaelaqaria.ui.activity.start

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.util.Common
import com.raiyansoft.alpwapaelaqaria.ui.fragments.item.PropertyShowFragment
import com.raiyansoft.alpwapaelaqaria.ui.fragments.item.WorkerProfileFragment
import com.raiyansoft.alpwapaelaqaria.ui.fragments.subcategory.CategoriesHeadFragment
import com.raiyansoft.alpwapaelaqaria.ui.fragments.notification.NotificationFragment
import com.raiyansoft.alpwapaelaqaria.ui.fragments.settings.SettingsFragment

class SettingsActivity : AppCompatActivity() {

    var propId = 0

    var opened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val shared = getSharedPreferences("shared", Context.MODE_PRIVATE)
        val lang = shared.getString("lang", "ar")!!
        Common.setLocale(lang, this)

        var case = 0
        var open = 0
        var catId = 0
        var workerId = 0
        if (intent != null) {
            case = intent.getIntExtra("case", 0)
            open = intent.getIntExtra("open", 0)
            catId = intent.getIntExtra("catId", 0)
            propId = intent.getIntExtra("propId", 0)
            workerId = intent.getIntExtra("workerId", 0)
        }

        handelDynamicLink()

        if(!opened) {
            when (open) {
                0 -> {
                    val fragment = SettingsFragment()
                    val bundle = Bundle()
                    bundle.putInt("case", case)
                    fragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flSettingsHolder, fragment).commit()
                }
                1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flSettingsHolder, NotificationFragment()).commit()
                }
                2 -> {
                    val fragment = CategoriesHeadFragment()
                    val bundle = Bundle()
                    bundle.putInt("id", catId)
                    fragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flSettingsHolder, fragment).commit()
                }
                3 -> {
                    val fragment = PropertyShowFragment()
                    val bundle = Bundle()
                    bundle.putInt("id", propId)
                    fragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flSettingsHolder, fragment).commit()
                }
                4 -> {
                    val fragment = WorkerProfileFragment()
                    val bundle = Bundle()
                    bundle.putInt("id", workerId)
                    fragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.flSettingsHolder, fragment).commit()
                }
            }
        }

    }

    private fun handelDynamicLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener {
            if (it != null) {
                opened = true
                val link = it.link
                val id = link!!.encodedQuery!!.subSequence(7, link.encodedQuery!!.length).toString()
                val fragment = PropertyShowFragment()
                val bundle = Bundle()
                bundle.putInt("id", Integer.parseInt(id))
                fragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.flSettingsHolder, fragment)
                    .commit()
            }
        }
    }

}