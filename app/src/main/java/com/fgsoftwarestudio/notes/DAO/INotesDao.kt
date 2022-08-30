package com.fgsoftwarestudio.notes.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fgsoftwarestudio.notes.Model.Note

@Dao
interface INotesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note : Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("Select * from notesTable order by id ASC")
    fun getAllNotes(): LiveData<List<Note>>

    @Update
    suspend fun update(note: Note)
}