package com.example.pethospitalmanagement.admin

import androidx.recyclerview.widget.RecyclerView
import com.example.pethospitalmanagement.data.db.RevenueItem

// RevenueAdapter.kt
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.pethospitalmanagement.databinding.ItemRevenueBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RevenueAdapter(private var revenueList: List<RevenueItem>) : RecyclerView.Adapter<RevenueAdapter.RevenueViewHolder>() {

    // เพิ่มเมธอด submitList
    fun submitList(newRevenueList: List<RevenueItem>) {
        this.revenueList = newRevenueList
        notifyDataSetChanged()
    }

    inner class RevenueViewHolder(private val binding: ItemRevenueBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(revenueItem: RevenueItem) {
            if (revenueItem.isMonthLabel) {
                // This is a month label
                binding.dateTextView.text = revenueItem.date  // format it however you like
                binding.incomeTextView.text = ""
            } else {
                // This is a daily entry
                val inputFormat = SimpleDateFormat("yyyy-M-d", Locale.US)
                val outputFormat = SimpleDateFormat("EEE d-M-yyyy", Locale.US)
                val inputDate = inputFormat.parse(revenueItem.date)
                val outputDate = outputFormat.format(inputDate ?: Date())
                binding.dateTextView.text = outputDate
                binding.incomeTextView.text = String.format(Locale.US, "%,.2f THB", revenueItem.totalIncome)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RevenueViewHolder {
        val binding = ItemRevenueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RevenueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RevenueViewHolder, position: Int) {
        val currentItem = revenueList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = revenueList.size
}
