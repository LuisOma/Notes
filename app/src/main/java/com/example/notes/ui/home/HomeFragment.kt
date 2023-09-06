package com.example.notes.ui.home

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.notes.R
import com.example.notes.databinding.FragmentHomeBinding
import com.example.notes.ui.create_notes.CreateNoteFragment
import com.example.notes.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private lateinit var notesAdapter: NotesAdapter

    private val viewModel by viewModels<HomeViewModel>()

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startRecyclerview()
        getNotes()

        binding.fabCreateNoteBtn.setOnClickListener {
            replaceFragment(CreateNoteFragment.newInstance())
        }

        binding.searchView.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    viewModel.onSearchQueryChanged(p0.toString())
                    return true
                }
            })
    }

    private val onClicked = object : NotesAdapter.OnItemClickListener {
        override fun onClicked(notesId: Int) {

            val fragment: Fragment
            val bundle = Bundle()
            bundle.putInt(getString(R.string.noteID), notesId)
            fragment = CreateNoteFragment.newInstance()
            fragment.arguments = bundle

            replaceFragment(fragment)
        }
    }

    private fun getNotes() = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.notes.collectLatest() {
            if(it.isNotEmpty())
                notesAdapter.submitList(it)
        }
    }

    private fun startRecyclerview() = binding.apply {
        notesAdapter = NotesAdapter().apply { setOnClickListener(onClicked) }
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = notesAdapter
    }

    fun replaceFragment(fragment: Fragment) {

        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.flFragment, fragment)
            .addToBackStack(fragment.javaClass.simpleName)
        fragmentTransition.commit()
    }
}
