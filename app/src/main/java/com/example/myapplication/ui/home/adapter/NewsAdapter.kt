package com.example.myapplication.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemNewsBinding
import com.example.myapplication.databinding.ItemNewsGalleryBinding
import com.example.myapplication.databinding.ItemNewsOverviewBinding
import com.example.myapplication.databinding.ItemNewsVideoBinding
import com.example.myapplication.network.models.NewsItem
import com.example.myapplication.ui.home.adapter.ImageAdapter
import com.example.myapplication.util.loadImage
import com.example.myapplication.util.toDisplayDate

class NewsAdapter(
    private val onItemClick: (NewsItem) -> Unit,
) : ListAdapter<NewsItem, RecyclerView.ViewHolder>(NewsDiffCallback()) {

    companion object {
        private const val TYPE_ARTICLE = 0
        private const val TYPE_OVERVIEW = 1
        private const val TYPE_GALLERY = 2
        private const val TYPE_STORY = 3
        private const val TYPE_VIDEO = 4
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).contentType.lowercase()) {
            "overview" -> TYPE_OVERVIEW
            "gallery" -> TYPE_GALLERY
            "story" -> TYPE_STORY
            "video" -> TYPE_VIDEO
            else -> TYPE_ARTICLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_OVERVIEW-> {
                val binding = ItemNewsOverviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                OverViewViewHolder(binding, onItemClick)
            }
            TYPE_ARTICLE -> {
                val binding = ItemNewsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ArticleViewHolder(binding, onItemClick)
            }
            TYPE_GALLERY, TYPE_STORY -> {
                val binding = ItemNewsGalleryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                GalleryViewHolder(binding, onItemClick)
            }
            TYPE_VIDEO -> {
                val binding = ItemNewsVideoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                VideoViewHolder(binding, onItemClick)
            }
            else -> {
                val binding = ItemNewsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ArticleViewHolder(binding, onItemClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ArticleViewHolder -> holder.bind(getItem(position))
            is OverViewViewHolder -> holder.bind(getItem(position))
            is GalleryViewHolder -> holder.bind(getItem(position))
            is VideoViewHolder -> holder.bind(getItem(position))
        }
    }

    class OverViewViewHolder(
        private val binding: ItemNewsOverviewBinding,
        private val onItemClick: (NewsItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(newsItem: NewsItem) {
            binding.apply {
                tvTitle.text = newsItem.title
                tvDescription.text = newsItem.description
                tvPublisher.text = newsItem.publisher.name
                tvPublishedDate.text = newsItem.publishedDate.toDisplayDate()


                newsItem.avatar?.let { avatar ->
                    ivNewsImage.loadImage(newsItem.avatar.href)
                } ?: run {
                    ivNewsImage.setImageResource(R.drawable.ic_launcher_background)
                }

                root.setOnClickListener {
                    onItemClick(newsItem)
                }
            }
        }


    }

    class ArticleViewHolder(
        private val binding: ItemNewsBinding,
        private val onItemClick: (NewsItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(newsItem: NewsItem) {
            binding.apply {
                tvTitle.text = newsItem.title
                tvDescription.text = newsItem.description
                tvPublisher.text = newsItem.publisher.name
                tvPublishedDate.text = newsItem.publishedDate.toDisplayDate()

                newsItem.avatar?.let { avatar ->
                    ivNewsImage.loadImage(newsItem.avatar?.href)
                } ?: run {
                    ivNewsImage.setImageResource(R.drawable.ic_launcher_background)
                }

                root.setOnClickListener {
                    onItemClick(newsItem)
                }
            }
        }


    }


    class GalleryViewHolder(
        private val binding: ItemNewsGalleryBinding,
        private val onItemClick: (NewsItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val imageAdapter = ImageAdapter()
        init {
            binding.rvImages.apply {
                layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager.HORIZONTAL, false
                )
                adapter = imageAdapter
                setRecycledViewPool(RecyclerView.RecycledViewPool())
            }

        }

        fun bind(newsItem: NewsItem) {
            binding.apply {
                tvGalleryTitle.text = newsItem.title
                tvPublisher.text = newsItem.publisher.name
                tvPublishedDate.text = newsItem.publishedDate.toDisplayDate()


                val imageList = newsItem.images ?: emptyList()
                imageAdapter.submitList(imageList)

                root.setOnClickListener {
                    onItemClick(newsItem)
                }
            }
        }


    }


    class VideoViewHolder(
        private val binding: ItemNewsVideoBinding,
        private val onItemClick: (NewsItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(newsItem: NewsItem) {
            binding.apply {
                tvVideoTitle.text = newsItem.title
                tvPublisher.text = newsItem.publisher.name
                tvPublishedDate.text = newsItem.publishedDate.toDisplayDate()

                // Load video preview image from avatar if available
                newsItem.avatar?.let { avatar ->
                    ivVideoPreview.loadImage(avatar.href)
                } ?: run {
                    ivVideoPreview.setImageResource(R.drawable.ic_launcher_background)
                }

                root.setOnClickListener {
                    onItemClick(newsItem)
                }
            }
        }


    }



    class NewsDiffCallback : DiffUtil.ItemCallback<NewsItem>() {
        override fun areItemsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem.documentId == newItem.documentId
        }

        override fun areContentsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem == newItem
        }
    }
}

