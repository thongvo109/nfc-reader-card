package com.corenfc

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.corenfc.model.DataQrCode


class ViewModelFactory(private val qrCodeData: DataQrCode) : ViewModelProvider.NewInstanceFactory()  {

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReaderCardViewModel(qrCodeData) as T
    }
}