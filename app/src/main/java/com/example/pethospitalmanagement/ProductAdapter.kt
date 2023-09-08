package com.example.pethospitalmanagement.presentation

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pethospitalmanagement.data.db.Product
import com.example.pethospitalmanagement.databinding.ItemProductBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ProductAdapter(
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var products: List<Product> = listOf()
    private var originalProducts: List<Product> = listOf()


    fun setProducts(products: List<Product>) {
        this.products = products
        this.originalProducts = products
        notifyDataSetChanged()
    }


    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val regex = "^[0-9]{10}$".toRegex()
        return regex.matches(phoneNumber)
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        return String.format(
            "%s-%s-%s",
            phoneNumber.substring(0, 3),
            phoneNumber.substring(3, 6),
            phoneNumber.substring(6, 10)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size


    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            if (product.selectedDate.isNotEmpty()) {
                try {
                    val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val targetFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                    val date = originalFormat.parse(product.selectedDate)
                    val formattedDate = targetFormat.format(date!!)
                    binding.productSelectedDate.text = "Date: $formattedDate"
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.productSelectedDate.text = "Date: ${product.selectedDate}"
                }
            } else {
                // Set placeholder if no date is selected
                binding.productSelectedDate.text = "Date: -"
            }


            binding.productName.text = product.name
            binding.productType.text = product.type
            binding.productPrice.text = product.price.toString() + " THB"
            binding.productDetail.text = product.details.toString()

            val selectedTime = product.selectedTime
            if (selectedTime.isNullOrEmpty()) {
                binding.productSelectedTime.text = "Time: -"
            } else {
                binding.productSelectedTime.text = "Time: $selectedTime"
            }


            val telephone = product.telephone ?: ""
            if (isValidPhoneNumber(telephone)) {
                val formattedNumber = formatPhoneNumber(telephone)
                val spannableString = SpannableString(formattedNumber)
                spannableString.setSpan(UnderlineSpan(), 0, formattedNumber.length, 0)
                binding.productTelephone.text = spannableString
                binding.productTelephone.setTextColor(Color.BLUE)  // Set text color to blue

                binding.productTelephone.setOnClickListener {
                    // Create an AlertDialog to confirm
                    AlertDialog.Builder(it.context)
                        .setTitle("Do you want to call")
                        .setMessage("Phone number: $formattedNumber")
                        .setPositiveButton("Yes") { _, _ ->
                            val dialIntent = Intent(Intent.ACTION_DIAL)
                            dialIntent.data = Uri.parse("tel:$telephone")
                            it.context.startActivity(dialIntent)
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            } else {
                binding.productTelephone.text = "Please fill the mobile phone"
                binding.productTelephone.setTextColor(Color.RED)
                binding.productTelephone.setOnClickListener(null)
            }

            // Handle edit and delete actions
            binding.btnEdit.setOnClickListener { onEditClick(product) }
            binding.btnDelete.setOnClickListener {
                // สร้าง AlertDialog และกำหนดคุณสมบัติ
                AlertDialog.Builder(itemView.context)
                    .setTitle("Do you want to delete") // หัวข้อ
                    .setMessage("Pet Name: ${product.name}") // รายละเอียด
                    .setPositiveButton("Yes") { _, _ ->
                        onDeleteClick(product) // ถ้ากด "Yes", ลบ item
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss() // ถ้ากด "No", ปิด dialog
                    }
                    .create()
                    .show()
            }
            Glide.with(itemView.context)
                .load(Uri.parse(product.imagePath))
                .into(binding.productImage)
        }
    }
}
