package com.nicha.furnier.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nicha.furnier.R
import com.nicha.furnier.models.FurnitureItem
import com.nicha.furnier.utils.extensions.onClick

class SelectedFurnitureAdapter(
    private val onItemFocused: (FurnitureItem) -> Unit
) : RecyclerView.Adapter<SelectedFurnitureAdapter.ViewHolder>() {

    private val items = mutableListOf<FurnitureItem>()

    fun setData(newItems: List<FurnitureItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_furniture, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        
        holder.itemView.onClick {
            onItemFocused(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivThumbnail: ImageView = itemView.findViewById(R.id.ivSelectedFurnitureThumbnail)
        private val tvName: TextView = itemView.findViewById(R.id.tvSelectedFurnitureName)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvSelectedFurnitureCategory)
        private val btnFocus: ImageView = itemView.findViewById(R.id.btnFocusOn)

        fun bind(item: FurnitureItem) {
            tvName.text = item.name
            tvCategory.text = item.categoryName
            
            // Load thumbnail with Glide
            Glide.with(itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(ivThumbnail)
        }
    }
} 