package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {
    private lateinit var downloadData: Bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        downloadData = intent.extras!!
        val status = downloadData.getBoolean("status")
        val name = downloadData.getString("name")
        val nameTextView = findViewById<TextView>(R.id.nameText)
        val statusTextView = findViewById<TextView>(R.id.statusText)
        val okButton = findViewById<TextView>(R.id.okButton)
        okButton.setOnClickListener{

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
