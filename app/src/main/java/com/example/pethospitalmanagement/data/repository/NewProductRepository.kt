package com.example.pethospitalmanagement.data.repository

import androidx.lifecycle.LiveData
import com.example.pethospitalmanagement.data.db.NewProduct
import com.example.pethospitalmanagement.data.db.NewProductDao

class NewProductRepository(private val newProductDao: NewProductDao) {

    val allNewProducts: LiveData<List<NewProduct>> = newProductDao.getAllNewProducts()

    suspend fun insert(newProduct: NewProduct) {
        newProductDao.insertNewProduct(newProduct)
    }

    suspend fun update(newProduct: NewProduct) {
        newProductDao.updateNewProduct(newProduct)
    }

    suspend fun delete(newProduct: NewProduct) {
        newProductDao.deleteNewProduct(newProduct)
    }
}
