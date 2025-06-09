package com.example.purrse1

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Date
import java.util.Locale

class SetIncomeActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var ivCat: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_income)

        ivCat = findViewById(R.id.ivCat)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val incomeEditText = findViewById<EditText>(R.id.incomeEditText)
        val saveIncomeButton = findViewById<Button>(R.id.saveIncomeButton)

        saveIncomeButton.setOnClickListener {
            val incomeStr = incomeEditText.text.toString()
            if (incomeStr.isEmpty()) {
                Toast.makeText(this, "Please enter income", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val income = incomeStr.toDouble()
            val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            database.child("users").child(userId).child("income").child(currentMonth).setValue(income)
                .addOnSuccessListener {
                    Toast.makeText(this, "Income saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save income", Toast.LENGTH_SHORT).show()
                }
        }
    }
}