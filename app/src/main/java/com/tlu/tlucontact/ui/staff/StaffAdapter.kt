// StaffAdapter.kt
package com.tlu.tlucontact.ui.staff

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tlu.tlucontact.R
import com.tlu.tlucontact.data.model.Staff
import com.tlu.tlucontact.databinding.ItemStaffBinding
import com.tlu.tlucontact.util.ImageLoader

class StaffAdapter(
    private val onItemClick: (Staff) -> Unit
) : RecyclerView.Adapter<StaffAdapter.ViewHolder>() {

    private val items = mutableListOf<Staff>()

    fun setItems(newItems: List<Staff>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStaffBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemStaffBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position])
                }
            }
        }

        fun bind(staff: Staff) {
            binding.tvStaffName.text = staff.fullName
            binding.tvStaffPosition.text = staff.position

            // Load unit name asynchronously if reference exists
            staff.unit?.get()?.addOnSuccessListener { documentSnapshot ->
                val unitName = documentSnapshot.getString("name") ?: ""
                binding.tvStaffUnit.text = unitName
            }

            // Load staff avatar
            if (staff.photoURL.isNotEmpty()) {
                ImageLoader.loadImage(binding.ivStaffAvatar, staff.photoURL, R.drawable.default_avatar)
            } else {
                binding.ivStaffAvatar.setImageResource(R.drawable.default_avatar)
            }
        }
    }
}