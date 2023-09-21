package com.example.pethospitalmanagement.admin
import androidx.lifecycle.viewModelScope
import com.example.pethospitalmanagement.data.db.Product
import com.example.pethospitalmanagement.data.repository.ProductRepository
import kotlinx.coroutines.launch
import com.example.pethospitalmanagement.data.db.RevenueItem
import java.util.Date
import java.util.Locale
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.ParseException
import java.util.*

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    val productsLiveData = MutableLiveData<List<Product>>()
    val filteredProductsLiveData = MutableLiveData<List<Product>?>()

    // Function to insert a product
    fun insert(product: Product) = viewModelScope.launch {
        Log.d("ProductViewModel", "Inserting: $product")

        repository.insert(product)
        fetchAllProducts()
    }

    // Function to update a product
    fun update(product: Product) = viewModelScope.launch {
        Log.d("ProductViewModel", "Updating with product: $product")

        repository.update(product)
        fetchAllProducts()

        // After fetching all products, you can verify the update by checking the latest data in productsLiveData
        Log.d("ProductViewModel", "Products after update: ${productsLiveData.value}")
    }


    // Function to delete a product
    fun delete(product: Product) = viewModelScope.launch {
        repository.delete(product)
        fetchAllProducts()
    }

    // Function to fetch all products
    fun fetchAllProducts() = viewModelScope.launch {
        val products = repository.getAllProducts().map { product ->
            product.copy(selectedDate = convertDateToStandardFormat(product.selectedDate))
        }
        Log.d("ViewModel", "Fetched products: $products")

        val format = java.text.SimpleDateFormat("yyyy-MM-dd h:mm a", Locale.US)

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

    // Function to filter products
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

    // Function to get daily revenue
    // Function to get daily revenue
// Function to get daily revenue
    fun getDailyRevenue(): List<RevenueItem> {
        val format = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val groupedByDate = productsLiveData.value?.groupBy { it.selectedDate }

        return groupedByDate?.mapNotNull { (date, products) ->
            try {
                // Validate if the date is in the expected format
                format.parse(date)
                RevenueItem(date, products.sumOf { it.price }, false, 0.0)
            } catch (e: ParseException) {
                // Skip this entry if the date is not valid
                null
            }
        }?.sortedByDescending { it.date } ?: listOf()  // Sort by date in descending order
    }


    fun getGroupedRevenue(): List<RevenueItem> {
        val dailyRevenue = getDailyRevenue()
        Log.d("ProductViewModel", "Calculated dailyRevenue: $dailyRevenue")

        val monthFormat = SimpleDateFormat("yyyy-MM", Locale.US)
        val groupedByMonth = dailyRevenue.groupBy {
            monthFormat.format(SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it.date)!!)
        }

        val groupedRevenueList = mutableListOf<RevenueItem>()

        val sortedMonths = groupedByMonth.keys.sortedDescending()  // Sort the months in descending order
        sortedMonths.forEach { month ->
            val items = groupedByMonth[month] ?: emptyList()
            val monthlyTotalIncome = items.sumByDouble { it.totalIncome }  // Calculate the total monthly income
            groupedRevenueList.add(RevenueItem(month, 0.0, true, monthlyTotalIncome))  // Add month label with total monthly income
            groupedRevenueList.addAll(items)  // Add individual days
        }

        return groupedRevenueList
    }


    private fun convertDateToStandardFormat(dateStr: String): String {
        val inputFormats = listOf(
            SimpleDateFormat("dd/MM/yyyy", Locale.US),
            SimpleDateFormat("yyyy-M-d", Locale.US)
        )

        for (format in inputFormats) {
            try {
                val date = format.parse(dateStr)
                if (date != null) {
                    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
                }
            } catch (e: ParseException) {
                // If parsing fails, try the next format
            }
        }

        // If no format matches, return the original string
        return dateStr
    }

    init {
        fetchAllProducts()  // Initially fetch all products
    }
}
