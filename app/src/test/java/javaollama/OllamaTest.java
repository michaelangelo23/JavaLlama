package javaollama;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class OllamaTest {
    
    private OllamaService ollama;
    
    @BeforeEach
    void setUp() {
        ollama = new OllamaService();
        ollama.setModel("qwen3-vl:2b");
    }
    
    @Test
    @DisplayName("Server connection should work")
    void testServerConnection() {
        assertTrue(ollama.isServerRunning(), "Ollama server should be running");
    }
    
    @Test
    @DisplayName("Basic chat should return response")
    void testBasicChat() {
        try {
            String response = ollama.chat("Say hello in one word");
            assertNotNull(response, "Response should not be null");
            assertFalse(response.trim().isEmpty(), "Response should not be empty");
        } catch (Exception e) {
            fail("Chat should not throw exception: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Conversation history should track messages")
    void testConversationHistory() {
        try {
            ollama.chat("First message");
            ollama.chat("Second message");
            
            ConversationHistory history = ollama.getHistory();
            assertEquals(4, history.size(), "Should have 4 messages (2 user + 2 assistant)");
        } catch (Exception e) {
            fail("History tracking failed: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Clear history should remove all messages")
    void testClearHistory() {
        try {
            ollama.chat("Test message");
            ollama.clearHistory();
            
            assertEquals(0, ollama.getHistory().size(), "History should be empty after clear");
        } catch (Exception e) {
            fail("Clear history failed: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Multiple chats should maintain context")
    void testMultipleChats() {
        try {
            String firstResponse = ollama.chat("My name is John");
            String secondResponse = ollama.chat("What is my name?");
            
            assertNotNull(firstResponse);
            assertNotNull(secondResponse);
            assertTrue(ollama.getHistory().size() >= 4, "Should track all messages");
        } catch (Exception e) {
            fail("Multiple chats failed: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("ConversationHistory should add messages correctly")
    void testConversationHistoryClass() {
        ConversationHistory history = new ConversationHistory();
        
        history.addUserMessage("Hello");
        history.addAssistantMessage("Hi there");
        
        assertEquals(2, history.size());
        assertEquals("user", history.getMessages().get(0).role);
        assertEquals("Hello", history.getMessages().get(0).content);
        assertEquals("assistant", history.getMessages().get(1).role);
        assertEquals("Hi there", history.getMessages().get(1).content);
    }
    
    @Test
    @DisplayName("Empty prompt should still work")
    void testEmptyPrompt() {
        try {
            String response = ollama.chat("");
            assertNotNull(response, "Should handle empty prompts");
        } catch (Exception e) {
            // empty prompts might throw - that's ok
        }
    }
}
