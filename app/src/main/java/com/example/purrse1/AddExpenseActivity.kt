package com.example.purrse1

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.UUID
 import com.google.firebase.database.ValueEventListener

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var etAmount: EditText
    private lateinit var etDescription: EditText
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedCategory: TextView
    private lateinit var ivReceipt: ImageView
    private lateinit var btnSelectCategory: Button
    private var selectedDate: String = ""
    private var selectedCategory: String = ""
    private var imageUri: Uri? = null
    private lateinit var ivCat: ImageView
    private val categoryList = mutableListOf<String>()
    private val storageRef = FirebaseStorage.getInstance().reference
    private val dbRef = FirebaseDatabase.getInstance().getReference("users")
    private val storagePermissionCode = 101

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            ivReceipt.visibility = View.VISIBLE
            ivReceipt.setImageURI(uri)
        }
    }

    private val categoryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selected = result.data?.getSerializableExtra("selectedCategory") as? Category
            if (selected != null) {
                selectedCategory = selected.name
                tvSelectedCategory.text = "${selected.icon} ${selected.name}"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        ivCat = findViewById(R.id.ivCat)

        etAmount = findViewById(R.id.etAmount)
        etDescription = findViewById(R.id.etDescription)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        tvSelectedCategory = findViewById(R.id.tvSelectedCategory)
        ivReceipt = findViewById(R.id.ivReceipt)

        btnSelectCategory = findViewById(R.id.btnSelectCategory)


        loadCategories()

        findViewById<Button>(R.id.btnSelectDate).setOnClickListener { pickDate() }
        findViewById<Button>(R.id.btnSelectCategory).setOnClickListener { val intent = Intent(this, CategorySelectorActivity::class.java)
            categoryLauncher.launch(intent) }
        findViewById<Button>(R.id.btnSelectImage).setOnClickListener { requestStoragePermission() }
        findViewById<Button>(R.id.btnSubmit).setOnClickListener { submitExpense() }


    }

    private fun showCategoryDialog() {
        if (categoryList.isEmpty()) {
            Toast.makeText(this, "No categories found", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Category")

        val categoriesArray = categoryList.toTypedArray()

        builder.setItems(categoriesArray) { _, which ->
            selectedCategory = categoriesArray[which]
            btnSelectCategory.text = selectedCategory
        }

        builder.show()
    }

    private fun loadCategories() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val categoryRef = dbRef.child(uid).child("categories")

        categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.clear()
                for (catSnap in snapshot.children) {
                    val categoryName = catSnap.child("name").getValue(String::class.java)
                    if (!categoryName.isNullOrEmpty()) {
                        categoryList.add(categoryName)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddExpenseActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun pickDate() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            selectedDate = "$year-${month + 1}-$day"
            tvSelectedDate.text = selectedDate
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun selectCategory() {
        categoryLauncher.launch(Intent(this, CategorySelectorActivity::class.java))
    }

    private fun requestStoragePermission() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                storagePermissionCode
            )
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        pickImage.launch("image/*")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == storagePermissionCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitExpense() {
        val amount = etAmount.text.toString().toDoubleOrNull()
        val description = etDescription.text.toString()

        if (amount == null || selectedCategory.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val expenseId = dbRef.child(uid).child("expenses").push().key!!

        if (imageUri != null) {
            val receiptRef = storageRef.child("receipts/$uid/$expenseId.jpg")
            receiptRef.putFile(imageUri!!)
                .continueWithTask { task ->
                    if (!task.isSuccessful) throw task.exception!!
                    receiptRef.downloadUrl
                }
                .addOnSuccessListener { uri ->
                    saveExpenseToDatabase(uid, expenseId, amount, description, selectedCategory, selectedDate, uri.toString())
                }
        } else {
            saveExpenseToDatabase(uid, expenseId, amount, description, selectedCategory, selectedDate, "")
        }
    }

    private fun saveExpenseToDatabase(
        uid: String,
        expenseId: String,
        amount: Double,
        desc: String,
        category: String,
        date: String,
        imageUrl: String
    ) {
        val expense = mapOf(
            "amount" to amount,
            "description" to desc,
            "category" to category,
            "date" to date,
            "imageUrl" to imageUrl
        )

        dbRef.child(uid).child("expenses").child(expenseId).setValue(expense)
            .addOnSuccessListener {
                Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}