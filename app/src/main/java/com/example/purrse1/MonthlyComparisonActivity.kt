package com.example.purrse1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Locale

class MonthlyComparisonActivity : AppCompatActivity() {
    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_comparison)

        barChart = findViewById(R.id.monthlyBarChart)
        loadMonthlyExpenses()
    }

    private fun loadMonthlyExpenses() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("expenses/$userId")

        dbRef.get().addOnSuccessListener { snapshot ->
            val monthlyTotals = mutableMapOf<String, Float>()

            for (expenseSnap in snapshot.children) {
                val date = expenseSnap.child("date").getValue(String::class.java) ?: continue
                val amount = expenseSnap.child("amount").getValue(Float::class.java) ?: 0f

                val month = try {
                    SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)!!
                    )
                } catch (e: Exception) {
                    continue
                }

                monthlyTotals[month] = monthlyTotals.getOrDefault(month, 0f) + amount
            }

            showBarChart(monthlyTotals)
        }
    }

    private fun showBarChart(data: Map<String, Float>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        data.entries.sortedBy { it.key }.forEachIndexed { index, entry ->
            entries.add(BarEntry(index.toFloat(), entry.value))
            labels.add(entry.key)
        }

        val dataSet = BarDataSet(entries, "Monthly Expenses")
        dataSet.color = ContextCompat.getColor(this, R.color.holo_pink_light)

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.granularity = 1f
        barChart.xAxis.setDrawLabels(true)
        barChart.animateY(1000)
        barChart.invalidate()
    }
}