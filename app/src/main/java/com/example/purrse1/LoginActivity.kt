package com.example.purrse1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth


    class LoginActivity : AppCompatActivity() {

        private lateinit var auth: FirebaseAuth
        private lateinit var ivCat: ImageView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_login)

            ivCat = findViewById(R.id.ivCat)

            auth = FirebaseAuth.getInstance()

            val emailEditText = findViewById<EditText>(R.id.emailEditText)
            val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
            val loginButton = findViewById<Button>(R.id.loginButton)
            val goToRegister = findViewById<TextView>(R.id.goToRegisterText)

            loginButton.setOnClickListener {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            goToRegister.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }

