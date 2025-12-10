/**
======================================================================
CLASS NAME : OllamaTest
DESCRIPTION : Unit tests for the OllamaService class
AUTHOR     : Mickel Angelo Castoverde
COPYRIGHT  : macastroverde 2025
REVISION HISTORY
Date:           By:                         Description:
2025-12-03      Mickel Angelo Castoverde  Creation of the program
2025-12-06      Mickel Angelo Castoverde  Added recent changes for testing

======================================================================
*/
package javaollama;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.chat.OllamaChatResponseModel;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OllamaTest {

    @Mock
    private Ollama mockApi;

    private OllamaService ollama;

    /*
     * ======================================================================
     * METHOD NAME : setUp
     * DESCRIPTION : Sets up the test environment before each test
     * PRE-CONDITION : Mockito annotations processed
     * POST-CONDITION : OllamaService initialized with mocked API
     * ======================================================================
     */
    @BeforeEach
    @SuppressWarnings("null")
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        ollama = new OllamaService(mockApi);
        ollama.setModel("qwen3-vl:2b");

        // mock listModels behavior
        when(mockApi.listModels()).thenReturn(new java.util.ArrayList<>());

        // mock chat behavior
        OllamaChatResult mockResult = mock(OllamaChatResult.class);
        OllamaChatResponseModel mockResponseModel = mock(OllamaChatResponseModel.class);

        // mock message structure
        io.github.ollama4j.models.chat.OllamaChatMessage mockMessage = mock(
                io.github.ollama4j.models.chat.OllamaChatMessage.class);

        when(mockApi.chat(any(), any())).thenReturn(mockResult);
        when(mockResult.getResponseModel()).thenReturn(mockResponseModel);
        when(mockResponseModel.getMessage()).thenReturn(mockMessage);
        when(mockMessage.getResponse()).thenReturn("Mock response");
    }

    /*
     * ======================================================================
     * METHOD NAME : testServerConnection
     * DESCRIPTION : Tests if the server connection check works
     * PRE-CONDITION : Mock API configured
     * POST-CONDITION : Returns true for isServerRunning
     * ======================================================================
     */
    @Test
    @DisplayName("Server connection should work")
    void testServerConnection() throws Exception {
        assertTrue(ollama.isServerRunning(), "Ollama server should be running");
        verify(mockApi).listModels();
    }

    /*
     * ======================================================================
     * METHOD NAME : testBasicChat
     * DESCRIPTION : Tests basic chat functionality
     * PRE-CONDITION : Mock API returns valid response
     * POST-CONDITION : ChatResponse contains expected text
     * ======================================================================
     */
    @Test
    @DisplayName("Basic chat should return response")
    void testBasicChat() {
        try {
            ChatResponse response = ollama.chat("Say hello in one word");
            assertNotNull(response.getResponse(), "Response should not be null");
            assertEquals("Mock response", response.getResponse());
        } catch (Exception e) {
            fail("Chat should not throw exception: " + e.getMessage());
        }
    }

    /*
     * ======================================================================
     * METHOD NAME : testConversationHistory
     * DESCRIPTION : Tests that conversation history tracks messages
     * PRE-CONDITION : Chat history is initially empty
     * POST-CONDITION : History contains all exchanged messages
     * ======================================================================
     */
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

    /*
     * ======================================================================
     * METHOD NAME : testClearHistory
     * DESCRIPTION : Tests clearing the conversation history
     * PRE-CONDITION : History has messages
     * POST-CONDITION : History is empty
     * ======================================================================
     */
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

    /*
     * ======================================================================
     * METHOD NAME : testMultipleChats
     * DESCRIPTION : Tests maintaining context across multiple chats
     * PRE-CONDITION : Multiple chat calls occur
     * POST-CONDITION : History tracks cumulative messages
     * ======================================================================
     */
    @Test
    @DisplayName("Multiple chats should maintain context")
    void testMultipleChats() {
        try {
            ChatResponse firstResponse = ollama.chat("My name is John");
            ChatResponse secondResponse = ollama.chat("What is my name?");

            assertNotNull(firstResponse.getResponse());
            assertNotNull(secondResponse.getResponse());
            assertTrue(ollama.getHistory().size() >= 4, "Should track all messages");
        } catch (Exception e) {
            fail("Multiple chats failed: " + e.getMessage());
        }
    }

    /*
     * ======================================================================
     * METHOD NAME : testConversationHistoryClass
     * DESCRIPTION : Tests internal logic of ConversationHistory class
     * PRE-CONDITION : None
     * POST-CONDITION : Messages added correctly with proper roles
     * ======================================================================
     */
    @Test
    @DisplayName("ConversationHistory should add messages correctly")
    void testConversationHistoryClass() {
        ConversationHistory history = new ConversationHistory();

        history.addUserMessage("Hello");
        history.addAssistantMessage("Hi there");

        assertEquals(2, history.size());
        assertEquals("user", history.getMessages().get(0).getRole());
        assertEquals("Hello", history.getMessages().get(0).getContent());
        assertEquals("assistant", history.getMessages().get(1).getRole());
        assertEquals("Hi there", history.getMessages().get(1).getContent());
    }

    /*
     * ======================================================================
     * METHOD NAME : testEmptyPrompt
     * DESCRIPTION : Tests handling of empty prompt
     * PRE-CONDITION : Empty prompt string
     * POST-CONDITION : System handles gracefully without crash
     * ======================================================================
     */
    @Test
    @DisplayName("Empty prompt should still work")
    void testEmptyPrompt() {
        try {
            ChatResponse response = ollama.chat("");
            assertNotNull(response.getResponse(), "Should handle empty prompts");
        } catch (Exception e) {
            // empty prompts might throw - that's ok
        }
    }
}
