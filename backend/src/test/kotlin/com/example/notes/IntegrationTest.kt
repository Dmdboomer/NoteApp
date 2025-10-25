package com.example.notes

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class NotesApplicationIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `context loads`() {
        // Basic test to verify Spring context loads
    }

    @Test
    fun `create note via REST API`() {
        val noteJson = """
            {
                "content": "This is a test note for integration testing"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(noteJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").value("This is a test note for integration testing"))
            .andExpect(jsonPath("$.tags").exists())
    }
}