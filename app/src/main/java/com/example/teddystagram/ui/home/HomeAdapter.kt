package com.example.teddystagram.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teddystagram.R
import com.example.teddystagram.databinding.ItemDetailBinding
import com.example.teddystagram.model.ContentDTO
import com.example.teddystagram.model.HomeContent
import kotlinx.android.synthetic.main.item_detail.view.*

class HomeAdapter(
    private val homeEventListener: HomeEventListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var homeContents: ArrayList<HomeContent> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val holder = DetailViewHolder(
            ItemDetailBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            homeEventListener
        )

        return holder
    }

    override fun getItemCount(): Int {
        return homeContents.size
    }

    fun submitList(homeContents: ArrayList<HomeContent>) {
        this.homeContents.clear()
        this.homeContents.addAll(homeContents
        )
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        (p0 as DetailViewHolder).bind(this.homeContents[p1])
    }

    class DetailViewHolder(
        private val binding: ItemDetailBinding,
        private val homeEventListener: HomeEventListener
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(homeContent: HomeContent) {
            binding.data = homeContent
            binding.eventListener = homeEventListener

            if (homeContent.favorites.containsKey(homeContent.snapshotId)) {
                binding.ivLike.setImageResource(R.drawable.ic_favorite)
            } else {
                binding.ivLike.setImageResource(R.drawable.ic_favorite_border)
            }
        }
    }
}