package com.example.pethospitalmanagement.admin


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pethospitalmanagement.data.db.NewProduct
import com.example.pethospitalmanagement.data.db.RevenueItem
import com.example.pethospitalmanagement.data.repository.NewProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.ParseException
import java.util.Locale

class NewProductViewModel(
    private val repository: NewProductRepository,
    application: Application
) : AndroidViewModel(application) {


    val allNewProducts: LiveData<List<NewProduct>> = repository.allNewProducts
    val filteredNewProductsLiveData = MutableLiveData<List<NewProduct>?>()  // New Line


    // Function to insert a new product
    fun insert(newProduct: NewProduct) = viewModelScope.launch {
        viewModelScope.launch(Dispatchers.IO) { // <-- Change here
            repository.insert(newProduct)
        }    }

    fun update(newProduct: NewProduct) {
        viewModelScope.launch(Dispatchers.IO) {  // <-- Use Dispatchers.IO to run in a background thread
            repository.update(newProduct)
        }
    }

    fun delete(newProduct: NewProduct) {
        viewModelScope.launch(Dispatchers.IO) {  // <-- Use Dispatchers.IO to run in a background thread
            repository.delete(newProduct)
        }
    }

    fun filterNewProducts(query: String) {  // New Function
        val filteredList = allNewProducts.value?.filter { newProduct ->
            newProduct.name.contains(query, ignoreCase = true) ||
                    newProduct.price.toString().contains(query, ignoreCase = true) ||
                    newProduct.date.contains(query, ignoreCase = true) ||
                    newProduct.time.contains(query, ignoreCase = true) ||
                    newProduct.quantity.toString().contains(query, ignoreCase = true) ||
                    newProduct.id.toString().contains(query, ignoreCase = true)
        }
        filteredNewProductsLiveData.postValue(filteredList)
    }

    fun getDailyRevenueNewProduct(): List<RevenueItem> {
        val format = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val groupedByDate = allNewProducts.value?.groupBy { it.date }

        return groupedByDate?.mapNotNull { (date, products) ->
            try {
                // Validate if the date is in the expected format
                format.parse(date)
                RevenueItem(date, products.sumOf { it.price * it.quantity }, false, 0.0)
            } catch (e: ParseException) {
                // Skip this entry if the date is not valid
                null
            }
        }?.sortedByDescending { it.date } ?: listOf()
    }

}
