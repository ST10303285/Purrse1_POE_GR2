package com.example.purrse1

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class CreateCategoryActivity : AppCompatActivity() {
    private lateinit var etCategoryName: EditText
    private lateinit var spinnerIcon: Spinner
    private lateinit var spinnerColor: Spinner
    private lateinit var btnSave: Button

    private lateinit var ivCat: ImageView


    private val icons = listOf("🍔", "🏠", "🚗", "🎁", "🎮", "☕", "📶", "🏋️", "✈️", "💼", "📚")
    private val colors = mapOf(
        "Red" to "#F44336",
        "Green" to "#4CAF50",
        "Blue" to "#2196F3",
        "Orange" to "#FF9800",
        "Purple" to "#9C27B0",
        "Pink" to "#E91E63",
        "Teal" to "#009688",
        "Brown" to "#795548"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_category)

        ivCat = findViewById(R.id.ivCat)

        etCategoryName = findViewById(R.id.etCategoryName)
        spinnerIcon = findViewById(R.id.spinnerIcon)
        spinnerColor = findViewById(R.id.spinnerColor)
        btnSave = findViewById(R.id.btnSaveCategory)

        // Set up spinners
        spinnerIcon.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, icons)
        spinnerColor.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors.keys.toList())

        btnSave.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            val icon = spinnerIcon.selectedItem.toString()
            val colorName = spinnerColor.selectedItem.toString()
            val colorHex = colors[colorName] ?: "#000000"

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveCategoryToFirebase(name, icon, colorHex)
        }
    }

    private fun saveCategoryToFirebase(name: String, icon: String, color: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid/categories")
        val categoryId = ref.push().key ?: return

        val categoryData = mapOf(
            "name" to name,
            "icon" to icon,
            "color" to color
        )

        ref.child(categoryId).setValue(categoryData)
            .addOnSuccessListener {
                Toast.makeText(this, "Category saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save category", Toast.LENGTH_SHORT).show()
            }
    }
}