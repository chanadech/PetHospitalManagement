package com.example.pethospitalmanagement.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NewProductDao {

    @Query("SELECT * FROM new_products")
    fun getAllNewProducts(): LiveData<List<NewProduct>>

    @Insert
    fun insertNewProduct(newProduct: NewProduct)

    @Update
    fun updateNewProduct(newProduct: NewProduct)

    @Delete
    fun deleteNewProduct(newProduct: NewProduct)
}
