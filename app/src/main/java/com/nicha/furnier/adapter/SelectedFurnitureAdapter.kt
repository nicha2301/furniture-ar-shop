package com.nicha.furnier.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.nicha.furnier.R
import com.nicha.furnier.models.FurnitureItem
import com.nicha.furnier.utils.ColorUtils
import com.nicha.furnier.utils.extensions.onClick

interface FurnitureControlsListener {
    fun onColorSelected(furnitureId: String, colorHex: String)
    fun onRotationChanged(furnitureId: String, angle: Float)
    fun onResetClicked(furnitureId: String)
}

class SelectedFurnitureAdapter(
    private val onItemFocused: (FurnitureItem) -> Unit
) : RecyclerView.Adapter<SelectedFurnitureAdapter.ViewHolder>() {

    private val items = mutableListOf<FurnitureItem>()
    private var selectedItemId: String? = null
    private var expandedPosition: Int = RecyclerView.NO_POSITION
    private var controlsListener: FurnitureControlsListener? = null
    
    fun setControlsListener(listener: FurnitureControlsListener) {
        this.controlsListener = listener
    }

    fun setData(newItems: List<FurnitureItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
    
    fun setSelectedItem(itemId: String) {
        val oldSelectedId = selectedItemId
        val oldPosition = items.indexOfFirst { it.id == oldSelectedId }
        
        selectedItemId = itemId
        expandedPosition = items.indexOfFirst { it.id == itemId }
        
        if (oldPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(oldPosition)
        }
        
        if (expandedPosition != RecyclerView.NO_POSITION && expandedPosition != oldPosition) {
            notifyItemChanged(expandedPosition)
        }
    }
    
    fun clearSelection() {
        val oldPosition = items.indexOfFirst { it.id == selectedItemId }
        selectedItemId = null
        expandedPosition = RecyclerView.NO_POSITION
        
        if (oldPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(oldPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_furniture, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val isSelected = item.id == selectedItemId
        
        holder.bind(item, isSelected)
        
        // Thiết lập recyclerview với colors
        if (isSelected) {
            setupColorRecyclerView(holder, item)
            setupRotationSeekBar(holder, item)
            
            // Thêm xử lý cho nút Reset
            holder.btnReset?.setOnClickListener {
                controlsListener?.onResetClicked(item.id)
            }
        }
        
        holder.itemView.onClick {
            onItemFocused(item)
        }
    }

    private fun setupColorRecyclerView(holder: ViewHolder, item: FurnitureItem) {
        holder.colorRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = ColorAdapter(ColorUtils.FURNITURE_COLORS) { colorHex ->
                controlsListener?.onColorSelected(item.id, colorHex)
            }
        }
    }
    
    private fun setupRotationSeekBar(holder: ViewHolder, item: FurnitureItem) {
        holder.rotationSeekBar.apply {
            max = 360
            progress = 0
            
            // Đặt giá trị ban đầu là 0 và kích hoạt để đảm bảo model hiển thị đúng góc
            progress = 0
            controlsListener?.onRotationChanged(item.id, 0f)
            
            // Thêm một khoảng trễ nhỏ để đảm bảo model được cập nhật trước khi bắt đầu lắng nghe sự kiện
            postDelayed({
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        controlsListener?.onRotationChanged(item.id, progress.toFloat())
                    }
                    
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
            }, 100)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.cardThumbnail)
        val ivThumbnail: ImageView = itemView.findViewById(R.id.ivSelectedFurnitureThumbnail)
        val tvName: TextView = itemView.findViewById(R.id.tvSelectedFurnitureName)
        val tvCategory: TextView = itemView.findViewById(R.id.tvSelectedFurnitureCategory)
        val btnFocus: ImageView = itemView.findViewById(R.id.btnFocusOn)
        val controlsContainer: ConstraintLayout = itemView.findViewById(R.id.modelControlsContainer)
        val colorRecyclerView: RecyclerView = itemView.findViewById(R.id.colorRecyclerView)
        val rotationSeekBar: SeekBar = itemView.findViewById(R.id.rotationSeekBar)
        val btnReset: ImageView? = itemView.findViewById(R.id.btnResetItem)

        fun bind(item: FurnitureItem, isSelected: Boolean) {
            tvName.text = item.name
            tvCategory.text = item.categoryName
            
            // Hiển thị trạng thái đã chọn
            val context = itemView.context
            if (isSelected) {
                cardView.strokeColor = ContextCompat.getColor(context, R.color.colorAccent)
                cardView.strokeWidth = context.resources.getDimensionPixelSize(R.dimen.selected_stroke_width)
                btnFocus.setImageResource(R.drawable.ic_close)
                controlsContainer.visibility = View.VISIBLE
            } else {
                cardView.strokeColor = ContextCompat.getColor(context, android.R.color.transparent)
                cardView.strokeWidth = 0
                btnFocus.setImageResource(R.drawable.ic_search)
                controlsContainer.visibility = View.GONE
            }
            
            // Load thumbnail with Glide
            Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(ivThumbnail)
        }
    }
} 