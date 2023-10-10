package com.example.pethospitalmanagement.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "new_products")
data class NewProduct(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val price: Double,
    val quantity: Int,
    val date: String,
    val time: String
)
