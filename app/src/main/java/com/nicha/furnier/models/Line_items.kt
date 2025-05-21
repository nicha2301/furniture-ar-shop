package com.nicha.furnier.models

import java.io.Serializable

data class Line_items(
    var product_id: Int = 0,
    var quantity: Int = 0
) : Serializable