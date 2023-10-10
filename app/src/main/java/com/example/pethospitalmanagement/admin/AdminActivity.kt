package com.example.pethospitalmanagement.presentation.admin

import AddProductDialogFragment
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethospitalmanagement.R
import com.example.pethospitalmanagement.admin.AddedProductAdapter
import com.example.pethospitalmanagement.admin.NewProductViewModel
import com.example.pethospitalmanagement.admin.calculate.CalculateActivity
import com.example.pethospitalmanagement.databinding.ActivityAdminBinding
import com.example.pethospitalmanagement.admin.editproduct.EditProductDialogFragment
import com.example.pethospitalmanagement.admin.ProductAdapter
import com.example.pethospitalmanagement.admin.ProductViewModel
import com.example.pethospitalmanagement.databinding.FragmentAddProductDialogBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.recyclerview.widget.ConcatAdapter


class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val productViewModel: ProductViewModel by viewModel()
    private val newProductViewModel: NewProductViewModel by viewModel()  // New Line

    private lateinit var adapter: ProductAdapter
    private lateinit var addedProductAdapter: AddedProductAdapter

    @SuppressLint("ClickableViewAccessibility")
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
            dialog.show(supportFragmentManager, "เพิ่มข้อมูลสัตว์เลี้ยง")
        }

        productViewModel.productsLiveData.observe(this) { products ->
            adapter.setProducts(products)
        }

        productViewModel.filteredProductsLiveData.observe(this) { filteredProducts ->
            adapter.setProducts(filteredProducts ?: listOf())
        }
        val clearDrawable = ContextCompat.getDrawable(this, R.drawable.clear_icon)
        binding.searchBar.setCompoundDrawablesWithIntrinsicBounds(null, null, clearDrawable, null)


        val searchBar = binding.searchBar
        searchBar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (searchBar.right - searchBar.compoundDrawables[2].bounds.width())) {
                    searchBar.setText("")  // Clear text
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                productViewModel.filterProducts(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.menuButton.setOnClickListener {
            val intent = Intent(this, CalculateActivity::class.java)
            startActivity(intent)
        }

        binding.btnAddProductType.setOnClickListener {
            showAddProductTypeDialog()
        }


        addedProductAdapter = AddedProductAdapter()  // New Line
        val concatAdapter = ConcatAdapter(adapter, addedProductAdapter)  // New Line
        binding.recyclerView.adapter = concatAdapter

        newProductViewModel.allNewProducts.observe(this) { newProducts ->
            Log.d("AdminActivity", "New Products: $newProducts")  // Add this line

            addedProductAdapter.setData(newProducts)
        }

    }

    private fun showAddProductTypeDialog() {
        val addProductDialogFragment = AddProductDialogFragment()
        addProductDialogFragment.show(supportFragmentManager, "AddProductDialogFragment")


    }

}