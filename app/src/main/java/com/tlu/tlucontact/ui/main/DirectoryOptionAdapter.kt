// DirectoryOptionAdapter.kt
package com.tlu.tlucontact.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tlu.tlucontact.data.model.DirectoryOption
import com.tlu.tlucontact.databinding.ItemDirectoryOptionBinding

class DirectoryOptionAdapter(
    private val onItemClick: (DirectoryOption) -> Unit
) : RecyclerView.Adapter<DirectoryOptionAdapter.ViewHolder>() {

    private val items = mutableListOf<DirectoryOption>()

    fun setItems(newItems: List<DirectoryOption>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDirectoryOptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemDirectoryOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position])
                }
            }
        }

        fun bind(option: DirectoryOption) {
            binding.ivDirectoryIcon.setImageResource(option.iconRes)
            binding.tvDirectoryTitle.text = option.title
            binding.tvDirectoryDescription.text = option.description
        }
    }
}