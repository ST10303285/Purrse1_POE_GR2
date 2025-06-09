package com.example.purrse1

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.Locale
import com.google.firebase.database.ValueEventListener
import kotlin.collections.getValue
import kotlin.text.format
import kotlin.text.get

class HomeActivity : AppCompatActivity() {
    private lateinit var tvDate: TextView
    private lateinit var tvIncome: TextView
    private lateinit var tvExpense: TextView
    private lateinit var tvBalance: TextView
    private lateinit var pieChart: PieChart
    private lateinit var topCategoriesLayout: LinearLayout
    private lateinit var ivCat: ImageView
    private lateinit var savingsText: TextView


    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val database = FirebaseDatabase.getInstance().getReference("users").child(userId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        ivCat = findViewById(R.id.ivCat)

        tvDate = findViewById(R.id.tvDate)
        tvIncome = findViewById(R.id.tvIncome)
        tvExpense = findViewById(R.id.tvExpense)
        tvBalance = findViewById(R.id.tvBalance)
        pieChart = findViewById(R.id.budgetPieChart)
        topCategoriesLayout = findViewById(R.id.topCategoriesLayout)
        savingsText = findViewById(R.id.savingsText)

        tvIncome.setOnClickListener {
            startActivity(Intent(this, SetIncomeActivity::class.java))
        }

        displayCurrentDate()
        loadSummaryData()
        loadBudgetPieChart()
        loadTopSpendingCategories()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_home // set selected tab

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_add -> {
                    startActivity(Intent(this, AddExpenseActivity::class.java))
                    true
                }
                R.id.nav_budget -> {
                    startActivity(Intent(this, BudgetActivity::class.java))
                    true
                }
                R.id.nav_trends -> {
                    startActivity(Intent(this, TrendsActivity::class.java))
                    true
                }
                R.id.nav_game -> {
                    startActivity(Intent(this, AchievementsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun displayCurrentDate() {
        val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        tvDate.text = "Today: $date"
    }

    private fun loadSummaryData() {

        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        // Fetch income
        database.child("income").child(currentMonth).get().addOnSuccessListener { incomeSnap ->
            val totalIncome = incomeSnap.getValue(Double::class.java) ?: 0.0

            // Fetch expenses
            database.child("expenses").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalExpense = 0.0
                    for (expenseSnap in snapshot.children) {
                        val amount = expenseSnap.child("amount").getValue(Double::class.java) ?: 0.0
                        totalExpense += amount
                    }
                    val balance = totalIncome - totalExpense

                    tvIncome.text = "Income\nR%.2f".format(totalIncome)
                    tvExpense.text = "Expenses\nR%.2f".format(totalExpense)
                    tvBalance.text = "Balance\nR%.2f".format(balance)

                    // Fetch total budget to calculate savings
                    database.child("totalBudget").get().addOnSuccessListener { budgetSnap ->
                        val totalBudget = budgetSnap.getValue(Double::class.java) ?: 0.0
                        val savings = totalIncome - totalBudget
                        savingsText.text = "Savings\nR%.2f".format(savings)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }


    private fun loadBudgetPieChart() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        database.child("users").child(userId).child("budgets")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(budgetSnap: DataSnapshot) {
                    val entries = ArrayList<PieEntry>()

                    // Loop through each budget category
                    for (budget in budgetSnap.children) {
                        val budgetObj = budget.getValue(Budget::class.java) ?: continue
                        val category = budgetObj.category
                        val budgetAmount = budgetObj.amount

                        // Now load expenses to compute spending
                        database.child("users").child(userId).child("expenses")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(expenseSnap: DataSnapshot) {
                                    var spent = 0.0

                                    for (expense in expenseSnap.children) {
                                        val cat = expense.child("category").getValue(String::class.java) ?: ""
                                        if (cat == category) {
                                            spent += expense.child("amount").getValue(Double::class.java) ?: 0.0
                                        }
                                    }

                                    val remaining = budgetAmount - spent
                                    entries.add(PieEntry(spent.toFloat(), "$category (Spent)"))
                                    entries.add(PieEntry(remaining.toFloat(), "$category (Remaining)"))

                                    // Only update the chart after the last budget is processed (optional improvement)
                                    val dataSet = PieDataSet(entries, "Budget Usage")
                                    dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
                                    pieChart.data = PieData(dataSet)
                                    pieChart.invalidate()
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadTopSpendingCategories() {
        database.child("expenses").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryTotals = mutableMapOf<String, Double>()

                for (expense in snapshot.children) {
                    val category = expense.child("category").getValue(String::class.java) ?: continue
                    val amount = expense.child("amount").getValue(Double::class.java) ?: 0.0

                    if (category.lowercase() != "salary" && category.lowercase() != "income") {
                        categoryTotals[category] = categoryTotals.getOrDefault(category, 0.0) + amount
                    }
                }

                val top3 = categoryTotals.toList().sortedByDescending { it.second }.take(3)

                topCategoriesLayout.removeAllViews()
                for ((category, amount) in top3) {
                    val textView = TextView(this@HomeActivity)
                    textView.text = "$category: R%.2f".format(amount)
                    textView.setPadding(8, 8, 8, 8)
                    topCategoriesLayout.addView(textView)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


}