package org.example.app.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for managing notes using SQLiteOpenHelper.
 */
class NotesRepository private constructor(context: Context) {

    private val db = NotesDbHelper(context)
    private val allLive = MutableLiveData<List<Note>>(emptyList())

    init {
        refreshAll()
    }

    fun observeAll(): LiveData<List<Note>> = allLive

    fun observeSearch(query: String, tag: String?): LiveData<List<Note>> {
        val live = MutableLiveData<List<Note>>(emptyList())
        live.value = db.search(query, tag)
        return live
    }

    suspend fun getById(id: Long): Note? = withContext(Dispatchers.IO) {
        db.getById(id)
    }

    suspend fun upsert(note: Note): Long = withContext(Dispatchers.IO) {
        val id = if (note.id == 0L) {
            db.insert(note)
        } else {
            db.update(note)
            note.id
        }
        refreshAll()
        id
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        db.deleteById(id)
        refreshAll()
    }

    private fun refreshAll() {
        allLive.postValue(db.queryAll())
    }

    companion object {
        @Volatile private var INSTANCE: NotesRepository? = null

        fun getInstance(context: Context): NotesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NotesRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
