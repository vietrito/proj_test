package com.example.myapplication.ui.detail

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.base.BaseActivity
import com.example.myapplication.databinding.ActivityDetailBinding
import com.example.myapplication.network.models.DetailResponse
import com.example.myapplication.ui.detail.adapter.SectionsAdapter
import com.example.myapplication.ui.detail.vm.DetailViewModel
import com.example.myapplication.util.gone
import com.example.myapplication.util.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : BaseActivity<ActivityDetailBinding>() {

    private val viewModel: DetailViewModel by viewModels()

    private lateinit var sectionsAdapter: SectionsAdapter

    override fun setBinding(layoutInflater: LayoutInflater): ActivityDetailBinding {
        return ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun initView() {
        setupToolbar()
        setupRecyclerView()
        loadDetailData()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        sectionsAdapter = SectionsAdapter()
        binding.rvSections.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity)
            adapter = sectionsAdapter
        }
    }

    private fun loadDetailData() {
        viewModel.loadDetailData()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    Log.d("DetailActivity", "Loading detail data...")
                }
            }
        }

        lifecycleScope.launch {
            viewModel.detailData.collect { detailResponse ->
                detailResponse?.let { displayDetailData(it) }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                if (errorMessage.isNotEmpty()) {
                    binding.tvDetailTitle.text = "Error: $errorMessage"
                }
            }
        }
    }

    private fun displayDetailData(detailResponse: DetailResponse) {

        binding.tvDetailTitle.text = detailResponse.title
        binding.tvDetailDescription.text = detailResponse.description
        binding.tvDetailPublisher.text = "Publisher: ${detailResponse.publisher.name}"
        binding.tvDetailDate.text = "Published: ${detailResponse.publishedDate}"

        detailResponse.avatar?.let { avatar ->
            binding.ivDetailImage.loadImage(avatar.href)

        } ?: run {
            binding.ivDetailImage.gone()
        }

        detailResponse.templateType?.let { template ->
            Log.d("DetailActivity", "Template type: $template")
        }
        sectionsAdapter.submitList(detailResponse.sections)
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
            return Intent(context, DetailActivity::class.java)
        }
    }
}
