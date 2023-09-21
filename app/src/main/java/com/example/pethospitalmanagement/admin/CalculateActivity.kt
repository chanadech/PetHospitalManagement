package com.example.pethospitalmanagement.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pethospitalmanagement.R
import com.example.pethospitalmanagement.databinding.ActivityCalculateBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CalculateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalculateBinding
    private val productViewModel: ProductViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // เตรียม Adapter และ RecyclerView
        val adapter = RevenueAdapter(emptyList())
        binding.revenueRecyclerView.adapter = adapter
        binding.revenueRecyclerView.layoutManager = LinearLayoutManager(this)

        productViewModel.productsLiveData.observe(this) { products ->
            // Choose either getDailyRevenue or getGroupedRevenue based on what you want to display.
            Log.d("CalculateActivity", "Observed Products: $products")
            val revenueList = productViewModel.getGroupedRevenue()
            adapter.submitList(revenueList)
            adapter.notifyDataSetChanged()

        }



        binding.menuButton.setOnClickListener {
            onBackPressed()
        }
    }
}
