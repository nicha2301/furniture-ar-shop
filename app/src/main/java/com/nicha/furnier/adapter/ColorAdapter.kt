package com.nicha.furnier.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.nicha.furnier.R

class ColorAdapter(
    private val colors: List<String>,
    private val onColorSelected: (String) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    companion object {
        private const val RESET_COLOR = "RESET"
        private const val LAYOUT_ITEM_COLOR = R.layout.item_color
        private const val DRAWABLE_RESET = R.drawable.ic_reset
    }

    class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorView: ImageView = view.findViewById(R.id.colorView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(LAYOUT_ITEM_COLOR, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = colors[position]
        holder.colorView.apply {
            configureColorDisplay(color)
            setOnClickListener { onColorSelected(color) }
        }
    }

    override fun getItemCount(): Int = colors.size

    private fun ImageView.configureColorDisplay(color: String) {
        if (color == RESET_COLOR) {
            setImageResource(DRAWABLE_RESET)
            setBackgroundColor(Color.TRANSPARENT)
        } else {
            setImageResource(0)
            try {
                setBackgroundColor(Color.parseColor(color))
            } catch (e: IllegalArgumentException) {
                setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}