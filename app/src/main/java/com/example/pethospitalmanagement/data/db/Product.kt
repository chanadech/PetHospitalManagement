package com.example.pethospitalmanagement.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_table")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val details: String,
    val price: Double,
    val imagePath: String,
    val selectedDate: String,
    val selectedTime: String,
    val telephone: String? = null
)
