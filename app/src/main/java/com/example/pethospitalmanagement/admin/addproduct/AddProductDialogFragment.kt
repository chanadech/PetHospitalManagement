import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.pethospitalmanagement.R
import com.example.pethospitalmanagement.data.db.AppDatabase
import com.example.pethospitalmanagement.data.db.NewProduct
import com.example.pethospitalmanagement.databinding.FragmentAddProductDialogBinding
import com.example.pethospitalmanagement.admin.NewProductViewModel
import com.example.pethospitalmanagement.admin.NewProductViewModelFactory

class AddProductDialogFragment : DialogFragment() {

    // Initialize ViewBinding
    private lateinit var binding: FragmentAddProductDialogBinding

    // Initialize ViewModel
    private lateinit var newProductViewModel: NewProductViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentAddProductDialogBinding.inflate(LayoutInflater.from(context))

        // Initialize ViewModel here (New line)
        val application = requireNotNull(this.activity).application
        val dataSource = AppDatabase.getDatabase(application).newProductDao()
        val viewModelFactory = NewProductViewModelFactory(dataSource, application)
        newProductViewModel = ViewModelProvider(this, viewModelFactory).get(NewProductViewModel::class.java)

        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.AlertDialogCustom)
            builder.setView(binding.root)
            builder.setTitle("เพิ่มสินค้า")
            val dialog = builder.create()

            dialog.setOnShowListener {
                val width = (resources.displayMetrics.widthPixels * 0.9).toInt()  // 70% of screen width
                val height = ViewGroup.LayoutParams.WRAP_CONTENT  // Auto-adjust the height
                dialog.window?.setLayout(width, height)

                // Setup button click listener here
                binding.btnbtnAddProductAddProduct.setOnClickListener {
                    Log.d("clicked","clicked")  // Debug line
                    addProduct()
                    dismiss()
                }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    private fun addProduct() {
        val name = binding.editProductName.text.toString()
        val priceText = binding.editProductPrice.text.toString()
        val quantityText = binding.editProductQuantity.text.toString()
        val date = binding.editProductDate.text.toString()
        val time = binding.editProductTime.text.toString()

        // Validation
        if (name.isBlank() || priceText.isBlank() || quantityText.isBlank()) {
            Toast.makeText(context, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show()
            return
        }

        val price: Double
        val quantity: Int

        try {
            price = priceText.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "ราคาไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            quantity = quantityText.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "จำนวนไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
            return
        }

        val newProduct = NewProduct(0, name, price, quantity, date, time)
        Log.d("AddProduct", "Inserting: $newProduct")

        newProductViewModel.insert(newProduct)
        dismiss()
    }



    companion object {
        @JvmStatic
        fun newInstance() = AddProductDialogFragment()
    }
}
