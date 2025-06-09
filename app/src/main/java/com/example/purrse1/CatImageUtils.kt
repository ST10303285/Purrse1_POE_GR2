package com.example.purrse1

object CatImageUtils {
    fun getCatDrawableRes(level: Int): Int {
        return when(level) {
            1 -> R.drawable.cat1
            2 -> R.drawable.cat2
            3 -> R.drawable.cat3
            4 -> R.drawable.cat4
            else -> R.drawable.cat1
        }
    }
}