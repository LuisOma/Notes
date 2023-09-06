package com.example.notes.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.data.local.entities.NoteEntity
import com.example.notes.databinding.ItemNotesBinding

class NotesAdapter : ListAdapter<NoteEntity, NotesAdapter.NotesViewHolder>(diffCallback) {

    private lateinit var listener: OnItemClickListener

    inner class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemNotesBinding.bind(view)

        fun bind(note: NoteEntity) {
            with(binding) {
                tvTitle.text = note.title
                tvDesc.text = note.content
                tvDateTime.text = note.dateTime

                cvItem.setOnClickListener {
                    listener.onClicked(note.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binding = ItemNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(binding.root)
    }


    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOnClickListener(listener1: OnItemClickListener) {
        listener = listener1
    }

    interface OnItemClickListener {
        fun onClicked(notesId: Int)
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<NoteEntity>() {
            override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}
