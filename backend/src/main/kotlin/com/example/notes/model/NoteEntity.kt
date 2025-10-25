package com.example.notes.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "notes")
data class NoteEntity(
    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, length = 1000)
    val content: String,

    @ElementCollection
    @CollectionTable(name = "note_tags", joinColumns = [JoinColumn(name = "note_id")])
    @Column(name = "tag")
    val tags: List<String> = emptyList(),

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)