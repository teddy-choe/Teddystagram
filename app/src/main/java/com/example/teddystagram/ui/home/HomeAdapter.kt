package com.example.teddystagram.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teddystagram.R
import com.example.teddystagram.databinding.ItemDetailBinding
import com.example.teddystagram.model.HomeUiData

class HomeAdapter(
    private val homeEventListener: HomeEventListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var homeUiDataList: ArrayList<HomeUiData> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder =
        DetailViewHolder(
            ItemDetailBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            homeEventListener
        )

    override fun getItemCount(): Int {
        return homeUiDataList.size
    }

    fun submitList(homeUiData: ArrayList<HomeUiData>) {
        this.homeUiDataList.clear()
        this.homeUiDataList.addAll(homeUiData)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DetailViewHolder).bind(this.homeUiDataList[position])
    }

    class DetailViewHolder(
        private val binding: ItemDetailBinding,
        private val homeEventListener: HomeEventListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(homeUiData: HomeUiData) {
            binding.data = homeUiData
            binding.eventListener = homeEventListener

            if (homeUiData.contentDTO.favorites.containsKey(homeUiData.contentDTO.uid)) {
                binding.ivLike.setImageResource(R.drawable.ic_favorite)
            } else {
                binding.ivLike.setImageResource(R.drawable.ic_favorite_border)
            }
        }
    }
}