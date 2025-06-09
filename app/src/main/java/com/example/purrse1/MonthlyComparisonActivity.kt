package com.example.purrse1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.database.ValueEventListener

class MonthlyComparisonActivity : AppCompatActivity() {
    private lateinit var barChart: BarChart
    private lateinit var database: DatabaseReference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_comparison)

        barChart = findViewById(R.id.barChart)
        database = FirebaseDatabase.getInstance().getReference("users").child(userId)

        loadMonthlyExpenses()
    }

    private fun loadMonthlyExpenses() {
        database.child("expenses").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val monthTotals = mutableMapOf<String, Double>()

                for (expense in snapshot.children) {
                    val date = expense.child("date").getValue(String::class.java) ?: continue
                    val amount = expense.child("amount").getValue(Double::class.java) ?: 0.0

                    val month = date.take(7) // yyyy-MM
                    monthTotals[month] = monthTotals.getOrDefault(month, 0.0) + amount
                }

                val entries = mutableListOf<BarEntry>()
                val labels = monthTotals.keys.sorted()

                labels.forEachIndexed { index, month ->
                    val value = monthTotals[month]?.toFloat() ?: 0f
                    entries.add(BarEntry(index.toFloat(), value))
                }

                val dataSet = BarDataSet(entries, "Monthly Expenses")
                dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

                barChart.data = BarData(dataSet)
                barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                barChart.xAxis.granularity = 1f
                barChart.xAxis.isGranularityEnabled = true
                barChart.description.text = "Expenses per Month"
                barChart.animateY(1000)
                barChart.invalidate()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}