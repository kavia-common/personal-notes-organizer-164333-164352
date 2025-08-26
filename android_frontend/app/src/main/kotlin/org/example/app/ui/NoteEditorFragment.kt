package org.example.app.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.EditText
import com.google.android.material.button.MaterialButton
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.example.app.R

/**
 * Fragment for creating or editing a note.
 */
class NoteEditorFragment : Fragment() {

    interface Host {
        fun closeEditor()
    }

    private var host: Host? = null

    private val viewModel: EditorViewModel by viewModels()

    private val noteId: Long?
        get() = arguments?.getLong(ARG_ID)?.takeIf { it != 0L }

    private lateinit var titleInput: EditText
    private lateinit var contentInput: EditText
    private lateinit var tagsInput: EditText
    private lateinit var saveButton: MaterialButton
    private lateinit var deleteButton: MaterialButton

    override fun onAttach(context: Context) {
        super.onAttach(context)
        host = context as? Host
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        noteId?.let { viewModel.load(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titleInput = view.findViewById(R.id.titleInput)
        contentInput = view.findViewById(R.id.contentInput)
        tagsInput = view.findViewById(R.id.tagsInput)
        saveButton = view.findViewById(R.id.saveButton)
        deleteButton = view.findViewById(R.id.deleteButton)

        titleInput.doAfterTextChanged { /* live validation if needed */ }
        contentInput.doAfterTextChanged { }
        tagsInput.doAfterTextChanged { }

        saveButton.setOnClickListener { saveAndClose() }
        deleteButton.setOnClickListener {
            noteId?.let { id ->
                viewModel.delete(id) { host?.closeEditor() }
            } ?: host?.closeEditor()
        }

        viewModel.current.observe(viewLifecycleOwner) { note ->
            if (note != null) {
                titleInput.setText(note.title)
                contentInput.setText(note.content)
                tagsInput.setText(note.tags)
            }
        }
    }

    private fun saveAndClose() {
        val title = titleInput.text?.toString()?.trim().orEmpty()
        val content = contentInput.text?.toString()?.trim().orEmpty()
        val tags = tagsInput.text?.toString()?.split(",")?.joinToString(",") { it.trim() } ?: ""
        viewModel.save(title, content, tags, noteId) {
            host?.closeEditor()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_editor, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> { saveAndClose(); true }
            android.R.id.home -> { host?.closeEditor(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val ARG_ID = "id"

        fun newInstance(id: Long? = null): NoteEditorFragment {
            return NoteEditorFragment().apply {
                arguments = bundleOf(ARG_ID to (id ?: 0L))
            }
        }
    }
}
