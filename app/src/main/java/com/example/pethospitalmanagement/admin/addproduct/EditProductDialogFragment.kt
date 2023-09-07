package com.example.pethospitalmanagement.presentation.admin.addproduct
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.example.pethospitalmanagement.data.db.Product
import com.example.pethospitalmanagement.databinding.FragmentDialogBinding
import com.example.pethospitalmanagement.presentation.ProductViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProductDialogFragment(private val product: Product? = null) : DialogFragment() {

    private lateinit var binding: FragmentDialogBinding
    private val productViewModel: ProductViewModel by sharedViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = FragmentDialogBinding.inflate(LayoutInflater.from(context))
        val isEditing = product != null


        product?.let {
            binding.editProductName.setText(it.name)
            binding.editProductType.setText(it.type)
            binding.editProductDetails.setText(it.details)
            binding.editProductPrice.setText(it.price.toString())
            binding.editProductImagePath.setText(it.imagePath)


            try {
                val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val targetFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                val date = originalFormat.parse(it.selectedDate)
                val formattedDate = targetFormat.format(date!!)
                binding.tvSelectedDate.text = "Selected Date: $formattedDate"
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvSelectedDate.text = "Selected Date: ${it.selectedDate}"
            }


            binding.tvSelectedtime.text = "Selected Time: ${it.selectedTime}"// t the previously selected date
            binding.editTelephone.setText(it.telephone)
        }

        // Date Picker Dialog`
        binding.btnDatePicker.setOnClickListener {
            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)
            val picker = DatePickerDialog(requireContext(),
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                     binding.tvSelectedDate.text = "Selected Date: $selectedDate"
                },
                year, month, day)
            picker.show()
        }
        binding.btnTimePicker.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val selectedTime = "$hourOfDay:$minute"
                binding.tvSelectedtime.setText("Selected Time: ${selectedTime}")
            }, hour, minute, true)

            timePicker.show()
        }


        binding.imgButton.setOnClickListener {
            // Open gallery
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setTitle(if (isEditing) "Edit Details" else "Add Details")
            .setPositiveButton("Save") { _, _ ->
                val name = binding.editProductName.text.toString()
                val type = binding.editProductType.text.toString()
                val details = binding.editProductDetails.text.toString()
                val priceString = binding.editProductPrice.text.toString()
                val imagePath = binding.editProductImagePath.text.toString()

                // Validate inputs
                if (name.isEmpty() || type.isEmpty() || details.isEmpty() || priceString.isEmpty()) {
                    // Show some validation error
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val price = priceString.toDouble() // Safe to convert now
                val selectedDate =  binding.tvSelectedDate.text.removePrefix("Selected Date: ").toString()
                val selectedTime = binding.tvSelectedtime.text.removePrefix("Selected Time: ").toString()
                val telephone = binding.editTelephone.text.toString()

                // Validate telephone number format
                val regex = "^[0-9]{10}$".toRegex()  // The telephone number should have 10 digits
                if (telephone.isNotEmpty() && !regex.matches(telephone)) {
                    Toast.makeText(context, "Invalid telephone number. Please enter a valid 10-digit number.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newProduct = Product(
                    id = product?.id ?: 0,
                    name = name,
                    type = type,
                    details = details,
                    price = price,
                    imagePath = imagePath,
                    selectedDate = selectedDate,
                    selectedTime = selectedTime,
                    telephone = if (telephone.isEmpty()) null else telephone  // ถ้าไม่มีข้อมูล ให้ใช้ค่า null

                )

                if (isEditing) {
                    productViewModel.update(newProduct)
                } else {
                    productViewModel.insert(newProduct)
                }
                productViewModel.fetchAllProducts()
                dismiss()
            }

            .create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            binding.editProductImagePath.setText(imageUri.toString())
        }
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 101
    }
}