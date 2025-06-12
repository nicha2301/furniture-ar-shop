package com.nicha.furnier.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nicha.furnier.R
import com.nicha.furnier.models.MaterialItem


class MaterialAdapter(
    private val materials: List<MaterialItem>,
    private val onMaterialSelected: (MaterialItem) -> Unit
) : RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_material, parent, false)
        return MaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = materials[position]
        holder.bind(material)
    }

    override fun getItemCount(): Int = materials.size

    inner class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMaterialName: TextView = itemView.findViewById(R.id.tvMaterialName)
        private val materialPreview: View = itemView.findViewById(R.id.materialPreview)

        fun bind(material: MaterialItem) {
            tvMaterialName.text = material.name
            
            if (material.isReset) {
                materialPreview.setBackgroundResource(R.drawable.ic_reset_material)
            } else {
                materialPreview.alpha = if (material.metallic > 0.5f) 0.9f else 0.7f
                materialPreview.setBackgroundResource(
                    when {
                        material.metallic > 0.8f -> R.drawable.bg_material_metal
                        material.roughness < 0.3f -> R.drawable.bg_material_glossy
                        material.roughness > 0.7f -> R.drawable.bg_material_rough
                        else -> R.drawable.bg_material_default
                    }
                )
            }

            itemView.setOnClickListener { onMaterialSelected(material) }
        }
    }
} 