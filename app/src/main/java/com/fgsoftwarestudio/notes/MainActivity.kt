package com.fgsoftwarestudio.notes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fgsoftwarestudio.notes.Model.Note
import com.fgsoftwarestudio.notes.UI.Adapter.NoteClickDeleteInterface
import com.fgsoftwarestudio.notes.UI.Adapter.NoteClickInterface
import com.fgsoftwarestudio.notes.UI.Adapter.RecyclerViewAdapter
import com.fgsoftwarestudio.notes.ViewModel.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), NoteClickInterface, NoteClickDeleteInterface {
    private lateinit var noteModel: NoteViewModel
    private lateinit var notesRV: RecyclerView
    private lateinit var noteRVAdapter: RecyclerViewAdapter
    private lateinit var btnAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    override fun onNoteClick(note: Note) {
        val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)

        intent.putExtra("noteType", "Edit")
        intent.putExtra("noteTitle", note.noteTitle)
        intent.putExtra("noteDescription", note.noteDescription)
        intent.putExtra("noteId", note.id)
        startActivity(intent)
        this.finish()
    }

    override fun onDeleteIconClick(note: Note) {
        noteModel.deleteNote(note)

        Toast.makeText(this, "${note.noteTitle} Deleted", Toast.LENGTH_LONG).show()
    }

    private fun init() {
        setContentView(R.layout.activity_main)

        notesRV = findViewById(R.id.notesRV)
        noteRVAdapter = RecyclerViewAdapter(this, this, this)
        btnAdd = findViewById(R.id.idFAB)

        notesRV.layoutManager = LinearLayoutManager(this)
        notesRV.adapter = noteRVAdapter

        noteModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModel::class.java)

        noteModel.allNotes.observe(this, Observer { list ->
            list?.let {
                noteRVAdapter.updateList(it)
            }
        })

        btnAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    //TODO: in-app update
    //TODO: in-app review
}
