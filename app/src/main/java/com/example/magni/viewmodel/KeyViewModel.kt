package com.example.magni.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.magni.database.AppDatabase
import com.example.magni.database.dao.KeyDao
import com.example.magni.model.KeyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KeyViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: KeyDao = AppDatabase.getDatabase(application).keyDao()

    fun insertKey(keyEntity: KeyEntity) = viewModelScope.launch {
        dao.insertKey(keyEntity)
    }

    val firstKey = liveData(Dispatchers.IO) {
        emit(dao.getFirstKey())
    }

}
