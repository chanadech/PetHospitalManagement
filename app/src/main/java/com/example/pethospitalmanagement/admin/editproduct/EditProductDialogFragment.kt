package com.example.pethospitalmanagement.admin.editproduct
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.example.pethospitalmanagement.R
import com.example.pethospitalmanagement.data.db.Product
import com.example.pethospitalmanagement.databinding.FragmentDialogBinding
import com.example.pethospitalmanagement.admin.ProductViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProductDialogFragment(private val product: Product? = null) : DialogFragment() {

    private lateinit var binding: FragmentDialogBinding
    private val productViewModel: ProductViewModel by sharedViewModel()
    private var selectedImageUri: Uri? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = FragmentDialogBinding.inflate(LayoutInflater.from(context))
        val isEditing = product != null


        product?.let {
            binding.editProductName.setText(it.name)
            binding.editProductType.setText(it.type)
            binding.editProductDetails.setText(it.details)
            binding.editProductPrice.setText(it.price.toString())
            selectedImageUri = Uri.parse(it.imagePath)

            if (it.selectedDate.isNotEmpty()) {
                try {
                    val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val targetFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                    val date = originalFormat.parse(it.selectedDate)
                    val formattedDate = targetFormat.format(date!!)
                    binding.tvSelectedDate.text = "วันที่เลือก: $formattedDate"
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.tvSelectedDate.text = "วันที่เลือก: ${it.selectedDate}"
                }
            } else {
                // Set placeholder if no date is selected
                binding.tvSelectedDate.text = "วันที่เลือก: -"
            }


            binding.tvSelectedtime.text = "เวลาที่เลือก: ${it.selectedTime}" // t the previously selected date
            binding.editTelephone.setText(it.telephone)
        }

        // Date Picker Dialog`
        binding.btnDatePicker.setOnClickListener {
            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)
            val picker = DatePickerDialog(requireContext(), R.style.CustomDatePickerDialog,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
                    binding.tvSelectedDate.text = "วันที่เลือก: $selectedDate"
                },
                year, month, day)
            picker.show()
        }
        binding.btnTimePicker.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(context, R.style.CustomDatePickerDialog, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val selectedTime = "$hourOfDay:$minute"
                binding.tvSelectedtime.setText("เวลาที่เลือก: ${selectedTime}")
            }, hour, minute, true)

            timePicker.show()
        }

        binding.imgButton.setOnClickListener {
            // Open gallery
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        val alertDialog = AlertDialog.Builder(requireActivity(), R.style.AlertDialogCustom)
            .setView(binding.root)
            .setTitle(if (isEditing) "แก้ไขข้อมูล" else "เพิ่มข้อมูลสัตว์เลี้ยง")
            .setPositiveButton("ยืนยัน") { _, _ ->

                val name = binding.editProductName.text.toString()
                val type = binding.editProductType.text.toString()
                val details = binding.editProductDetails.text.toString()
                val priceString = binding.editProductPrice.text.toString()
                val imagePath = selectedImageUri?.toString() ?: ""

                // Validate inputs
                if (name.isEmpty() || type.isEmpty() || details.isEmpty() || priceString.isEmpty()) {
                    // Show some validation error
                    Toast.makeText(context, "โปรดกรอกข้อมูลให้ครบ", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val price = priceString.toDouble() // Safe to convert now
                val selectedDate =  binding.tvSelectedDate.text.removePrefix("วันที่เลือก: ").toString()
                val selectedTime = binding.tvSelectedtime.text.removePrefix("เวลาที่เลือก: ").toString()
                val telephone = binding.editTelephone.text.toString()

                // Validate telephone number format
                val regex = "^[0-9]{10}$".toRegex()  // The telephone number should have 10 digits
                if (telephone.isNotEmpty() && !regex.matches(telephone)) {
                    Toast.makeText(context, "โปรดกรอกเบอร์โทรที่ถูกต้อง", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newProduct = Product(
                    id = product?.id ?: 0,
                    name = name,
                    type = type,
                    details = details,
                    price = price,
                    imagePath = imagePath,
                    selectedDate = if (selectedDate != "-") selectedDate else "",
                    selectedTime = selectedTime,
                    telephone = if (telephone.isEmpty()) null else telephone  // ถ้าไม่มีข้อมูล ให้ใช้ค่า null

                )

                Log.d("EditProductDialogFragment", "New Product: $newProduct")


                if (isEditing) {
                    productViewModel.update(newProduct)
                } else {
                    productViewModel.insert(newProduct)
                }
                productViewModel.fetchAllProducts()
                dismiss()
            }

            .setNegativeButton("ยกเลิก") { _, _ -> dismiss() }
            .create()

        alertDialog.setOnShowListener {
            val titleView = alertDialog.findViewById<TextView>(android.R.id.title)
            titleView?.setTextColor(resources.getColor(R.color.dialog_title_color))
        }

        return alertDialog
    }

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
        selectedImageUri = data?.data
        binding.editProductImagePath.apply {
            setText("เลือกรูปภาพ")
            setTextColor(resources.getColor(R.color.green))
        }
    }
}


    companion object {
        private const val GALLERY_REQUEST_CODE = 101
    }
}
