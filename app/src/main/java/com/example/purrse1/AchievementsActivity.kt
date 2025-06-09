package com.example.purrse1

import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AchievementsActivity : AppCompatActivity() {
    private lateinit var badgeRecyclerView: RecyclerView
    private lateinit var badgeAdapter: BadgeAdapter
    private lateinit var badgeList: MutableList<Badge>
    private lateinit var catLevelText: TextView
    private lateinit var catLevelImage: ImageView
    private lateinit var catXpProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        badgeRecyclerView = findViewById(R.id.badgeRecyclerView)
        catLevelText = findViewById(R.id.catLevelText)
        catLevelImage = findViewById(R.id.catLevelImage)
        catXpProgress = findViewById(R.id.catXpProgress)

        badgeList = mutableListOf()
        badgeAdapter = BadgeAdapter(badgeList)

        badgeRecyclerView.layoutManager = GridLayoutManager(this, 2)
        badgeRecyclerView.adapter = badgeAdapter

        loadUserAchievements()
    }

    fun loadUserAchievements() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("users/$userId")

        dbRef.get().addOnSuccessListener { snapshot ->
            val badges = snapshot.child("badges")
            val catLevel = snapshot.child("catLevel").getValue(Int::class.java) ?: 1
            val xp = snapshot.child("catXp").getValue(Int::class.java) ?: 0

            // Populate cat level section
            catLevelText.text = "Level $catLevel - ${getCatTitle(catLevel)}"
            catLevelImage.setImageResource(getCatImage(catLevel))
            catXpProgress.progress = xp

            // Badges - match Firebase keys exactly
            badgeList.clear()
            badgeList.addAll(listOf(
                Badge("Pawsome Start", "Pawsome Start", "Created your first budget", badges.child("Pawsome Start").getValue(Boolean::class.java) == true, R.drawable.pawsome_badge),
                Badge("Budget Buddy", "Budget Buddy", "Stayed under budget", badges.child("Budget Buddy").getValue(Boolean::class.java) == true, R.drawable.budget_buddy),
                Badge("Category Commander", "Category Commander", "5+ category budgets", badges.child("Category Commander").getValue(Boolean::class.java) == true, R.drawable.commander),
                Badge("Consistent Cat", "Consistent Cat", "3 monthly budgets in a row", badges.child("Consistent Cat").getValue(Boolean::class.java) == true, R.drawable.consistent_cat),
                Badge("Sharp Saver", "Sharp Saver", "Spent 20% less than budget", badges.child("Sharp Saver").getValue(Boolean::class.java) == true, R.drawable.sharp_saver),
                Badge("Emergency Expert", "Emergency Expert", "Added emergency savings", badges.child("Emergency Expert").getValue(Boolean::class.java) == true, R.drawable.emergency)
            ))

            badgeAdapter.notifyDataSetChanged()
        }
    }

    private fun getCatTitle(level: Int): String {
        return when (level) {
            1 -> "Kitten"
            2 -> "Curious Cat"
            3 -> "Savvy Siamese"
            4 -> "Budget Bengal"
            else -> "Financial Feline"
        }
    }

    private fun getCatImage(level: Int): Int {
        return when (level) {
            1 -> R.drawable.cat1
            2 -> R.drawable.cat2
            3 -> R.drawable.cat3
            4 -> R.drawable.cat4
            else -> R.drawable.cat5
        }
    }
}