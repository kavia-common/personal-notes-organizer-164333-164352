package org.example.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.app.data.Note
import org.example.app.data.NotesRepository

/**
 * ViewModel for creating and editing a single note.
 */
class EditorViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = NotesRepository.getInstance(app)

    val current = MutableLiveData<Note?>()

    fun load(id: Long) {
        viewModelScope.launch {
            current.postValue(repo.getById(id))
        }
    }

    fun save(title: String, content: String, tags: String, existingId: Long? = null, onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val note = if (existingId != null && existingId != 0L) {
                Note(
                    id = existingId,
                    title = title,
                    content = content,
                    tags = tags,
                    createdAt = current.value?.createdAt ?: now,
                    updatedAt = now
                )
            } else {
                Note(
                    title = title,
                    content = content,
                    tags = tags,
                    createdAt = now,
                    updatedAt = now
                )
            }
            val id = repo.upsert(note)
            onSaved(id)
        }
    }

    fun delete(id: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repo.deleteById(id)
            onDeleted()
        }
    }
}
