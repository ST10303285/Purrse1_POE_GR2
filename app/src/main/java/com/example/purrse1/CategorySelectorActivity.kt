package com.example.purrse1

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.ValueEventListener

class CategorySelectorActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var btnCreateNew: Button
    private val categoryList = mutableListOf<Category>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listItems: MutableList<String>
    private lateinit var ivCat: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_selector)

        ivCat = findViewById(R.id.ivCat)

        listView = findViewById(R.id.listViewCategories)
        btnCreateNew = findViewById(R.id.btnCreateCategory)

        listItems = mutableListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter

        categoryList.addAll(getDefaultCategories())
        listItems.addAll(getDefaultCategories().map { "${it.icon} ${it.name}" })
        adapter.notifyDataSetChanged()

        listItems = mutableListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter

        loadDefaultCategories()
        loadCustomCategoriesFromFirebase()

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categoryList[position]
            val resultIntent = Intent()
            resultIntent.putExtra("selectedCategory", selectedCategory)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        btnCreateNew.setOnClickListener {
            startActivity(Intent(this, CreateCategoryActivity::class.java))
        }
    }

    private fun loadDefaultCategories() {
        val defaults = listOf(
            Category("Food", "🍔", "#F44336"),
            Category("Salary", "💼", "#4CAF50"),
            Category("Groceries", "🛒", "#FF9800"),
            Category("Petrol", "⛽", "#2196F3"),
            Category("Rent", "🏠", "#9C27B0"),
            Category("Gym", "🏋️", "#E91E63"),
            Category("Takeouts", "🥡", "#009688"),
            Category("Vacation", "🏖️", "#FF5722"),
            Category("Travel", "✈️", "#3F51B5"),
            Category("Gifts", "🎁", "#8BC34A"),
            Category("Investments", "📈", "#607D8B"),
            Category("Savings", "💰", "#795548"),
            Category("Entertainment", "🎮", "#00BCD4"),
            Category("Coffee", "☕", "#FFC107"),
            Category("Internet", "📶", "#673AB7"),
            Category("Taxi", "🚕", "#9E9E9E")
        )
        categoryList.addAll(defaults)
        listItems.addAll(defaults.map { "${it.icon} ${it.name}" })
        adapter.notifyDataSetChanged()
    }

    private fun loadCustomCategoriesFromFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid/categories")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.removeAll { it !in getDefaultCategories() }
                listItems.clear()
                listItems.addAll(getDefaultCategories().map { "${it.icon} ${it.name}" })

                for (categorySnap in snapshot.children) {
                    val category = categorySnap.getValue(Category::class.java)
                    if (category != null) {
                        categoryList.add(category)
                        listItems.add("${category.icon} ${category.name}")
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CategorySelectorActivity, "Failed to load custom categories", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun getDefaultCategories(): List<Category> {
        return listOf(
            Category("Food", "🍔", "#F44336"),
            Category("Salary", "💼", "#4CAF50"),
            Category("Groceries", "🛒", "#FF9800"),
            Category("Petrol", "⛽", "#2196F3"),
            Category("Rent", "🏠", "#9C27B0"),
            Category("Gym", "🏋️", "#E91E63"),
            Category("Takeouts", "🥡", "#009688"),
            Category("Vacation", "🏖️", "#FF5722"),
            Category("Travel", "✈️", "#3F51B5"),
            Category("Gifts", "🎁", "#8BC34A"),
            Category("Investments", "📈", "#607D8B"),
            Category("Savings", "💰", "#795548"),
            Category("Entertainment", "🎮", "#00BCD4"),
            Category("Coffee", "☕", "#FFC107"),
            Category("Internet", "📶", "#673AB7"),
            Category("Taxi", "🚕", "#9E9E9E")
        )
    }
}