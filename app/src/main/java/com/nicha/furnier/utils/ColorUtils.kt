package com.nicha.furnier.utils

import android.content.Context
import com.nicha.furnier.R

/**
 * Lớp chứa các hằng số liên quan đến màu sắc và cấu hình màu sắc trong ứng dụng
 */
object ColorUtils {
    const val RESET_COLOR = "RESET"
    
    // Danh sách màu sắc cho đồ nội thất
    val FURNITURE_COLORS = listOf(
        RESET_COLOR,  // Màu reset
        "#FFFFFF",    // Trắng
        "#000000",    // Đen
        "#8B4513",    // Nâu gỗ
        "#A52A2A",    // Nâu đỏ 
        "#D2B48C",    // Tan
        "#808080",    // Xám
        "#4169E1",    // Xanh dương
        "#228B22"     // Xanh lá
    )
    
    // Danh sách màu sắc cho vật liệu
    val MATERIAL_COLORS = listOf(
        RESET_COLOR,  // Màu reset
        "#FFFFFF",    // Trắng
        "#F5F5DC",    // Beige
        "#D2B48C",    // Tan
        "#DEB887",    // BurlyWood
        "#CD853F",    // Peru
        "#D2691E",    // Chocolate
        "#8B4513",    // SaddleBrown
        "#A52A2A"     // Nâu đỏ
    )
    
    // Danh sách màu sắc cho AR View
    val AR_COLORS = listOf(
        RESET_COLOR,  // Màu reset
        "#FF0000",    // Đỏ
        "#00FF00",    // Xanh lá
        "#0000FF",    // Xanh dương
        "#FFFF00",    // Vàng
        "#FF00FF",    // Hồng
        "#00FFFF",    // Xanh lục lam
        "#FFFFFF",    // Trắng
        "#000000"     // Đen
    )
    
    /**
     * Lấy danh sách màu cho AR View từ resource strings
     */
    fun getARColorsFromResources(context: Context): List<String> {
        return listOf(
            context.getString(R.string.color_reset),
            context.getString(R.string.color_red),
            context.getString(R.string.color_green),
            context.getString(R.string.color_blue),
            context.getString(R.string.color_yellow),
            context.getString(R.string.color_magenta),
            context.getString(R.string.color_cyan),
            context.getString(R.string.color_white),
            context.getString(R.string.color_black)
        )
    }
} 