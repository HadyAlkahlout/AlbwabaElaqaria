package com.raiyansoft.alpwapaelaqaria.ui.fragments.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.raiyansoft.alpwapaelaqaria.R
import kotlinx.android.synthetic.main.fragment_video.view.*

class VideoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_video, container, false)

        val videoPath = "android.resource://" + activity!!.packageName + "/" + R.raw.splash_video
        root.vvSplash.setVideoPath(videoPath)
        root.vvSplash.start()
        root.vvSplash.setOnCompletionListener{
            val ft = activity!!.supportFragmentManager.beginTransaction()
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            ft.replace(R.id.flSplashHolder, SplashFragment())
            ft.commit()
        }

        return root
    }
}