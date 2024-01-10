package com.example.chamber_3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        val clickableText = findViewById<TextView>(R.id.button)
        clickableText.setOnClickListener{
            val intent = Intent(this@MainActivity4, MainActivity3::class.java)
            startActivity(intent)

        }
    }
}