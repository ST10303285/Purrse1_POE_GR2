package com.example.purrse1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class BudgetActivity : AppCompatActivity() {
    private lateinit var assignBudgetButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTotalBudget: EditText
    private lateinit var btnSaveTotalBudget: Button
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private lateinit var ivCat: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        ivCat = findViewById(R.id.ivCat)

        recyclerView = findViewById(R.id.rvCategoryBudgets)
        recyclerView.layoutManager = LinearLayoutManager(this)


        btnSaveTotalBudget = findViewById(R.id.btnSaveTotalBudget)
        editTotalBudget = findViewById(R.id.editTotalBudget)
        assignBudgetButton = findViewById(R.id.btnAssignBudget)

        loadCategoryBudgetsAndUsage()

        assignBudgetButton.setOnClickListener {
            startActivity(Intent(this, AssignBudgetActivity::class.java))
        }

        btnSaveTotalBudget.setOnClickListener {
            saveTotalBudget()
        }

        loadTotalBudget()
    }


    private fun loadTotalBudget() {
        val uid = auth.currentUser?.uid ?: return
        database.child("users").child(uid).child("totalBudget").get()
            .addOnSuccessListener { snapshot ->
                val totalBudget = snapshot.getValue(Double::class.java)
                if (totalBudget != null) {
                    editTotalBudget.setText(totalBudget.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load total budget", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveTotalBudget() {
        val totalBudgetText = editTotalBudget.text.toString().trim()
        val uid = auth.currentUser?.uid ?: return

        val totalBudget = totalBudgetText.toDoubleOrNull()
        if (totalBudget == null || totalBudget < 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        database.child("users").child(uid).child("totalBudget").setValue(totalBudget)
            .addOnSuccessListener {
                Toast.makeText(this, "Total budget saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save total budget", Toast.LENGTH_SHORT).show()
            }
    }


    private fun loadCategoryBudgetsAndUsage() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().getReference("users/$uid")

        val budgetsRef = userRef.child("budgets")
        val expensesRef = userRef.child("expenses")

        budgetsRef.get().addOnSuccessListener { budgetSnapshot ->
            val budgetMap = mutableMapOf<String, Double>()

            for (categorySnap in budgetSnapshot.children) {
                val category = categorySnap.key ?: continue
                val amount = categorySnap.child("amount").getValue(Double::class.java) ?: 0.0
                budgetMap[category] = amount
            }

            expensesRef.get().addOnSuccessListener { expenseSnapshot ->
                val usageMap = mutableMapOf<String, Double>()

                for (expenseSnap in expenseSnapshot.children) {
                    val category = expenseSnap.child("category").getValue(String::class.java) ?: continue
                    val amount = expenseSnap.child("amount").getValue(Double::class.java) ?: 0.0
                    usageMap[category] = (usageMap[category] ?: 0.0) + amount
                }

                val categoryBudgets = budgetMap.map { (category, assigned) ->
                    val used = usageMap[category] ?: 0.0
                    CategoryBudgetUsage(category, assigned, used)
                }

                recyclerView.adapter = CategoryBudgetAdapter(categoryBudgets)
            }
        }
    }

}