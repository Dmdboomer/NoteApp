package com.example.notes.model

import java.time.LocalDateTime
import java.util.*

data class Note(
    val id: UUID = UUID.randomUUID(),
    val content: String,
    val tags: List<String> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun fromEntity(entity: NoteEntity): Note {
            return Note(
                id = entity.id,
                content = entity.content,
                tags = entity.tags,
                createdAt = entity.createdAt
            )
        }
    }

    fun toEntity(): NoteEntity {
        return NoteEntity(
            id = id,
            content = content,
            tags = tags,
            createdAt = createdAt
        )
    }
}

