package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var seeMorePendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        radioGlide.setOnClickListener {
            radioRetro.isChecked = false
            radioUdacity.isChecked = false
            URL = getString(R.string.glideUrl)
            NAME = getString(R.string.glideString)
        }
        radioRetro.setOnClickListener {
            radioGlide.isChecked = false
            radioUdacity.isChecked = false

            URL = getString(R.string.retrofitUrl)
            NAME = getString(R.string.retrofitString)
        }
        radioUdacity.setOnClickListener {
            radioGlide.isChecked = false
            radioRetro.isChecked = false

            URL = getString(R.string.udacityUrl)
            NAME = getString(R.string.udacityString)
        }
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel(getString(R.string.notification_channel), "downloads")
        custom_button.setOnClickListener {
            if (null != URL) {
                download()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Please Select Download Link",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = DownloadManager.Query()
            id?.let { query.setFilterById(it) }
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            val seeMoreIntent = Intent(applicationContext, DetailActivity::class.java)
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {

                        custom_button.buttonState = ButtonState.Completed
                        seeMoreIntent.putExtra("status", true)
                        seeMoreIntent.putExtra("name", "$NAME")
                        seeMorePendingIntent = PendingIntent.getActivity(
                            applicationContext, 0, seeMoreIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        notificationManager.sendNotification()
                        Toast.makeText(
                            applicationContext,
                            "Download Success",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    DownloadManager.STATUS_FAILED -> {
                        custom_button.buttonState = ButtonState.Completed
                        seeMoreIntent.putExtra("status", false)
                        seeMoreIntent.putExtra("name", "$NAME")
                        seeMorePendingIntent = PendingIntent.getActivity(
                            applicationContext, 0, seeMoreIntent,
                            PendingIntent.FLAG_ONE_SHOT
                        )
                        notificationManager.sendNotification()
                        Toast.makeText(
                            applicationContext,
                            "Failed to download ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
            cursor.close()

        }
    }

    private fun download() {
        custom_button.buttonState = ButtonState.Loading
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        URL?.let { Log.i("activityMain", it) }
    }

    fun NotificationManager.sendNotification() {

        val builder = NotificationCompat.Builder(
            applicationContext,
            getString(R.string.notification_channel)
        ).setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_description))
            .setContentIntent(seeMorePendingIntent).addAction(
                R.drawable.ic_assistant_black_24dp,
                getString(R.string.notification_button), seeMorePendingIntent
            ).setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH)

        notify(0, builder.build())
    }

    companion object {
        private var URL: String? = null
        private var NAME: String? = null
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
                .apply {
                    notificationChannel.setShowBadge(false)
                }
            notificationChannel.description = getString(R.string.notification_description)
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
