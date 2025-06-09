package com.example.purrse1

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date
import java.util.Locale

class AllExpensesActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var rvAllExpenses: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerSort: Spinner
    private lateinit var spinnerDateSort: Spinner
    private lateinit var tvDateRange: TextView
    private var startDateFilter: String? = null
    private var endDateFilter: String? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var ivCat: ImageView

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val allExpenses = mutableListOf<Expense>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_expenses)

        ivCat = findViewById(R.id.ivCat)

        rvAllExpenses = findViewById(R.id.rvAllExpenses)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerSort = findViewById(R.id.spinnerSort)
        spinnerDateSort= findViewById(R.id.spinnerDateSort)

        rvAllExpenses.layoutManager = LinearLayoutManager(this)
        expenseAdapter = ExpenseAdapter(allExpenses)
        rvAllExpenses.adapter = expenseAdapter

        database = FirebaseDatabase.getInstance().getReference("users").child(userId).child("expenses")

        setupFilters()
        loadAllExpenses()

        tvDateRange = findViewById(R.id.tvDateRange)

        tvDateRange.setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun showDateRangePicker() {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Date Range")
                .build()

        dateRangePicker.show(supportFragmentManager, "date_range_picker")

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startMillis = selection.first
            val endMillis = selection.second

            val startDate = dateFormat.format(Date(startMillis))
            val endDate = dateFormat.format(Date(endMillis))

            startDateFilter = startDate
            endDateFilter = endDate

            tvDateRange.text = "$startDate to $endDate"
            applyFilters()
        }
    }

    private fun setupFilters() {
        val sortOptions = listOf("Sort Amount:None", "Amount ↑", "Amount ↓")
        spinnerSort.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortOptions)

        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilters()
            }
        }

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilters()
            }
        }
        val dateSortOptions = listOf("Sort Date: None", "Newest First", "Oldest First")
        spinnerDateSort.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dateSortOptions)

        spinnerDateSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilters()
            }
        }
    }

    private fun loadAllExpenses() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allExpenses.clear()
                val categories = mutableSetOf<String>()

                for (snap in snapshot.children) {
                    val expense = snap.getValue(Expense::class.java)
                    if (expense != null) {
                        allExpenses.add(expense)
                        categories.add(expense.category)
                    }
                }

                val categoryOptions = listOf("All") + categories.toList()
                spinnerCategory.adapter = ArrayAdapter(this@AllExpensesActivity, android.R.layout.simple_spinner_dropdown_item, categoryOptions)

                applyFilters()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun applyFilters() {
        val selectedCategory = spinnerCategory.selectedItem?.toString() ?: "All"
        val selectedSort = spinnerSort.selectedItem?.toString() ?: "None"
        val dateSort = spinnerDateSort.selectedItem?.toString() ?: "None"


        var filtered = allExpenses.filter {
            selectedCategory == "All" || it.category == selectedCategory
        }

        filtered = when (selectedSort) {
            "Amount ↑" -> filtered.sortedBy { it.amount }
            "Amount ↓" -> filtered.sortedByDescending { it.amount }
            else -> filtered
        }

        filtered = when (dateSort) {
            "Newest First" -> filtered.sortedByDescending { it.date }
            "Oldest First" -> filtered.sortedBy { it.date }
            else -> filtered
        }

        startDateFilter?.let { start ->
            val startDate = dateFormat.parse(start)
            filtered = filtered.filter { dateFormat.parse(it.date)?.after(startDate) != false || it.date == start }
        }

        endDateFilter?.let { end ->
            val endDate = dateFormat.parse(end)
            filtered = filtered.filter { dateFormat.parse(it.date)?.before(endDate) != false || it.date == end }
        }

        expenseAdapter.updateData(filtered)
    }
}