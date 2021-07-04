package com.example.teddystagram.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teddystagram.R
import com.example.teddystagram.databinding.ItemDetailBinding
import com.example.teddystagram.model.HomeContent

class HomeAdapter(
    private val homeEventListener: HomeEventListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var homeContents: ArrayList<HomeContent> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder =
        DetailViewHolder(
            ItemDetailBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            homeEventListener
        )

    override fun getItemCount(): Int {
        return homeContents.size
    }

    fun submitList(homeContents: ArrayList<HomeContent>) {
        this.homeContents.clear()
        this.homeContents.addAll(homeContents)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DetailViewHolder).bind(this.homeContents[position])
    }

    class DetailViewHolder(
        private val binding: ItemDetailBinding,
        private val homeEventListener: HomeEventListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(homeContent: HomeContent) {
            binding.data = homeContent
            binding.eventListener = homeEventListener

            if (homeContent.favorites.containsKey(homeContent.uid)) {
                binding.ivLike.setImageResource(R.drawable.ic_favorite)
            } else {
                binding.ivLike.setImageResource(R.drawable.ic_favorite_border)
            }
        }
    }
}