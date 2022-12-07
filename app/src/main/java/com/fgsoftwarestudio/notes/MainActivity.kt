package com.fgsoftwarestudio.notes

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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
import com.google.android.vending.licensing.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), NoteClickInterface, NoteClickDeleteInterface {
    companion object {
        private const val BASE64_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkkmPYDP6G5fDQipuNP2bTU9tK5W7FMi5mqfTA2oOdkSbttKn6E4LEUlqGSkQXS2xHCFe6em3n/91TYNI50UjQB0TnbZVua806GxqQF6mI34HBtd4Jwo2Q6RjZw0wlze3RoGu8XPLURAwCzuQP8iD7LzbO71zgh5WGIL0VOJivRr6TNLmj7GfkI0CkNl68dWUOg/EjJPWOZB77kqaRp7XaDVsG+AybU6bfEDvo+bSLt9ZGmYfmdk6i6zmVTCo6xLA/b52bezSq0Bk7A9pKgB2wiNDrJEiRFxMv0NguscxK2Iyeqg2kI5PrKU7kXrpJj8Z5OBeuyT+my+wZgl9FjjZQQIDAQAB"
        private const val MY_REQUEST_CODE = 500
        private val SALT = byteArrayOf(
            1, 92, -46, 44, 83,
            -64, 63, -66, 21, -11,
            -78, -41, 22, -52, 2,
            -45, -14, -19, 80, -36
        )
    }

    private val deviceId: String by lazy {
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }
    private lateinit var licenseCheckerCallback: LicenseCheckerCallback
    private lateinit var checker: LicenseChecker
    private lateinit var noteModel: NoteViewModel
    private lateinit var notesRV: RecyclerView
    private lateinit var noteRVAdapter: RecyclerViewAdapter
    private lateinit var btnAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        licenseCheckerCallback = MyLicenseCheckerCallback()

        checker = LicenseChecker(
            this,
            ServerManagedPolicy(this, AESObfuscator(SALT, packageName, deviceId)),
            BASE64_PUBLIC_KEY
        )

        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        checker.onDestroy()
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
        doCheck()

        notesRV = findViewById(R.id.notesRV)
        noteRVAdapter = RecyclerViewAdapter(this, this, this)
        btnAdd = findViewById(R.id.idFAB)

        notesRV.layoutManager = LinearLayoutManager(this)
        notesRV.adapter = noteRVAdapter

        noteModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[NoteViewModel::class.java]

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

    private fun doCheck() {
        checker.checkAccess(licenseCheckerCallback)
    }

    private inner class MyLicenseCheckerCallback : LicenseCheckerCallback {
        override fun allow(reason: Int) {
            if (isFinishing) {
                return
            }
        }

        override fun applicationError(errorCode: Int) {
            dontAllow(Policy.NOT_LICENSED)
        }

        override fun dontAllow(reason: Int) {
            if (isFinishing) {
                return
            }

            displayResult("Not Licensed")
            abort()
        }
    }

    private fun abort() {
        finishAffinity()
        exitProcess(0)
    }

    private fun displayResult(result: String) {
        Toast.makeText(this, result, Toast.LENGTH_LONG).show()
    }

    //TODO: in-app review
}
