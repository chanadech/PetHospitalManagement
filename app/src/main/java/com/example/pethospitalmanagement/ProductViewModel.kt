package com.example.pethospitalmanagement.presentation
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pethospitalmanagement.data.db.Product
import com.example.pethospitalmanagement.data.repository.ProductRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    val productsLiveData = MutableLiveData<List<Product>>()
    val filteredProductsLiveData = MutableLiveData<List<Product>?>()

    // Function to insert a product
    fun insert(product: Product) = viewModelScope.launch {
        repository.insert(product)
        fetchAllProducts()  // Refresh the product list

    }

    fun filterProducts(query: String) {
        val filteredList = productsLiveData.value?.filter { product ->
            product.name.contains(query, ignoreCase = true) ||
                    product.type.contains(query, ignoreCase = true) ||
                    product.details.contains(query, ignoreCase = true) ||
                    product.price.toString().contains(query, ignoreCase = true) ||
                    product.selectedDate.contains(query, ignoreCase = true) ||
                    product.selectedTime.contains(query, ignoreCase = true) ||
                    (product.telephone?.contains(query, ignoreCase = true) == true)
        }
        filteredProductsLiveData.postValue(filteredList)
    }


    fun fetchAllProducts() = viewModelScope.launch {
        val products = repository.getAllProducts()
        val format = SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.US)

        val sortedProducts = products.sortedWith(Comparator { p1, p2 ->
            var dateTime1: Date? = null
            var dateTime2: Date? = null

            val dateTimeString1 = "${p1.selectedDate} ${p1.selectedTime}"
            val dateTimeString2 = "${p2.selectedDate} ${p2.selectedTime}"

            try {
                dateTime1 = format.parse(dateTimeString1)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            try {
                dateTime2 = format.parse(dateTimeString2)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            when {
                dateTime1 != null && dateTime2 != null -> {
                    dateTime2.compareTo(dateTime1)
                }
                dateTime1 == null && dateTime2 == null -> {
                    p1.name.compareTo(p2.name)
                }
                else -> {
                    if (dateTime1 == null) 1 else -1
                }
            }
        })

        productsLiveData.postValue(sortedProducts)
    }



    // Function to update a product
    fun update(product: Product) = viewModelScope.launch {
        repository.update(product)
        fetchAllProducts()  // Refresh the product list
    }

    // Function to delete a product
    fun delete(product: Product) = viewModelScope.launch {
        repository.delete(product)
        fetchAllProducts()  // Refresh the product list
    }



    init {
        fetchAllProducts()  // Initially fetch all products
    }
}
