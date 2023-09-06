package com.example.notes.ui.create_notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.local.entities.NoteEntity
import com.example.notes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(private val notesRepository: NotesRepository) : ViewModel() {

    val noteId = MutableStateFlow<Int?>(null)

    val note = noteId.flatMapLatest {
        val note = it?.let { notesRepository.getNote(it) }
        flowOf(note)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun setNoteId(id:Int) = viewModelScope.launch {
        noteId.emit(id)
    }

    suspend fun updateNote(noteEntity: NoteEntity) = notesRepository.updateNotes(noteEntity)

    suspend fun saveNote(noteEntity: NoteEntity) = notesRepository.insertNote(noteEntity)

    suspend fun deleteNote() = noteId.value?.let { notesRepository.deleteNoteById(it) }

}