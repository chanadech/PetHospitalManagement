package com.example.pethospitalmanagement.admin

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.pethospitalmanagement.data.db.RevenueItem

// RevenueAdapter.kt
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.pethospitalmanagement.databinding.ItemMonthBinding
import com.example.pethospitalmanagement.databinding.ItemRevenueBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RevenueAdapter(
    private var revenueList: List<RevenueItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_MONTH = 0
        const val VIEW_TYPE_DATE = 1
    }
    // เพิ่มเมธอด submitList
    fun submitList(newRevenueList: List<RevenueItem>) {
        this.revenueList = newRevenueList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (revenueList[position].isMonthLabel) {
            VIEW_TYPE_MONTH
        } else {
            VIEW_TYPE_DATE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MONTH) {
            val binding = ItemMonthBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MonthViewHolder(binding)
        } else {
            val binding = ItemRevenueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RevenueViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentItem = revenueList[position]
        Log.d("RevenueAdapter", "Binding: $currentItem")

        if (holder is RevenueViewHolder) {
            holder.bind(currentItem)
        } else if (holder is MonthViewHolder) {
            holder.bind(currentItem)
        }
    }

    override fun getItemCount() = revenueList.size

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

    inner class MonthViewHolder(private val binding: ItemMonthBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(revenueItem: RevenueItem) {
            binding.monthTextView.text = revenueItem.date
        }
    }


}
