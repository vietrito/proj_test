package com.example.myapplication.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.detail.DetailActivity
import com.example.myapplication.ui.home.vm.HomeViewModel
import com.example.myapplication.ui.home.adapter.NewsAdapter
import com.example.myapplication.util.gone
import com.example.myapplication.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var newsAdapter: NewsAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter ({ newsItem ->
            startActivity(DetailActivity.newIntent(requireContext()))
        })

        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupSearch() {
        lifecycleScope.launch {
            callbackFlow {
                val textWatcher = binding.etSearch.addTextChangedListener { editable ->
                    trySend(editable.toString().trim())
                }
                awaitClose { binding.etSearch.removeTextChangedListener(textWatcher) }
            }
            .debounce(300)
            .distinctUntilChanged()
            .collectLatest { query ->
                viewModel.searchByTitle(query)
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    binding.progressBar.visible()
                } else {
                    binding.progressBar.gone()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.searchResults.collect { items ->
                Log.d("viet", "observeViewModel: Search results received ${items.size} items")
                newsAdapter.submitList(items)
                if (items.isEmpty() && binding.etSearch.text.isNotEmpty()) {
                    binding.tvNoResults.visible()
                } else {
                    binding.tvNoResults.gone()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { message ->
                if (message.isNotEmpty()) {
                    Log.e("viet", "Error: $message")
                    binding.tvError.text = message
                    binding.tvError.visible()
                } else {
                    binding.tvError.gone()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

