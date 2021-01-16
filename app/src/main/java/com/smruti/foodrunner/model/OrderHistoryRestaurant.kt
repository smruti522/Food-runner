package com.smruti.foodrunner.model


import org.json.JSONArray

data class OrderHistoryRestaurant (
    var orderId:String,
    var restaurantName:String,

    var orderPlacedAt:String,
    var foodItem:JSONArray
)
