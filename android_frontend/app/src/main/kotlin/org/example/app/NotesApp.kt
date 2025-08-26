package org.example.app

import android.app.Application


/**
 * Application class to provide a singleton NotesDatabase instance.
 */
class NotesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // No-op: SQLiteOpenHelper will initialize lazily on first use
    }
}
