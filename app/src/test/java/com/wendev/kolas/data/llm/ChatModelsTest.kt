package com.wendev.kolas.data.llm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatModelsTest {

    @Test
    fun `catalog has unique ids`() {
        val ids = ChatModels.ALL.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `default is part of the catalog`() {
        assertTrue(ChatModels.ALL.contains(ChatModels.DEFAULT))
    }

    @Test
    fun `every spec is downloadable`() {
        ChatModels.ALL.forEach { spec ->
            assertTrue(spec.url.startsWith("https://"))
            assertTrue(spec.fileName.endsWith(".gguf"))
            assertTrue(spec.approxSizeBytes > 0)
            assertTrue(spec.minRamGb > 0)
        }
    }

    @Test
    fun `byId resolves known ids and rejects unknown`() {
        assertEquals(ChatModels.FAST, ChatModels.byId("gemma-3-1b"))
        assertEquals(null, ChatModels.byId("nope"))
        assertEquals(null, ChatModels.byId(null))
    }
}
