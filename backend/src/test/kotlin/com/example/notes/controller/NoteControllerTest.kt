package com.example.notes.controller

import com.example.notes.dto.CreateNoteRequest
import com.example.notes.model.Note
import com.example.notes.service.NoteService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*

class NoteControllerTest {

    private lateinit var noteService: NoteService
    private lateinit var noteController: NoteController

    @BeforeEach
    fun setUp() {
        noteService = mockk()
        noteController = NoteController(noteService)
    }

    @Test
    fun `createNote should return OK when successful`() {
        // Given
        val request = CreateNoteRequest("Test content")
        val expectedNote = Note(content = "Test content", tags = listOf("Test"))

        every { noteService.createNote(request) } returns expectedNote

        // When
        val response = noteController.createNote(request)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedNote, response.body)
    }

    @Test
    fun `createNote should return BAD_REQUEST when exception occurs`() {
        // Given
        val request = CreateNoteRequest("Test content")

        every { noteService.createNote(request) } throws RuntimeException("Service error")

        // When
        val response = noteController.createNote(request)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertTrue(response.body.toString().contains("Failed to create note"))
    }

    @Test
    fun `getNoteById should return note when found`() {
        // Given
        val noteId = UUID.randomUUID().toString()
        val expectedNote = Note(id = UUID.fromString(noteId), content = "Test", tags = listOf("Test"))

        every { noteService.getNoteById(any()) } returns expectedNote

        // When
        val response = noteController.getNoteById(noteId)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedNote, response.body)
    }

    @Test
    fun `getNoteById should return NOT_FOUND when note not found`() {
        // Given
        val noteId = UUID.randomUUID().toString()

        every { noteService.getNoteById(any()) } returns null

        // When
        val response = noteController.getNoteById(noteId)

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertTrue(response.body.toString().contains("not found"))
    }

    @Test
    fun `getNoteById should return BAD_REQUEST for invalid UUID`() {
        // Given
        val invalidId = "invalid-uuid"

        // When
        val response = noteController.getNoteById(invalidId)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertTrue(response.body.toString().contains("Invalid note ID format"))
    }

    @Test
    fun `deleteNote should return OK when note deleted`() {
        // Given
        val noteId = UUID.randomUUID().toString()

        every { noteService.deleteNote(any()) } returns true

        // When
        val response = noteController.deleteNote(noteId)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.body.toString().contains("cancelled successfully"))
    }

    @Test
    fun `submitNoteToDatabase should return saved note`() {
        // Given
        val request = CreateNoteRequest("Database content")
        val expectedNote = Note(content = "Database content", tags = listOf("Database"))

        every { noteService.submitNoteToDatabase(request) } returns expectedNote

        // When
        val response = noteController.submitNoteToDatabase(request)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedNote, response.body)
    }

    @Test
    fun `getAllNotesFromDatabase should return notes list`() {
        // Given
        val notes = listOf(
            Note(content = "Note 1", tags = listOf("Tag1")),
            Note(content = "Note 2", tags = listOf("Tag2"))
        )

        every { noteService.getAllNotesFromDatabase() } returns notes

        // When
        val response = noteController.getAllNotesFromDatabase()

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notes, response.body)
    }
}