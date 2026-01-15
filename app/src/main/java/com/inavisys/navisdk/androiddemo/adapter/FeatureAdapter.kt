package com.inavisys.navisdk.androiddemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inavisys.navisdk.androiddemo.databinding.ItemMainFeatureBinding
import com.inavisys.navisdk.androiddemo.databinding.ItemMainFeatureHeaderBinding
import com.inavisys.navisdk.androiddemo.utils.Feature

class FeatureAdapter(
    private val features: List<Feature>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder = when (viewType) {
        HEADER_VIEW_TYPE -> HeaderViewHolder(
            ItemMainFeatureHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        else -> MainViewHolder(
            ItemMainFeatureBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val feature = features.getOrNull(position) ?: return

        when (holder) {
            is HeaderViewHolder -> holder.bind(feature)
            is MainViewHolder -> holder.bind(feature)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (features.getOrNull(position)?.isHeaderItem == true) {
            true -> 0
            false -> 1
        }
    }

    override fun getItemCount(): Int {
        return features.size
    }

    class HeaderViewHolder(
        private val binding: ItemMainFeatureHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(feature: Feature) {
            binding.nameView.text = feature.getFeatureLabel()
            binding.descriptionView.text = feature.getFeatureDescription()
        }
    }

    class MainViewHolder(
        private val binding: ItemMainFeatureBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(feature: Feature) {
            binding.nameView.text = feature.getFeatureLabel()
            binding.descriptionView.text = feature.getFeatureDescription()
        }
    }

    companion object {
        private const val HEADER_VIEW_TYPE = 0
    }
}