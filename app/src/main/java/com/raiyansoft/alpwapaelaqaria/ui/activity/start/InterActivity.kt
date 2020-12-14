package com.raiyansoft.alpwapaelaqaria.ui.activity.start

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.util.Common
import com.raiyansoft.alpwapaelaqaria.ui.fragments.home.*
import kotlinx.android.synthetic.main.activity_inter.*

class InterActivity : AppCompatActivity() {

    private lateinit var home: View
    private lateinit var seller: View
    private lateinit var category: View
    private lateinit var map: View
    private lateinit var profile: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inter)

        val shared = getSharedPreferences("shared", Context.MODE_PRIVATE)
        val lang = shared.getString("lang", "ar")!!
        Common.setLocale(lang, this)

        val anim = AnimationUtils.loadAnimation(this, R.anim.top_move_anim)
        llBottomMenu.startAnimation(anim)
        val down = AnimationUtils.loadAnimation(this, R.anim.down_move_anim)
        flInterHolder.startAnimation(down)

        var active = 0
        val edit = shared.edit()
        active = shared.getInt("active", 0)

        var open = 0
        edit.putInt("active", 0)
        edit.apply()
        active = 0
        if (intent != null){
            open = intent.getIntExtra("open", 0)
        }

        home = vwHome
        seller = vwSaller
        category = vwCategory
        map = vwMap
        profile = vwProfile

        openPage(open)

        clHome.setOnClickListener {
            if (active != 0) {
                edit.putInt("active", 0)
                edit.apply()
                active = 0
                openPage(0)
            }
        }

        clSaller.setOnClickListener {
            if (active != 1) {
                edit.putInt("active", 1)
                edit.apply()
                active = 1
                openPage(1)
            }
        }

        clCategory.setOnClickListener {
            if (active != 2) {
                edit.putInt("active", 2)
                edit.apply()
                active = 2
                openPage(2)
            }
        }

        clMap.setOnClickListener {
            if (active != 3) {
                edit.putInt("active", 3)
                edit.apply()
                active = 3
                openPage(3)
            }
        }

        clProfile.setOnClickListener {
            if (active != 4) {
                edit.putInt("active", 4)
                edit.apply()
                active = 4
                openPage(4)
            }
        }

    }

    private fun openPage(id : Int){
        when(id){
            0 -> {
                home.setBackgroundColor(resources.getColor(R.color.colorAccent))
                seller.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                category.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                map.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                profile.setBackgroundColor(resources.getColor(R.color.colorPrimary))

                supportFragmentManager.beginTransaction().replace(R.id.flInterHolder, HomeFragment()).commit()
            }
            1 -> {
                home.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                seller.setBackgroundColor(resources.getColor(R.color.colorAccent))
                category.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                map.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                profile.setBackgroundColor(resources.getColor(R.color.colorPrimary))

                supportFragmentManager.beginTransaction().replace(R.id.flInterHolder, WorkerFragment()).commit()
            }
            2 -> {
                home.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                seller.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                category.setBackgroundColor(resources.getColor(R.color.colorAccent))
                map.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                profile.setBackgroundColor(resources.getColor(R.color.colorPrimary))

                supportFragmentManager.beginTransaction().replace(R.id.flInterHolder, CategoriesFragment()).commit()
            }
            3 -> {
                home.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                seller.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                category.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                map.setBackgroundColor(resources.getColor(R.color.colorAccent))
                profile.setBackgroundColor(resources.getColor(R.color.colorPrimary))

                supportFragmentManager.beginTransaction().replace(R.id.flInterHolder, MapFragment()).commit()
            }
            4 -> {
                home.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                seller.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                category.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                map.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                profile.setBackgroundColor(resources.getColor(R.color.colorAccent))

                supportFragmentManager.beginTransaction().replace(R.id.flInterHolder, ProfileFragment()).commit()
            }
        }
    }
}