package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.network.api.NewsApiService
import com.example.myapplication.network.models.DetailResponse
import com.example.myapplication.network.models.NewsItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val apiService: NewsApiService
) {
    private var cachedNewsFeed: List<NewsItem> = emptyList()
    private var isCacheLoaded = false

    fun getNewsFeedFlow(): Flow<List<NewsItem>> = flow {
        try {
            val response = apiService.getNewsFeed()
            val items = response.items
            cachedNewsFeed = items
            isCacheLoaded = true

            emit(items)
        } catch (e: Exception) {
            Log.e("NewsRepository", "getNewsFeedFlow error: ${e.message}", e)
            emit(emptyList())
        }
    }

    fun getDetailsFlow(): Flow<DetailResponse> = flow {
        try {
            val response = apiService.getDetails()
            emit(response)
        } catch (e: Exception) {
            Log.e("NewsRepository", "getDetailsFlow error: ${e.message}", e)
            emit(DetailResponse())
        }
    }

    suspend fun getNewsFeed(): List<NewsItem> {
        return try {
            val response = apiService.getNewsFeed()
            response.items
        } catch (e: Exception) {
            Log.e("NewsRepository", "getNewsFeed error: ${e.message}", e)
            emptyList()
        }
    }



    suspend fun searchNewsByTitle(query: String): List<NewsItem> {
        if (!isCacheLoaded) {
            Log.d("NewsRepository", "Cache not loaded, fetching from API")
            getNewsFeed()
        }
        return cachedNewsFeed.filter { item ->
            item.title.contains(query, ignoreCase = true)
        }.also { results ->
            Log.d("NewsRepository", "Search found ${results.size} results for '$query'")
        }
    }


}
