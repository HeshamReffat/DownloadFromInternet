package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {
    private lateinit var downloadData: Bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        downloadData = intent.extras!!
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()
        val status = downloadData.getBoolean("status")
        val name = downloadData.getString("name")
        val nameTextView = findViewById<TextView>(R.id.nameText)
        val statusTextView = findViewById<TextView>(R.id.statusText)
        val okButton = findViewById<Button>(R.id.okButton)
        okButton.setOnClickListener{
            Log.i("backButton","Presssssssed")
            //Todo we call finish to navigate up to the main activity
            finish()
        }
        nameTextView.text = name
        if(status){
            statusTextView.setTextColor(Color.GREEN)
            statusTextView.text = "Success"
        }else{
            statusTextView.setTextColor(Color.RED)
            statusTextView.text = "Failed"
        }
    }
}
