package org.example.app.data

/**
 * Model representing a Note with title, content, and comma-separated tags.
 */
data class Note(
    val id: Long = 0L,
    val title: String,
    val content: String,
    val tags: String, // comma-separated, e.g. "work,personal"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
