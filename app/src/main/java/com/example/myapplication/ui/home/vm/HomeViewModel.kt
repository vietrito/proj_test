package com.example.myapplication.ui.home.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.models.NewsItem
import com.example.myapplication.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _newsFeed = MutableStateFlow<List<NewsItem>>(emptyList())
    val newsFeed: StateFlow<List<NewsItem>> = _newsFeed

    private val _searchResults = MutableStateFlow<List<NewsItem>>(emptyList())
    val searchResults: StateFlow<List<NewsItem>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    init {
        loadNewsFeed()
    }

    fun loadNewsFeed() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getNewsFeedFlow().collect { feed ->
                    Log.d("NewsViewModel", "data size : ${feed.size} ")
                    _newsFeed.value = feed
                    _searchResults.value = feed
                    _errorMessage.value = ""
                }
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error loading feed: ${e.message}", e)
                _errorMessage.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchByTitle(query: String) {
        viewModelScope.launch {
            try {
                if (query.isEmpty()) {
                    _searchResults.value = _newsFeed.value
                } else {
                    val filtered = repository.searchNewsByTitle(query)
                    _searchResults.value = filtered
                }
                _errorMessage.value = ""
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Search error: ${e.message}", e)
                _errorMessage.value = e.message ?: "Search failed"
            }
        }
    }

    fun getNewsItemById(documentId: String): NewsItem? {
        return _newsFeed.value.firstOrNull { it.documentId == documentId }
    }
}