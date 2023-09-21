package com.example.pethospitalmanagement.data.db

data class RevenueItem (
    val date: String,
    val totalIncome: Double,
    val isMonthLabel: Boolean = false
)