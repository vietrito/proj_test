package com.example.myapplication

import android.app.Application
import android.content.Context


import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {


    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

    }


    companion object {
        var appContext: Context? = null
            private set
    }


}