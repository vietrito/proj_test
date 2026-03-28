package com.example.myapplication.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.currency.currencyconverter.currencyexchangeapp.helper.PreferenceHelper
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.ui.home.HomeFragment
import com.example.myapplication.ui.profile.ProfileFragment
import com.example.myapplication.ui.main.vm.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var preferenceHelper: PreferenceHelper


    override fun setBinding(layoutInflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        setupBottomNavigation()
        // Load home fragment by default
        if (supportFragmentManager.findFragmentById(binding.fragmentContainer.id) == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                com.example.myapplication.R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                com.example.myapplication.R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
