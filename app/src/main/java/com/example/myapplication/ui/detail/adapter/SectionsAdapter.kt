package com.example.myapplication.ui.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemSectionImageBinding
import com.example.myapplication.databinding.ItemSectionTextBinding
import com.example.myapplication.databinding.ItemSectionVideoBinding
import com.example.myapplication.network.models.DetailMarkup
import com.example.myapplication.network.models.DetailSection
import com.example.myapplication.util.gone
import com.example.myapplication.util.loadImage
import com.example.myapplication.util.visible

class SectionsAdapter : ListAdapter<DetailSection, RecyclerView.ViewHolder>(SectionDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_TEXT = 1
        private const val VIEW_TYPE_VIDEO = 2
        private const val VIEW_TYPE_IMAGE = 3
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).sectionType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TEXT -> {
                val binding = ItemSectionTextBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextSectionViewHolder(binding)
            }
            VIEW_TYPE_VIDEO -> {
                val binding = ItemSectionVideoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                VideoSectionViewHolder(binding)
            }
            VIEW_TYPE_IMAGE -> {
                val binding = ItemSectionImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ImageSectionViewHolder(binding)
            }
            else -> {
                val binding = ItemSectionTextBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TextSectionViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val section = getItem(position)
        when (holder) {
            is TextSectionViewHolder -> holder.bind(section)
            is VideoSectionViewHolder -> holder.bind(section)
            is ImageSectionViewHolder -> holder.bind(section)
        }
    }

    class TextSectionViewHolder(
        private val binding: ItemSectionTextBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(section: DetailSection) {
            val content = section.content
            var displayText = content.text ?: ""

            // Process markups if available
            content.markups?.let { markups ->
                displayText = applyMarkups(displayText, markups)
            }

            binding.tvSectionText.text = displayText
        }

        private fun applyMarkups(text: String, markups: List<DetailMarkup>): String {
            if (text.isEmpty() || markups.isEmpty()) return text


            return text
        }
    }

    class VideoSectionViewHolder(
        private val binding: ItemSectionVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(section: DetailSection) {
            val content = section.content

            content.previewImage?.let { preview ->
                binding.ivVideoPreview.loadImage(preview.href)
            } ?: run {
                binding.ivVideoPreview.setImageResource(R.drawable.ic_launcher_background)
            }

            binding.tvVideoCaption.text = content.caption ?: ""

            content.duration?.let { duration ->
                binding.tvVideoDuration.text = formatDuration(duration)
                binding.tvVideoDuration.visible()
            } ?: run {
                binding.tvVideoDuration.gone()
            }
        }

        private fun formatDuration(seconds: Int): String {
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return String.format("%d:%02d", minutes, remainingSeconds)
        }
    }

    class ImageSectionViewHolder(
        private val binding: ItemSectionImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(section: DetailSection) {
            val content = section.content


            content.href?.let { imageUrl ->
                binding.ivSectionImage.loadImage(imageUrl)
            } ?: run {
                binding.ivSectionImage.setImageResource(R.drawable.ic_launcher_background)
            }


            binding.tvImageCaption.text = content.caption ?: ""
        }
    }

    class SectionDiffCallback : DiffUtil.ItemCallback<DetailSection>() {
        override fun areItemsTheSame(oldItem: DetailSection, newItem: DetailSection): Boolean {
            return oldItem.sectionType == newItem.sectionType &&
                   oldItem.content.text == newItem.content.text &&
                   oldItem.content.href == newItem.content.href
        }

        override fun areContentsTheSame(oldItem: DetailSection, newItem: DetailSection): Boolean {
            return oldItem == newItem
        }
    }
}
