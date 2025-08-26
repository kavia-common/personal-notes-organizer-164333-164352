package org.example.app.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLiteOpenHelper for notes database.
 */
class NotesDbHelper(context: Context) : SQLiteOpenHelper(context, "notes.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE notes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT NOT NULL," +
                    "content TEXT NOT NULL," +
                    "tags TEXT NOT NULL," +
                    "createdAt INTEGER NOT NULL," +
                    "updatedAt INTEGER NOT NULL" +
                    ")"
        )
        db.execSQL("CREATE INDEX idx_notes_title ON notes(title)")
        db.execSQL("CREATE INDEX idx_notes_tags ON notes(tags)")
        db.execSQL("CREATE INDEX idx_notes_updatedAt ON notes(updatedAt)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS notes")
        onCreate(db)
    }

    fun insert(note: Note): Long {
        val cv = toContentValues(note)
        cv.remove("id")
        return writableDatabase.insert("notes", null, cv)
    }

    fun update(note: Note) {
        val cv = toContentValues(note)
        writableDatabase.update("notes", cv, "id=?", arrayOf(note.id.toString()))
    }

    fun deleteById(id: Long) {
        writableDatabase.delete("notes", "id=?", arrayOf(id.toString()))
    }

    fun getById(id: Long): Note? {
        val c = readableDatabase.query("notes", null, "id=?", arrayOf(id.toString()), null, null, null)
        c.use {
            if (it.moveToFirst()) return fromCursor(it)
        }
        return null
    }

    fun queryAll(): List<Note> {
        val list = mutableListOf<Note>()
        val c = readableDatabase.query("notes", null, null, null, null, null, "updatedAt DESC")
        c.use {
            while (it.moveToNext()) list.add(fromCursor(it))
        }
        return list
    }

    fun search(query: String, tag: String?): List<Note> {
        val args = mutableListOf<String>()
        val sb = StringBuilder()
        sb.append("(title LIKE ? OR content LIKE ?)")
        args.add("%$query%")
        args.add("%$query%")
        if (!tag.isNullOrEmpty()) {
            sb.append(" AND tags LIKE ?")
            args.add("%$tag%")
        }
        val list = mutableListOf<Note>()
        val c = readableDatabase.query("notes", null, sb.toString(), args.toTypedArray(), null, null, "updatedAt DESC")
        c.use {
            while (it.moveToNext()) list.add(fromCursor(it))
        }
        return list
    }

    private fun toContentValues(n: Note): ContentValues {
        return ContentValues().apply {
            put("id", n.id)
            put("title", n.title)
            put("content", n.content)
            put("tags", n.tags)
            put("createdAt", n.createdAt)
            put("updatedAt", n.updatedAt)
        }
    }

    private fun fromCursor(c: android.database.Cursor): Note {
        return Note(
            id = c.getLong(c.getColumnIndexOrThrow("id")),
            title = c.getString(c.getColumnIndexOrThrow("title")),
            content = c.getString(c.getColumnIndexOrThrow("content")),
            tags = c.getString(c.getColumnIndexOrThrow("tags")),
            createdAt = c.getLong(c.getColumnIndexOrThrow("createdAt")),
            updatedAt = c.getLong(c.getColumnIndexOrThrow("updatedAt")),
        )
    }
}
