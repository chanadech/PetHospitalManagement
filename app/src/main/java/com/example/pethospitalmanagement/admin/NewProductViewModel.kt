package com.example.pethospitalmanagement.admin


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.pethospitalmanagement.data.db.NewProduct
import com.example.pethospitalmanagement.data.repository.NewProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewProductViewModel(
    private val repository: NewProductRepository,
    application: Application
) : AndroidViewModel(application) {


    val allNewProducts: LiveData<List<NewProduct>> = repository.allNewProducts


    // Function to insert a new product
    fun insert(newProduct: NewProduct) = viewModelScope.launch {
        viewModelScope.launch(Dispatchers.IO) { // <-- Change here
            repository.insert(newProduct)
        }    }
}
