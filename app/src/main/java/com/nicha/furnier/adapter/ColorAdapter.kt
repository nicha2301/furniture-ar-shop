package com.nicha.furnier.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nicha.furnier.R
import com.nicha.furnier.utils.ColorUtils

class ColorAdapter(
    private val colors: List<String>,
    private val onColorSelected: (String) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    companion object {
        private val LAYOUT_ITEM_COLOR = R.layout.item_color
        private val DRAWABLE_RESET = R.drawable.ic_reset
        private const val ANIMATION_DURATION = 300L
    }

    class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view as CardView
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
            setOnClickListener { 
                animateColorSelection(holder.cardView)
                onColorSelected(color) 
            }
        }
    }

    private fun animateColorSelection(cardView: CardView) {
        // Tạo hiệu ứng nhấp nháy và phóng to thu nhỏ
        val scaleX = ObjectAnimator.ofFloat(cardView, "scaleX", 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(cardView, "scaleY", 1f, 1.2f, 1f)
        val alpha = ObjectAnimator.ofFloat(cardView, "alpha", 1f, 0.8f, 1f)
        
        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = ANIMATION_DURATION
            interpolator = OvershootInterpolator()
            start()
        }
    }

    override fun getItemCount(): Int = colors.size

    private fun ImageView.configureColorDisplay(color: String) {
        if (color == ColorUtils.RESET_COLOR) {
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