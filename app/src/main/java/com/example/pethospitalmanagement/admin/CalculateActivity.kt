package com.example.pethospitalmanagement.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        // รับข้อมูลรายได้จาก ViewModel และอัพเดท Adapter
        productViewModel.productsLiveData.observe(this) { _ ->
            val revenueList = productViewModel.getDailyRevenue()
            adapter.submitList(revenueList)
        }

        productViewModel.productsLiveData.observe(this) { _ ->
            val revenueList = productViewModel.getGroupedRevenue()
            adapter.submitList(revenueList)
        }



        binding.menuButton.setOnClickListener {
            onBackPressed()
        }
    }
}
