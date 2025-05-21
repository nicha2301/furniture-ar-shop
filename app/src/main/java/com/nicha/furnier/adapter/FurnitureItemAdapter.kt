package com.nicha.furnier.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.card.MaterialCardView
import com.nicha.furnier.R
import com.nicha.furnier.models.FurnitureItem
import com.nicha.furnier.utils.extensions.onClick

class FurnitureItemAdapter(
    private val onItemSelected: (FurnitureItem) -> Unit,
    private val getSelectedItemId: () -> String?
) : RecyclerView.Adapter<FurnitureItemAdapter.ViewHolder>() {

    private val items = mutableListOf<FurnitureItem>()

    fun setData(newItems: List<FurnitureItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_furniture, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val isSelected = getSelectedItemId() == item.id
        holder.bind(item, isSelected)
        
        holder.itemView.onClick {
            onItemSelected(item)
            notifyDataSetChanged() // Refresh all to update selected state
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivFurniture: ImageView = itemView.findViewById(R.id.ivFurniture)
        private val tvName: TextView = itemView.findViewById(R.id.tvFurnitureName)
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardFurniture)
        private val ivSelected: ImageView = itemView.findViewById(R.id.ivSelected)

        fun bind(item: FurnitureItem, isSelected: Boolean) {
            tvName.text = item.name
            
            // Load image with Glide
            Glide.with(itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(ivFurniture)
            
            // Update selected state
            if (isSelected) {
                cardView.strokeWidth = itemView.resources.getDimensionPixelSize(R.dimen.selected_stroke_width)
                cardView.strokeColor = itemView.resources.getColor(R.color.colorPrimary, null)
                ivSelected.visibility = View.VISIBLE
            } else {
                cardView.strokeWidth = itemView.resources.getDimensionPixelSize(R.dimen.card_stroke_width)
                cardView.strokeColor = itemView.resources.getColor(R.color.card_stroke_color, null)
                ivSelected.visibility = View.GONE
            }
        }
    }
} 