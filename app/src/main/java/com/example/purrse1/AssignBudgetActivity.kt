package com.example.purrse1

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class AssignBudgetActivity : AppCompatActivity() {
    private lateinit var btnSelectCategory: Button
    private lateinit var editBudgetAmount: EditText
    private lateinit var btnSaveBudget: Button
    private lateinit var editTextMinBudget: EditText
    private lateinit var editTextMaxBudget: EditText
    private lateinit var ivCat: ImageView

    private var selectedCategory: String? = null
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_budget)

        ivCat = findViewById(R.id.ivCat)

        btnSelectCategory = findViewById(R.id.btnSelectCategory)
        editBudgetAmount = findViewById(R.id.editBudgetAmount)
        btnSaveBudget = findViewById(R.id.btnSaveBudget)
        editTextMinBudget = findViewById(R.id.editTextMinBudget)
        editTextMaxBudget = findViewById(R.id.editTextMaxBudget)

        btnSelectCategory.setOnClickListener {
            val intent = Intent(this, CategorySelectorActivity::class.java)
            startActivityForResult(intent, 1001)
        }

        btnSaveBudget.setOnClickListener {
            saveBudgetToFirebase()
        }
    }

    private fun saveBudgetToFirebase() {
        val amountText = editBudgetAmount.text.toString().trim()
        val uid = auth.currentUser?.uid
        val minBudget = editTextMinBudget.text.toString().toDoubleOrNull() ?: 0.0
        val maxBudget = editTextMaxBudget.text.toString().toDoubleOrNull()

        if (maxBudget == null || maxBudget < minBudget) {
            Toast.makeText(this, "Please enter valid min and max values", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCategory.isNullOrEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        if (amountText.isEmpty()) {
            Toast.makeText(this, "Please enter a budget amount", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount < 0) {
            Toast.makeText(this, "Enter a valid budget amount", Toast.LENGTH_SHORT).show()
            return
        }

        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val budgetData = mapOf(
            "category" to selectedCategory,
            "amount" to amount,
            "minBudget" to minBudget,
            "maxBudget" to maxBudget
        )

        database.child("users").child(uid).child("budgets").child(selectedCategory!!)
            .setValue(budgetData)
            .addOnSuccessListener {
                Toast.makeText(this, "Budget assigned successfully", Toast.LENGTH_SHORT).show()
                BadgeUtils().checkAndUnlockPawsomeStart()
                BadgeUtils().checkCategoryCommanderBadge()
                BadgeUtils().checkAndUnlockEmergencyExpert(selectedCategory)
                finish()


            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save budget: ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val category = data?.getSerializableExtra("selectedCategory") as? Category
            if (category != null) {
                selectedCategory = category.name
                btnSelectCategory.text = "${category.icon} ${category.name}"
            }
        }
    }
}