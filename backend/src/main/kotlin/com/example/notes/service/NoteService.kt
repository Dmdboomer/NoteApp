package com.example.notes.service

import com.example.notes.dto.CreateNoteRequest
import com.example.notes.model.Note
import com.example.notes.model.NoteEntity
import com.example.notes.repository.NoteRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class NoteService(private val noteRepository: NoteRepository) {

    // In-memory storage for temporary notes (current behavior)
    private val temporaryNotes = mutableMapOf<UUID, Note>()

    // Submit note to database (persistent storage)
    fun submitNoteToDatabase(request: CreateNoteRequest): Note {
        val tags = generateTags(request.content)
        val noteEntity = NoteEntity(
            content = request.content,
            tags = tags
        )
        val savedEntity = noteRepository.save(noteEntity)
        return Note.fromEntity(savedEntity)
    }

    // Get all notes from database
    fun getAllNotesFromDatabase(): List<Note> {
        return noteRepository.findAllByOrderByCreatedAtDesc()
            .map { Note.fromEntity(it) }
    }

    // Delete note from database
    fun deleteNoteFromDatabase(id: UUID): Boolean {
        return if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    // Original methods for temporary in-memory storage
    fun createNote(request: CreateNoteRequest): Note {
        val tags = generateTags(request.content)
        val note = Note(
            content = request.content,
            tags = tags
        )
        temporaryNotes[note.id] = note
        return note
    }

    fun getAllNotes(): List<Note> = temporaryNotes.values.toList()

    fun getNoteById(id: UUID): Note? = temporaryNotes[id]

    fun deleteNote(id: UUID): Boolean = temporaryNotes.remove(id) != null

    private fun generateTags(content: String): List<String> {
        val words = content.split("\\s+".toRegex())
            .map { it.trim().replace(Regex("[^A-Za-z]"), "").lowercase() }
            .filter { it.length > 3 }

        val commonWords = setOf("this", "that", "with", "from", "have", "when", "what")

        val potentialTags = words
            .filter { it !in commonWords }
            .distinct()
            .take(3)

        return if (potentialTags.isEmpty()) {
            listOf("other")
        } else {
            potentialTags.map { it.replaceFirstChar { char -> char.uppercase() } }
        }
    }

    fun searchNotesInDatabase(query: String): List<Note> {
        return if (query.isBlank()) {
            emptyList()
        } else {
            // You can use either search method - both work
            noteRepository.searchByContent(query)
                .map { Note.fromEntity(it) }

            // Alternative:
            // noteRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(query)
            //     .map { Note.fromEntity(it) }
        }
    }
}