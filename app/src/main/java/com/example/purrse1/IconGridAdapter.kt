package com.example.purrse1

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView

class IconGridAdapter (private val context: Context, private val icons: List<String>) : BaseAdapter() {

    override fun getCount() = icons.size
    override fun getItem(position: Int) = icons[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val imageView = ImageView(context)
        val iconId = context.resources.getIdentifier(icons[position], "drawable", context.packageName)
        imageView.setImageResource(iconId)
        imageView.layoutParams = AbsListView.LayoutParams(96, 96)
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        return imageView
    }
}