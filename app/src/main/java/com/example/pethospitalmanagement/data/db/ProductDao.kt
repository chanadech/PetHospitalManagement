package com.example.pethospitalmanagement.data.db

import androidx.room.*

@Dao
interface ProductDao {

    @Insert
    suspend fun insert(product: Product): Long

    @Query("SELECT * FROM product_table")
    suspend fun getAllProducts(): List<Product>

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)
}
