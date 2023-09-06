package com.example.notes.ui.create_notes

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.notes.R
import com.example.notes.databinding.FragmentNoteBinding
import com.example.notes.data.local.entities.NoteEntity
import com.example.notes.utils.extensions.EMPTY_STRING
import com.example.notes.utils.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateNoteFragment :
    Fragment(R.layout.fragment_note){

    private val binding by viewBinding(FragmentNoteBinding::bind)
    private val viewModel by viewModels<CreateNoteViewModel>()
    private var currentTime: String? = null
    private var isNew = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireArguments().getInt(getString(R.string.noteID), -1).also {
            if(it != -1) {
                viewModel.setNoteId(it)
                isNew = false
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateNoteFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        collectNotes()
    }

    private fun collectNotes() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        viewModel.note.collectLatest {
            it?.let(this@CreateNoteFragment::setNoteDataInUI)
        }
    }

    private fun setNoteDataInUI(note: NoteEntity) = binding.apply {
        etNoteTitle.setText(note.title)
        etNoteDesc.setText(note.content)
    }

    private fun initViews() = binding.apply {

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        currentTime = sdf.format(Date())

        tvDateTime.text = currentTime

        btnAdd.setOnClickListener {
            viewModel.note.value?.let { updateNote(it) } ?: saveNote()
        }

        imgBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        btnDelete.apply {
            if(isNew) this.visibility = View.INVISIBLE
            setOnClickListener { deleteNote() }
        }
    }

    private fun updateNote(note: NoteEntity) = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        note.apply {
            title = binding.etNoteTitle.text.toString()
            content = binding.etNoteDesc.text.toString()
            dateTime = currentTime
        }.also {
            viewModel.updateNote(it)
        }
        binding.etNoteTitle.setText(EMPTY_STRING)
        binding.etNoteDesc.setText(EMPTY_STRING)
        requireActivity().supportFragmentManager.popBackStack()
    }


    private fun saveNote() {

        val etNoteTitle = view?.findViewById<EditText>(R.id.etNoteTitle)
        val etNoteDesc = view?.findViewById<EditText>(R.id.etNoteDesc)

        when {
            etNoteTitle?.text.isNullOrEmpty() -> {
                Snackbar.make(requireView(), getString(R.string.title_require), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.snackbarok)) {

                    }.show()
            }
            etNoteDesc?.text.isNullOrEmpty() -> {
                Snackbar.make(
                    requireView(),
                    getString(R.string.empty_note_description_warning),
                    Snackbar.LENGTH_LONG
                ).setAction(getString(R.string.snackbarok)) {

                }.show()
            }
            else -> {
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    NoteEntity().apply {
                        title = etNoteTitle?.text.toString()
                        content = etNoteDesc?.text.toString()
                        dateTime = currentTime
                    }.also {
                        viewModel.saveNote(it)
                    }
                    etNoteTitle?.setText(EMPTY_STRING)
                    etNoteDesc?.setText(EMPTY_STRING)
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun deleteNote() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        viewModel.deleteNote()
        requireActivity().supportFragmentManager.popBackStack()
    }
}
