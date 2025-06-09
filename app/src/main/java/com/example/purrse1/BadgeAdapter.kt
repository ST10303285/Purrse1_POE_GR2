package com.example.purrse1

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BadgeAdapter(private val badgeList: List<Badge>) :
    RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val badgeImage: ImageView = itemView.findViewById(R.id.badgeImage)
        val badgeName: TextView = itemView.findViewById(R.id.badgeName)
        val lockIcon: ImageView = itemView.findViewById(R.id.lockIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badgeList[position]
        holder.badgeImage.setImageResource(badge.iconRes)
        holder.badgeName.text = badge.name

        if (!badge.earned) {
            holder.badgeImage.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
            holder.lockIcon.visibility = View.VISIBLE
        } else {
            holder.badgeImage.clearColorFilter()
            holder.lockIcon.visibility = View.GONE
        }
    }

    override fun getItemCount() = badgeList.size
}