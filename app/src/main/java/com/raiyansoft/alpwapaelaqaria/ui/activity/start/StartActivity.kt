package com.raiyansoft.alpwapaelaqaria.ui.activity.start

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.util.Common
import com.raiyansoft.alpwapaelaqaria.ui.fragments.login.LoginFragment

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val shared = getSharedPreferences("shared", Context.MODE_PRIVATE)
        val lang = shared.getString("lang", "ar")!!
        Common.setLocale(lang, this)

        supportFragmentManager.beginTransaction().replace(R.id.fragmentStartContainer, LoginFragment()).commit()

    }
}