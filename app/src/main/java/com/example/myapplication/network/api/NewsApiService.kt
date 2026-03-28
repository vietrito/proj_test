package com.example.myapplication.network.api

import com.example.myapplication.network.models.NewsFeedResponse
import com.example.myapplication.network.models.DetailResponse
import retrofit2.http.GET

interface NewsApiService {
    @GET("newsfeed.json")
    suspend fun getNewsFeed(): NewsFeedResponse

    @GET("detail.json")
    suspend fun getDetails(): DetailResponse
}

