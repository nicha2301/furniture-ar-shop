package com.nicha.furnier.utils

import android.content.Context
import com.nicha.furnier.R
import com.nicha.furnier.models.MaterialItem


fun getARMaterialsFromResources(context: Context): List<MaterialItem> {
    val materialList = mutableListOf<MaterialItem>()
    
    materialList.add(
        MaterialItem(
            id = "reset",
            name = context.getString(R.string.material_reset_option),
            isReset = true
        )
    )
    
    materialList.add(
        MaterialItem(
            id = "metal",
            name = context.getString(R.string.material_metal),
            metallic = 0.9f,
            roughness = 0.2f
        )
    )
    
    materialList.add(
        MaterialItem(
            id = "glossy",
            name = context.getString(R.string.material_glossy),
            metallic = 0.0f,
            roughness = 0.1f
        )
    )
    
    materialList.add(
        MaterialItem(
            id = "plastic",
            name = context.getString(R.string.material_plastic),
            metallic = 0.0f,
            roughness = 0.4f
        )
    )
    
    materialList.add(
        MaterialItem(
            id = "rough",
            name = context.getString(R.string.material_rough),
            metallic = 0.0f,
            roughness = 0.9f
        )
    )
    
    materialList.add(
        MaterialItem(
            id = "wood",
            name = context.getString(R.string.material_wood),
            metallic = 0.0f,
            roughness = 0.7f
        )
    )
    
    return materialList
}