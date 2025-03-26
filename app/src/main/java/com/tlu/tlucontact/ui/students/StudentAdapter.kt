// StudentAdapter.kt
package com.tlu.tlucontact.ui.student

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tlu.tlucontact.R
import com.tlu.tlucontact.data.model.Student
import com.tlu.tlucontact.databinding.ItemStudentBinding
import com.tlu.tlucontact.util.ImageLoader

class StudentAdapter(
    private val onItemClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    private val items = mutableListOf<Student>()

    fun setItems(newItems: List<Student>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStudentBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(items[position])
                }
            }
        }

        // Inside StudentAdapter.kt, in the ViewHolder's bind method
        fun bind(student: Student) {
            binding.tvStudentName.text = student.fullName
            binding.tvStudentId.text = student.studentId

            // Load class name asynchronously if reference exists
            if (student.classInfo != null) {
                student.classInfo.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val className = documentSnapshot.getString("name") ?: ""
                        binding.tvStudentClass.text = className
                    } else {
                        binding.tvStudentClass.text = "N/A"
                    }
                }.addOnFailureListener {
                    binding.tvStudentClass.text = "N/A"
                }
            } else {
                binding.tvStudentClass.text = "N/A"
            }

            // Load student avatar
            if (student.photoURL.isNotEmpty()) {
                ImageLoader.loadImage(binding.ivStudentAvatar, student.photoURL, R.drawable.default_avatar)
            } else {
                binding.ivStudentAvatar.setImageResource(R.drawable.default_avatar)
            }
        }
    }
}