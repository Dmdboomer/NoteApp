package com.example.notes.controller

import com.example.notes.dto.CreateNoteRequest
import com.example.notes.service.NoteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
class NoteController(private val noteService: NoteService) {

    // Original endpoints for temp notes
    @PostMapping("/temp")
    fun createNote(@RequestBody request: CreateNoteRequest): ResponseEntity<Any> {
        return try {
            val note = noteService.createNote(request)
            ResponseEntity.ok(note)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf("error" to "Failed to create note: ${e.message}")
            )
        }
    }

    @GetMapping("/temp")
    fun getAllNotes(): ResponseEntity<List<Any>> {
        return try {
            val notes = noteService.getAllNotes()
            ResponseEntity.ok(notes)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                listOf(mapOf("error" to "Failed to retrieve notes"))
            )
        }
    }

    @GetMapping("/temp/{id}")
    fun getNoteById(@PathVariable id: String): ResponseEntity<Any> {
        return try {
            val uuid = UUID.fromString(id)
            val note = noteService.getNoteById(uuid)

            note?.let {
                ResponseEntity.ok(it)
            } ?: ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                mapOf("error" to "Note with id $id not found")
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf("error" to "Invalid note ID format")
            )
        }
    }

    @DeleteMapping("/temp/{id}")
    fun deleteNote(@PathVariable id: String): ResponseEntity<Any> {
        return try {
            val uuid = UUID.fromString(id)
            val deleted = noteService.deleteNote(uuid)

            if (deleted) {
                ResponseEntity.ok(mapOf("message" to "Note cancelled successfully"))
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    mapOf("error" to "Note with id $id not found")
                )
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf("error" to "Invalid note ID format")
            )
        }
    }

    // Endpoints for database operations
    @PostMapping("/notes")
    fun submitNoteToDatabase(@RequestBody request: CreateNoteRequest): ResponseEntity<Any> {
        return try {
            val note = noteService.submitNoteToDatabase(request)
            ResponseEntity.ok(note)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf("error" to "Failed to submit note to database: ${e.message}")
            )
        }
    }

    @GetMapping("/notes")
    fun getAllNotesFromDatabase(): ResponseEntity<List<Any>> {
        return try {
            val notes = noteService.getAllNotesFromDatabase()
            ResponseEntity.ok(notes)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                listOf(mapOf("error" to "Failed to retrieve notes from database"))
            )
        }
    }

    @DeleteMapping("/notes/{id}")
    fun deleteNoteFromDatabase(@PathVariable id: String): ResponseEntity<Any> {
        return try {
            val uuid = UUID.fromString(id)
            val deleted = noteService.deleteNoteFromDatabase(uuid)

            if (deleted) {
                ResponseEntity.ok(mapOf("message" to "Note deleted from database successfully"))
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    mapOf("error" to "Note with id $id not found in database")
                )
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf("error" to "Invalid note ID format")
            )
        }
    }

    @GetMapping("/search")
    fun searchNotes(@RequestParam query: String): ResponseEntity<List<Any>> {
        return try {
            if (query.isBlank()) {
                ResponseEntity.badRequest().body(
                    listOf(mapOf("error" to "Query parameter cannot be empty"))
                )
            } else {
                val notes = noteService.searchNotesInDatabase(query)
                ResponseEntity.ok(notes)
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                listOf(mapOf("error" to "Failed to search notes: ${e.message}"))
            )
        }
    }
}

