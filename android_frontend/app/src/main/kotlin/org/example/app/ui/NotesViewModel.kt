package org.example.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import org.example.app.data.Note
import org.example.app.data.NotesRepository

/**
 * ViewModel to provide notes list with search and tag filtering without Transformations.
 */
class NotesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = NotesRepository.getInstance(app)

    private val queryLive = MutableLiveData("")
    private val tagLive = MutableLiveData<String?>(null)

    private val _notes = MediatorLiveData<List<Note>>().apply {
        addSource(repo.observeAll()) { list ->
            value = applyFilters(list, queryLive.value ?: "", tagLive.value)
        }
        addSource(queryLive) { q ->
            value = applyFilters(repo.observeAll().value ?: emptyList(), q, tagLive.value)
        }
        addSource(tagLive) { t ->
            value = applyFilters(repo.observeAll().value ?: emptyList(), queryLive.value ?: "", t)
        }
    }
    val notes: LiveData<List<Note>> = _notes

    private fun applyFilters(list: List<Note>, q: String, tag: String?): List<Note> {
        val qLower = q.lowercase()
        return list.filter { note ->
            val matchesQ = qLower.isBlank() || note.title.lowercase().contains(qLower) || note.content.lowercase().contains(qLower)
            val matchesTag = tag.isNullOrBlank() || note.tags.split(",").map { it.trim() }.contains(tag)
            matchesQ && matchesTag
        }
    }

    fun setQuery(q: String) {
        queryLive.value = q
    }

    fun setTag(tag: String?) {
        tagLive.value = tag
    }
}
