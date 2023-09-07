package com.example.pethospitalmanagement.presentation.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethospitalmanagement.databinding.ActivityAdminBinding
import com.example.pethospitalmanagement.presentation.admin.addproduct.EditProductDialogFragment
import com.example.pethospitalmanagement.presentation.ProductAdapter
import com.example.pethospitalmanagement.presentation.ProductViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val productViewModel: ProductViewModel by viewModel()
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        adapter = ProductAdapter(
            onEditClick = { product ->
                val dialog = EditProductDialogFragment(product)
                dialog.show(supportFragmentManager, "Edit Details")
            },
            onDeleteClick = { product ->
                productViewModel.delete(product)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Initialize FAB for adding new product
        binding.btnAddProduct.setOnClickListener {
            val dialog = EditProductDialogFragment()
            dialog.show(supportFragmentManager, "Add Details")
        }

        productViewModel.productsLiveData.observe(this) { products ->
            adapter.setProducts(products)
        }

        productViewModel.filteredProductsLiveData.observe(this) { filteredProducts ->
            adapter.setProducts(filteredProducts ?: listOf())
        }

        val searchBar = binding.searchBar
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                productViewModel.filterProducts(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })



    }
}