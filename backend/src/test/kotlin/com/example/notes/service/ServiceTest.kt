package com.example.notes.service

import com.example.notes.dto.CreateNoteRequest
import com.example.notes.model.NoteEntity
import com.example.notes.repository.NoteRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class NoteServiceTest {

    private lateinit var noteRepository: NoteRepository
    private lateinit var noteService: NoteService

    @BeforeEach
    fun setUp() {
        noteRepository = mockk()
        noteService = NoteService(noteRepository)
    }

    @Test
    fun `createNote should create and store temporary note`() {
        // Given
        val request = CreateNoteRequest("This is a test note content")

        // When
        val result = noteService.createNote(request)

        // Then
        assertNotNull(result.id)
        assertEquals("This is a test note content", result.content)
        assertTrue(result.tags.isNotEmpty())
    }

    @Test
    fun `getAllNotes should return all temporary notes`() {
        // Given
        val request = CreateNoteRequest("Test content")
        noteService.createNote(request)
        noteService.createNote(CreateNoteRequest("Another note"))

        // When
        val result = noteService.getAllNotes()

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `getNoteById should return note when exists`() {
        // Given
        val request = CreateNoteRequest("Test content")
        val createdNote = noteService.createNote(request)

        // When
        val result = noteService.getNoteById(createdNote.id)

        // Then
        assertNotNull(result)
        assertEquals(createdNote.id, result!!.id)
    }

    @Test
    fun `getNoteById should return null when note not found`() {
        // When
        val result = noteService.getNoteById(UUID.randomUUID())

        // Then
        assertNull(result)
    }

    @Test
    fun `deleteNote should return true when note exists`() {
        // Given
        val request = CreateNoteRequest("Test content")
        val createdNote = noteService.createNote(request)

        // When
        val result = noteService.deleteNote(createdNote.id)

        // Then
        assertTrue(result)
        assertNull(noteService.getNoteById(createdNote.id))
    }

    @Test
    fun `submitNoteToDatabase should save note to repository`() {
        // Given
        val request = CreateNoteRequest("Database test content")
        val noteEntity = NoteEntity(content = "Database test content", tags = listOf("Test", "Database"))

        every { noteRepository.save(any()) } returns noteEntity

        // When
        val result = noteService.submitNoteToDatabase(request)

        // Then
        verify { noteRepository.save(any()) }
        assertEquals("Database test content", result.content)
    }

    @Test
    fun `getAllNotesFromDatabase should return all notes from repository`() {
        // Given
        val noteEntities = listOf(
            NoteEntity(content = "Note 1", tags = listOf("Tag1")),
            NoteEntity(content = "Note 2", tags = listOf("Tag2"))
        )

        every { noteRepository.findAllByOrderByCreatedAtDesc() } returns noteEntities

        // When
        val result = noteService.getAllNotesFromDatabase()

        // Then
        assertEquals(2, result.size)
        verify { noteRepository.findAllByOrderByCreatedAtDesc() }
    }

    @Test
    fun `deleteNoteFromDatabase should return true when note exists`() {
        // Given
        val noteId = UUID.randomUUID()
        every { noteRepository.existsById(noteId) } returns true
        every { noteRepository.deleteById(noteId) } returns Unit

        // When
        val result = noteService.deleteNoteFromDatabase(noteId)

        // Then
        assertTrue(result)
        verify { noteRepository.deleteById(noteId) }
    }

    @Test
    fun `generateTags should create tags from content`() {
        // This tests the private method indirectly through public methods
        val request = CreateNoteRequest("This is a programming Kotlin Spring Boot test")

        val result = noteService.createNote(request)

        assertTrue(result.tags.size <= 3)
        assertTrue(result.tags.all { it[0].isUpperCase() })
    }
}