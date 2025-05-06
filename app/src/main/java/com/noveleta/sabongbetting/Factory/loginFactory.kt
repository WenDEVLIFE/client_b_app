package com.noveleta.sabongbetting.Factory;

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.noveleta.sabongbetting.Helper.*
import android.app.Application
import android.util.Log

class AccountLogInViewModelFactory(
    private val context: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return try {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                LoginViewModel(context) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } catch (e: Exception) {
            Log.e("ViewModelFactory", "Error creating ViewModel: ${e.message}")
            throw RuntimeException("ViewModel creation failed", e)
        }
    }
}

