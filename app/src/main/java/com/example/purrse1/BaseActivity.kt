package com.example.purrse1

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.bumptech.glide.Glide

open class BaseActivity : AppCompatActivity() {

    protected lateinit var ivCat: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Don't call setContentView here, derived activities do that
    }

    override fun onStart() {
        super.onStart()
        ivCat = findViewById(R.id.ivCat)
    }

    private fun loadUserCatImage() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

        userRef.child("catLevel").get().addOnSuccessListener { snapshot ->
            val catLevel = snapshot.getValue(Int::class.java) ?: 1
            Log.d("CAT_DEBUG", "Loaded cat level: $catLevel")

            val catDrawable = CatImageUtils.getCatDrawableRes(catLevel)
            Glide.with(this).load(catDrawable).into(ivCat)
        }.addOnFailureListener {
            Log.e("CAT_DEBUG", "Failed to get cat level")
            Glide.with(this).load(R.drawable.cat1).into(ivCat)
        }
    }
}