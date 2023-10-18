package com.example.pethospitalmanagement.data

import com.example.pethospitalmanagement.admin.NewProductViewModel
import com.example.pethospitalmanagement.admin.ProductViewModel
import com.example.pethospitalmanagement.data.db.AppDatabase
import com.example.pethospitalmanagement.data.repository.NewProductRepository
import com.example.pethospitalmanagement.data.repository.ProductRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getDatabase(get()) }
    single { get<AppDatabase>().productDao() }
    single { ProductRepository(get()) }
    viewModel { ProductViewModel(get()) }
    single { get<AppDatabase>().newProductDao() }
    single { NewProductRepository(get()) }  // Make sure this line is added
    viewModel { NewProductViewModel(get(), get()) }  // Make
// sure this line is added
}