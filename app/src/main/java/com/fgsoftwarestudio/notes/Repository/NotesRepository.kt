package com.fgsoftwarestudio.notes.Repository

import androidx.lifecycle.LiveData
import com.fgsoftwarestudio.notes.DAO.INotesDao
import com.fgsoftwarestudio.notes.Model.Note

class NoteRepository(private val notesDao: INotesDao) {
    val allNotes: LiveData<List<Note>> = notesDao.getAllNotes()

    suspend fun insert(note: Note) {
        notesDao.insert(note)
    }

    suspend fun delete(note: Note){
        notesDao.delete(note)
    }

    suspend fun update(note: Note){
        notesDao.update(note)
    }
}