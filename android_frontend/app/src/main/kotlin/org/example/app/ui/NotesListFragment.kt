package org.example.app.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.example.app.R
import org.example.app.data.Note

/**
 * Fragment that shows list of notes with search and tag chips.
 */
class NotesListFragment : Fragment() {

    interface Host {
        fun openEditor(noteId: Long? = null)
    }

    private var host: Host? = null

    private val viewModel: NotesViewModel by viewModels()

    private lateinit var recycler: RecyclerView
    private lateinit var empty: TextView
    private lateinit var fab: FloatingActionButton
    private lateinit var chipGroup: com.google.android.material.chip.ChipGroup

    private val adapter = NotesAdapter(
        onClick = { host?.openEditor(it.id) },
        onDelete = { /* handle deletion in future */ }
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        host = context as? Host
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_notes_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recycler)
        empty = view.findViewById(R.id.empty)
        fab = view.findViewById(R.id.fab)
        chipGroup = view.findViewById(R.id.chipGroup)

        recycler.adapter = adapter
        empty.isVisible = false

        fab.setOnClickListener { host?.openEditor(null) }

        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            adapter.submitList(notes)
            empty.isVisible = notes.isEmpty()
            buildTagChips(notes)
        }
    }

    private fun buildTagChips(notes: List<Note>) {
        val tags = notes.flatMap { it.tags.split(",").map { t -> t.trim() } }
            .filter { it.isNotEmpty() }
            .toSet()
        chipGroup.removeAllViews()
        if (tags.isEmpty()) {
            chipGroup.isVisible = false
            return
        }
        chipGroup.isVisible = true
        val all = Chip(requireContext()).apply {
            text = getString(R.string.all)
            isCheckable = true
            isChecked = true
            setOnClickListener { viewModel.setTag(null) }
        }
        chipGroup.addView(all)
        tags.forEach { tag ->
            val chip = Chip(requireContext()).apply {
                text = tag
                isCheckable = true
                setOnClickListener { viewModel.setTag(tag) }
            }
            chipGroup.addView(chip)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setQuery(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setQuery(newText ?: "")
                return true
            }
        })
    }

    class NotesAdapter(
        private val onClick: (Note) -> Unit,
        private val onDelete: (Note) -> Unit
    ) : ListAdapter<Note, NotesAdapter.VH>(DIFF) {

        companion object {
            private val DIFF = object : DiffUtil.ItemCallback<Note>() {
                override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id
                override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
            }
        }

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            private val title: TextView = itemView.findViewById(R.id.title)
            private val content: TextView = itemView.findViewById(R.id.content)
            private val tags: TextView = itemView.findViewById(R.id.tags)
            private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

            init {
                view.setOnClickListener {
                    val item = getItem(bindingAdapterPosition)
                    onClick(item)
                }
                deleteButton.setOnClickListener {
                    val item = getItem(bindingAdapterPosition)
                    onDelete(item)
                }
            }

            fun bind(note: Note) {
                title.text = note.title.ifBlank { itemView.context.getString(R.string.untitled) }
                content.text = note.content
                tags.text = note.tags
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(getItem(position))
        }
    }

    companion object {
        fun newInstance() = NotesListFragment()
    }
}
