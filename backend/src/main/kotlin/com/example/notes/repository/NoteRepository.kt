package com.example.notes.repository

import com.example.notes.model.NoteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@Repository
interface NoteRepository : JpaRepository<NoteEntity, UUID> {
    fun findAllByOrderByCreatedAtDesc(): List<NoteEntity>

    @Query("SELECT n FROM NoteEntity n WHERE LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY n.createdAt DESC")
    fun searchByContent(@Param("query") query: String): List<NoteEntity>
}