package com.example.pethospitalmanagement.admin

import AddProductDialogFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.pethospitalmanagement.data.db.NewProduct
import com.example.pethospitalmanagement.databinding.ItemAddProductBinding

class AddedProductAdapter(private val onEditClick: (NewProduct) -> Unit,
                          private val onDeleteClick: (NewProduct) -> Unit
) : RecyclerView.Adapter<AddedProductAdapter.ViewHolder>() {

    private var newProducts: List<NewProduct> = listOf()

    fun setData(newProducts: List<NewProduct>) {
        this.newProducts = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAddProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(newProducts[position], onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int {
        return newProducts.size
    }

    class ViewHolder(private val binding: ItemAddProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(newProduct: NewProduct, onEditClick: (NewProduct) -> Unit, onDeleteClick: (NewProduct) -> Unit) {
            binding.newProductName.text = "ชื่อสินค้า: ${newProduct.name}"
            binding.newProductPrice.text = "ราคา: ${newProduct.price} บาท"
            binding.newProductQuantity.text = "จำนวน: ${newProduct.quantity}"
            binding.newProductDate.text = "วันที่: ${newProduct.date}"
            binding.newProductTime.text = "เวลา: ${newProduct.time}"

            binding.btnEdit.setOnClickListener {
                val dialog = AddProductDialogFragment()
                dialog.setProductToEdit(newProduct)  // set the product to be edited
                dialog.show((binding.root.context as AppCompatActivity).supportFragmentManager, "Edit Product")
            }


            binding.btnDelete.setOnClickListener {
                onDeleteClick(newProduct)
            }
        }
    }
}
