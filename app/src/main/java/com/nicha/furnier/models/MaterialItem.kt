package com.nicha.furnier.models

data class MaterialItem(
    val id: String,
    val name: String,
    val metallic: Float = 0.0f,
    val roughness: Float = 0.5f,
    val isReset: Boolean = false
) 