package org.example.app.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.android.material.appbar.MaterialToolbar
import org.example.app.R

/**
 * MainActivity hosting notes list and editor fragments.
 * - Toolbar with search
 * - RecyclerView list
 * - FAB to add note
 */
class MainActivity : AppCompatActivity(), NotesListFragment.Host, NoteEditorFragment.Host {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Notes_Light) // ensure light theme
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.container, NotesListFragment.newInstance(), "list")
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val editor = supportFragmentManager.findFragmentByTag("editor")
                if (editor != null) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
            }
        })
    }

    override fun openEditor(noteId: Long?) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.container, NoteEditorFragment.newInstance(noteId), "editor")
            addToBackStack("editor")
        }
    }

    override fun closeEditor() {
        supportFragmentManager.popBackStack()
    }
}
