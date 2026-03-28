package com.example.myapplication.ui.detail.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.models.DetailResponse
import com.example.myapplication.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _detailData = MutableStateFlow<DetailResponse?>(null)
    val detailData: StateFlow<DetailResponse?> = _detailData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    fun loadDetailData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getDetailsFlow().collect { detailResponse ->

                    _detailData.value = detailResponse
                    _errorMessage.value = ""

                }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error loading detail data: ${e.message}", e)
                _errorMessage.value = e.message ?: "Failed to load detail data"
                _detailData.value = null
            } finally {
                _isLoading.value = false
                Log.d("DetailViewModel", "loadDetailData() finished")
            }
        }
    }
}