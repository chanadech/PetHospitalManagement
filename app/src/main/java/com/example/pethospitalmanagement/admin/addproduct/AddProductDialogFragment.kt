import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
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
import java.util.Calendar

class AddProductDialogFragment : DialogFragment() {

    // Initialize ViewBinding
    private lateinit var binding: FragmentAddProductDialogBinding

    // Initialize ViewModel
    private lateinit var newProductViewModel: NewProductViewModel

    // Mutable variable to hold the product being edited
    private var productToEdit: NewProduct? = null  // <-- Add this line

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = FragmentAddProductDialogBinding.inflate(LayoutInflater.from(context))

        // Initialize ViewModel
        val application = requireNotNull(this.activity).application
        val dataSource = AppDatabase.getDatabase(application).newProductDao()
        val viewModelFactory = NewProductViewModelFactory(dataSource, application)
        newProductViewModel = ViewModelProvider(this, viewModelFactory).get(NewProductViewModel::class.java)
// Added: OnClickListener for btnTime to show DatePickerDialog
        binding.btnTime.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(requireActivity(), R.style.CustomDatePickerDialog, DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                // Set the selected date to editProductDate EditText
                binding.editProductDate.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")


            }, year, month, day)

            dpd.show()
        }

        binding.btnDate.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            val tpd = TimePickerDialog(requireActivity(),R.style.CustomTimePickerDialog, TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                // 2. Set the selected time to editProductTime EditText
                val formattedMinute = String.format("%02d", selectedMinute)
                val formattedHour = String.format("%02d", selectedHour)
                binding.editProductTime.setText("$formattedHour:$formattedMinute")
            }, hour, minute, true)

            tpd.show()
        }



        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.AlertDialogCustom)
            builder.setView(binding.root)
            builder.setTitle("เพิ่มสินค้า")
            val dialog = builder.create()

            dialog.setOnShowListener {
                val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
                val height = ViewGroup.LayoutParams.WRAP_CONTENT
                dialog.window?.setLayout(width, height)

                // Pre-fill the form if editing an existing product
                productToEdit?.let { product ->
                    binding.editProductName.setText(product.name)
                    binding.editProductPrice.setText(product.price.toString())
                    binding.editProductQuantity.setText(product.quantity.toString())

                    // Update the editProductDate based on productToEdit
                    productToEdit?.let { product ->
                        binding.editProductDate.setText(product.date)
                    }}

                binding.btnAddProductAddProduct.setOnClickListener {
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

        val datePattern = "^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d$"
        if (date.isNotEmpty() && !date.matches(datePattern.toRegex())) {
            Toast.makeText(context, "โปรดกรอกวันที่ให้ถูกต้อง", Toast.LENGTH_SHORT).show()
            return
        }
        val timePattern = "([01]?[0-9]|2[0-3]):[0-5][0-9]"
        if (time.isNotEmpty() && !time.matches(timePattern.toRegex())) {
            Toast.makeText(context, "โปรดกรอกเวลาให้ถูกต้อง", Toast.LENGTH_SHORT).show()
            return
        }

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

        // Check if we are editing an existing product or adding a new one
        val newProduct = if (productToEdit != null) {
            NewProduct(productToEdit!!.id, name, price, quantity, date, time)
        } else {
            NewProduct(0, name, price, quantity, date, time)
        }

        // Either update the existing product or insert a new one
        if (productToEdit != null) {
            newProductViewModel.update(newProduct)
        } else {
            newProductViewModel.insert(newProduct)
        }

        dismiss()
    }

    // Set the product to be edited
    fun setProductToEdit(newProduct: NewProduct) {
        this.productToEdit = newProduct
    }
}
