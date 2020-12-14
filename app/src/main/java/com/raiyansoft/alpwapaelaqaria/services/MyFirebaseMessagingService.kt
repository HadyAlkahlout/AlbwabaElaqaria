package com.raiyansoft.alpwapaelaqaria.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.ui.activity.start.VideoActivity
import com.raiyansoft.alpwapaelaqaria.model.SetResponse
import com.raiyansoft.alpwapaelaqaria.network.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FCM"

    private val currentLang = Locale.getDefault().language
    private lateinit var userToken: String

    override fun onNewToken(token: String) {
        val sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)
        userToken = sharedPreferences.getString("userToken", "")!!
        sharedPreferences.edit().putString("deviceToken", token).apply()
        val service = ServiceBuilder.apis!!
        val call = service.getSet(currentLang, "Bearer $userToken", token)
        call.enqueue(object : Callback<SetResponse> {
            override fun onFailure(call: Call<SetResponse>, t: Throwable) {}

            override fun onResponse(
                call: Call<SetResponse>,
                response: Response<SetResponse>
            ) {}
        })
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.notification != null) {
            showNotification(remoteMessage)
        }
    }

    private fun showNotification(remoteMessage: RemoteMessage) {
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "FCM_NOTIFICATION_CHANNEL",
                "FCM_CHANNEL_NOTIFICATION",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "This is channel for sending fcm app notification"
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.lightColor = Color.GRAY
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            mNotificationManager.createNotificationChannel(channel)
        }
        val mBuilder =
            NotificationCompat.Builder(applicationContext, "FCM_NOTIFICATION_CHANNEL")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(remoteMessage.notification!!.title) // title for notification
                .setContentText(remoteMessage.notification!!.body)// message for notification
                .setAutoCancel(true) // clear notification after click

        val intent = Intent(applicationContext, VideoActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        val time = Date().time
        val tmpStr = time.toString()
        val last4Str = (tmpStr.subSequence(tmpStr.length - 5, tmpStr.length - 1)).toString()
        val notificationId = Integer.parseInt(last4Str)

        mNotificationManager.notify(notificationId, mBuilder.build())
    }

}