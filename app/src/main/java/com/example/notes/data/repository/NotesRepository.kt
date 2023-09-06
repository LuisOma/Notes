package com.example.notes.data.repository

import com.example.notes.data.local.dao.NoteDao
import com.example.notes.data.local.entities.NoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotesRepository @Inject constructor(private val notesDao: NoteDao) {

    val notes = notesDao.getAllNotes()

    suspend fun getNote(id: Int) = withContext(Dispatchers.IO) {
        notesDao.getSpecificNote(id)
    }

    suspend fun insertNote(note: NoteEntity) = withContext(Dispatchers.IO) {
        notesDao.insertNotes(note)
    }

    suspend fun deleteNoteById(id: Int) = withContext(Dispatchers.IO) {
        notesDao.deleteSpecificNote(id)
    }

    suspend fun updateNotes(note: NoteEntity) = withContext(Dispatchers.IO) {
        notesDao.updateNotes(note)
    }
}