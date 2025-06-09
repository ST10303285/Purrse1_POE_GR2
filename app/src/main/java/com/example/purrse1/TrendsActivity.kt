package com.example.purrse1

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Date
import java.util.Locale
import com.google.firebase.database.ValueEventListener
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarEntry.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.Calendar
import kotlin.collections.getValue
import kotlin.collections.plusAssign

class TrendsActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var pieChart: PieChart
    private lateinit var tvDailyExpenses: TextView
    private lateinit var layoutRecentExpenses: LinearLayout
    private lateinit var btnViewMore: Button
    private lateinit var barChart: BarChart
    private lateinit var ivCat: ImageView

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trends)

        ivCat = findViewById(R.id.ivCat)
        database =
            FirebaseDatabase.getInstance().getReference("users").child(userId).child("expenses")

        tvDailyExpenses = findViewById(R.id.tvDailyExpenses)
        layoutRecentExpenses = findViewById(R.id.layoutRecentExpenses)
        btnViewMore = findViewById(R.id.btnViewMore)
        barChart = findViewById(R.id.barChart)


        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1) // months 0-based

        val currentMonth = "$year-$month"

        checkIfUserStayedWithinBudget(userId, currentMonth)

        loadDailySpending()
        loadRecentExpenses()
        loadCategoryBarChart()

        btnViewMore.setOnClickListener {
            startActivity(Intent(this, AllExpensesActivity::class.java))
        }

        val btnMonthlyComparison = findViewById<Button>(R.id.btnMonthlyComparison)
        btnMonthlyComparison.setOnClickListener {
            startActivity(Intent(this, MonthlyComparisonActivity::class.java))
        }
    }

    private fun loadDailySpending() {
        val calendar = Calendar.getInstance()
        val today = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalToday = 0.0

                for (snap in snapshot.children) {
                    val date = snap.child("date").getValue(String::class.java) ?: continue
                    val amount = snap.child("amount").getValue(Double::class.java) ?: 0.0

                    if (date == today) {
                        totalToday += amount
                    }
                }
                tvDailyExpenses.text =" R${totalToday.toInt()}"
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun loadRecentExpenses() {
        database.orderByChild("date").limitToLast(10)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryTotals = mutableMapOf<String, Double>()

                    for (snap in snapshot.children) {
                        val category =
                            snap.child("category").getValue(String::class.java) ?: continue
                        val amount = snap.child("amount").getValue(Double::class.java) ?: 0.0
                        categoryTotals[category] = (categoryTotals[category] ?: 0.0) + amount
                    }

                    val top3 = categoryTotals.entries.sortedByDescending { it.value }.take(3)

                    layoutRecentExpenses.removeAllViews()
                    top3.forEach { entry ->
                        val tv = TextView(this@TrendsActivity)
                        tv.text = "${entry.key}: R%.2f".format(entry.value)
                        layoutRecentExpenses.addView(tv)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadCategoryBarChart() {
        val expenseRef = FirebaseDatabase.getInstance()
            .getReference("users").child(userId).child("expenses")

        val budgetRef = FirebaseDatabase.getInstance()
            .getReference("users").child(userId).child("budgets")

        expenseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(expenseSnap: DataSnapshot) {
                val categorySpend = mutableMapOf<String, Double>()

                for (snap in expenseSnap.children) {
                    val category = snap.child("category").getValue(String::class.java) ?: continue
                    val amount = snap.child("amount").getValue(Double::class.java) ?: 0.0
                    categorySpend[category] = (categorySpend[category] ?: 0.0) + amount
                }

                budgetRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(budgetSnap: DataSnapshot) {
                        val entries = mutableListOf<BarEntry>()
                        val labels = mutableListOf<String>()
                        var index = 0f

                        for ((category, spentAmount) in categorySpend) {
                            val minGoal = budgetSnap.child(category).child("minBudget").getValue(Double::class.java) ?: 0.0
                            val maxGoal = budgetSnap.child(category).child("maxBudget").getValue(Double::class.java) ?: 0.0

                            val minGoalF = minGoal.toFloat()
                            val spentMinusMin = (spentAmount - minGoal).coerceAtLeast(0.0).toFloat()
                            val maxMinusSpent = (maxGoal - spentAmount).coerceAtLeast(0.0).toFloat()

                            val barEntry = BarEntry(
                                index, floatArrayOf(
                                    minGoalF,
                                    spentMinusMin,
                                    maxMinusSpent
                                )
                            )
                            entries.add(barEntry)
                            labels.add(category)
                            index += 1
                        }

                        val dataSet = BarDataSet(entries, "Budget Analysis")
                        dataSet.setColors(
                            ColorTemplate.rgb("#A5D6A7"), // Min (green)
                            ColorTemplate.rgb("#FFCC80"), // Spent above min (orange)
                            ColorTemplate.rgb("#EF9A9A")  // Overspent buffer (red)
                        )
                        dataSet.stackLabels = arrayOf("Min Goal", "Spending", "Overspending")

                        val data = BarData(dataSet)
                        data.setValueTextSize(10f)
                        data.barWidth = 0.9f

                        barChart.data = data
                        barChart.description.isEnabled = false
                        barChart.setFitBars(true)

                        val xAxis = barChart.xAxis
                        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.granularity = 1f
                        xAxis.setDrawGridLines(false)

                        barChart.axisRight.isEnabled = false
                        barChart.invalidate()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun checkIfUserStayedWithinBudget(uid: String, month: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

        // 1. Get all budgets for user
        dbRef.child("budgets").get().addOnSuccessListener { budgetsSnapshot ->

            var totalBudget = 0.0
            for (budgetSnap in budgetsSnapshot.children) {
                val amount = budgetSnap.child("amount").getValue(Double::class.java) ?: 0.0
                totalBudget += amount
            }

            // 2. Get all expenses for user for the specified month
            dbRef.child("expenses").orderByChild("date").startAt("$month-01").endAt("$month-31").get()
                .addOnSuccessListener { expensesSnapshot ->

                    var totalExpenses = 0.0
                    for (expenseSnap in expensesSnapshot.children) {
                        val amount = expenseSnap.child("amount").getValue(Double::class.java) ?: 0.0
                        totalExpenses += amount
                    }

                    // 3. Check if user stayed within budget
                    if (totalExpenses <= totalBudget) {
                        // Unlock badge
                        BadgeUtils().unlockBadge("Budget Buddy")
                    }
                }
        }
    }

    private fun calculateSavings() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("users/$userId")

        dbRef.get().addOnSuccessListener { snapshot ->
            val salary = snapshot.child("salary").getValue(Float::class.java) ?: 0f
            val budgets = snapshot.child("budgets")

            var totalBudget = 0f
            for (budget in budgets.children) {
                totalBudget += budget.child("amount").getValue(Float::class.java) ?: 0f
            }

            val savings = salary - totalBudget

            findViewById<TextView>(R.id.savingsText).text =
                "Savings: R%.2f".format(savings)
        }
    }
}