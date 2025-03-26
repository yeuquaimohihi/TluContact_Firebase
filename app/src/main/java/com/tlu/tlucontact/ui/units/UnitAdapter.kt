package com.tlu.tlucontact.ui.units

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tlu.tlucontact.R
import com.tlu.tlucontact.data.model.OrganizationalUnit
import com.tlu.tlucontact.databinding.ItemUnitBinding
import com.tlu.tlucontact.util.ImageLoader

class UnitAdapter(
    private val onItemClick: (OrganizationalUnit) -> Unit
) : RecyclerView.Adapter<UnitAdapter.ViewHolder>() {

    private val items = mutableListOf<OrganizationalUnit>()

    fun setItems(newItems: List<OrganizationalUnit>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUnitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemUnitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position])
                }
            }
        }

        fun bind(unit: OrganizationalUnit) {
            binding.tvUnitName.text = unit.name
            binding.tvUnitCode.text = unit.code

            // Load unit logo
            if (unit.logoURL.isNotEmpty()) {
                ImageLoader.loadImage(binding.ivUnitLogo, unit.logoURL, R.drawable.ic_unit)
            } else {
                binding.ivUnitLogo.setImageResource(R.drawable.ic_unit)
            }
        }
    }
}