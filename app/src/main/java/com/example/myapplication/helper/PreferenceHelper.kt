package com.currency.currencyconverter.currencyexchangeapp.helper

import android.content.Context
import android.content.Context.MODE_PRIVATE

import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


interface SharedPreferenceHelper {
    fun setString(key: String, value: String)
    fun getString(key: String): String?

    fun setInt(key: String, value: Int)
    fun getInt(key: String): Int?

    fun setBoolean(key: String, value: Boolean)
    fun getBoolean(key: String): Boolean?

}

@Singleton
class PreferenceHelper @Inject constructor(
    @ApplicationContext context: Context
) : SharedPreferenceHelper {
    companion object {
        const val APP_PREFS = "app_prefs"

    }


    private val sharedPreferences by lazy {
        context.getSharedPreferences(APP_PREFS, MODE_PRIVATE)
    }

    fun hidePermission() {
        sharedPreferences.edit().putBoolean("show_permission", false).commit()
    }

    fun showPermission(): Boolean {
        return sharedPreferences.getBoolean("show_permission", true)
    }

    fun hideFavourite() {
        sharedPreferences.edit().putBoolean("show_favourite", false).commit()
    }

    fun forceRated() {
        sharedPreferences.edit().putBoolean("rate", true).commit()
    }

    fun isRate(): Boolean {
        return sharedPreferences.getBoolean("rate", false)
    }
    fun increaseCountRate() {
        sharedPreferences.edit().putInt("COUNT_RATE", sharedPreferences.getInt("COUNT_RATE", 1) + 1).apply()
    }


    override fun setString(key: String, value: String) {
        sharedPreferences
            .edit()
            .putString(key, value)
            .apply()
    }

    override fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override fun setInt(key: String, value: Int) {
        sharedPreferences
            .edit()
            .putInt(key, value)
            .apply()
    }

    override fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    override fun setBoolean(key: String, value: Boolean) {
        sharedPreferences
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    override fun getBoolean(key: String): Boolean? {
        return sharedPreferences.getBoolean(key, false)
    }
}