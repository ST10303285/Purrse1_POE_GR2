package com.example.purrse1

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BadgeUtils {
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid ?: "")

    fun unlockBadge(badgeKey: String) {
        if (uid == null) return

        val badgeRef = userRef.child("badges").child(badgeKey)
        badgeRef.setValue(true).addOnSuccessListener {
            updateCatLevel()
        }
    }

    private fun updateCatLevel() {
        userRef.child("badges").get().addOnSuccessListener { snapshot ->
            val earnedCount = snapshot.children.count { it.getValue(Boolean::class.java) == true }

            val newCatLevel = when {
                earnedCount >= 5 -> 4
                earnedCount >= 3 -> 3
                earnedCount >= 1 -> 2
                else -> 1
            }

            userRef.child("catLevel").setValue(newCatLevel)
        }
    }

    fun checkAndUnlockPawsomeStart() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

        userRef.child("budgets").addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                if (snapshot.childrenCount == 1L) { // Only 1 budget created so far
                    unlockBadge("Pawsome Start")
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }

    fun checkCategoryCommanderBadge() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val budgetsRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("budgets")

        budgetsRef.get().addOnSuccessListener { snapshot ->
            val assignedCategories = mutableListOf<String>()
            for (budgetSnap in snapshot.children) {
                val categoryName = budgetSnap.child("category").getValue(String::class.java)
                if (!categoryName.isNullOrEmpty()) {
                    assignedCategories.add(categoryName)
                }
            }

            if (assignedCategories.size >= 5) {
                unlockBadge("Category Commander")
            }
        }
    }

    fun checkAndUnlockEmergencyExpert(categoryName: String?) {
        if (categoryName == null) return

        if (categoryName.contains("saving", ignoreCase = true) ||
            categoryName.contains("emergency", ignoreCase = true)) {
            unlockBadge("Emergency Expert")
        }
    }
}