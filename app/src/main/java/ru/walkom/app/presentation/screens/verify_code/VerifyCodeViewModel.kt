package ru.walkom.app.presentation.screens.verify_code

import androidx.lifecycle.ViewModel
import javax.inject.Inject


class VerifyCodeViewModel @Inject constructor(): ViewModel() {

//    val code by lazy {  }

    fun checkCode(code: String): Boolean {
        return true
    }
}