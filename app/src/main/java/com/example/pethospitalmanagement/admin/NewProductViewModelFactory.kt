package com.example.pethospitalmanagement.admin


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pethospitalmanagement.data.db.NewProductDao
import com.example.pethospitalmanagement.data.repository.NewProductRepository

class NewProductViewModelFactory(
    private val dataSource: NewProductDao,
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewProductViewModel::class.java)) {
            return NewProductViewModel(
                NewProductRepository(dataSource),
                application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
