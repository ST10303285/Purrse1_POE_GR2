package com.example.purrse1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var ivCat: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ivCat = findViewById(R.id.ivCat)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)


            val continueBtn = findViewById<Button>(R.id.continueBtn)

            continueBtn.setOnClickListener{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

            }
            insets
        }
        Toast.makeText(this, "WelcomeActivity started", Toast.LENGTH_SHORT).show()
    }
}