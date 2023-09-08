package com.example.pethospitalmanagement.splahscreen

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean> get() = _navigateToMain

    init {
        // Simulating a network operation or data loading for 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            _navigateToMain.value = true
        }, 3000)
    }
}
