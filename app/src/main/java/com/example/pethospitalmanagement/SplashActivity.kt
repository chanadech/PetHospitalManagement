package com.example.pethospitalmanagement.presentation.splashscreen
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pethospitalmanagement.R
import com.example.pethospitalmanagement.presentation.admin.AdminActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        viewModel.navigateToMain.observe(this) { navigate ->
            if (navigate) {
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}

