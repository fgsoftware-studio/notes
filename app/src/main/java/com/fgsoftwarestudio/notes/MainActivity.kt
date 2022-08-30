package com.fgsoftwarestudio.notes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.fgsoftwarestudio.notes.Model.Note
import com.fgsoftwarestudio.notes.UI.Adapter.NoteClickDeleteInterface
import com.fgsoftwarestudio.notes.UI.Adapter.NoteClickInterface
import com.fgsoftwarestudio.notes.UI.Adapter.RecyclerViewAdapter
import com.fgsoftwarestudio.notes.ViewModel.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

class MainActivity : AppCompatActivity(), NoteClickInterface, NoteClickDeleteInterface {
    private lateinit var reviewManager: ReviewManager

    lateinit var viewModal: NoteViewModel
    lateinit var notesRV: RecyclerView
    lateinit var addFAB: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val noteRVAdapter = RecyclerViewAdapter(this, this, this)

        reviewManager = ReviewManagerFactory.create(this@MainActivity)
        notesRV = findViewById(R.id.notesRV)
        addFAB = findViewById(R.id.idFAB)

        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModel::class.java)

        viewModal.allNotes.observe(this, Observer { list ->
            list?.let {
                noteRVAdapter.updateList(it)
            }
        })
        addFAB.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }
    //TODO: splash screen

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
        viewModal.deleteNote(note)

        Toast.makeText(this, "${note.noteTitle} Deleted", Toast.LENGTH_LONG).show()
    }

    //TODO: in-app update
    //TODO: in-app review
}