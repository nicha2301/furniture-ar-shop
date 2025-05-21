package com.nicha.furnier.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.nicha.furnier.R
import com.nicha.furnier.models.FurnitureCategory
import com.nicha.furnier.utils.extensions.onClick

class FurnitureCategoryAdapter(private val onCategorySelected: (FurnitureCategory) -> Unit) :
    RecyclerView.Adapter<FurnitureCategoryAdapter.ViewHolder>() {

    private val items = mutableListOf<FurnitureCategory>()
    private var selectedPosition = 0

    fun setData(newItems: List<FurnitureCategory>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_furniture_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position == selectedPosition)
        
        holder.itemView.onClick {
            val oldSelectedPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            
            // Update UI for previously selected and newly selected items
            notifyItemChanged(oldSelectedPosition)
            notifyItemChanged(selectedPosition)
            
            onCategorySelected(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivCategoryIcon)
        private val tvName: TextView = itemView.findViewById(R.id.tvCategoryName)
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardCategory)

        fun bind(category: FurnitureCategory, isSelected: Boolean) {
            tvName.text = category.name
            
            // Hiển thị hình ảnh danh mục từ URL
            Glide.with(itemView.context)
                .load(category.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(ivIcon)
            
            // Highlight selected item
            if (isSelected) {
                cardView.strokeWidth = itemView.resources.getDimensionPixelSize(R.dimen.selected_stroke_width)
                cardView.setCardBackgroundColor(itemView.resources.getColor(R.color.colorPrimary, null))
                tvName.setTextColor(itemView.resources.getColor(R.color.white, null))
            } else {
                cardView.strokeWidth = 0
                cardView.setCardBackgroundColor(itemView.resources.getColor(R.color.white, null))
                tvName.setTextColor(itemView.resources.getColor(R.color.textColorPrimary, null))
            }
        }
    }
} 