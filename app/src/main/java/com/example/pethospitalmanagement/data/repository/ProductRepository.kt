package com.example.pethospitalmanagement.data.repository

import com.example.pethospitalmanagement.data.db.Product
import com.example.pethospitalmanagement.data.db.ProductDao

class ProductRepository(private val productDao: ProductDao) {

    suspend fun insert(product: Product): Long {
        return productDao.insert(product)
    }

    suspend fun getAllProducts(): List<Product> {
        return productDao.getAllProducts()
    }

    suspend fun update(product: Product) {
        productDao.update(product)
    }

    suspend fun delete(product: Product) {
        productDao.delete(product)
    }
}
