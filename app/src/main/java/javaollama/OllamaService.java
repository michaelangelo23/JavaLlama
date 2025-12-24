/**
======================================================================
CLASS NAME : OllamaService
DESCRIPTION : Service class for interacting with the Ollama language model
AUTHOR     : Mickel Angelo Castoverde
COPYRIGHT  : macastroverde 2025
REVISION HISTORY
Date:           By:                       Description:
2025-12-03      Mickel Angelo Castoverde  Creation of the program
2025-12-04      Mickel Angelo Castoverde  made default model name and added optimization options
2025-12-04      Mickel Angelo Castoverde  added System Prompt
2025-12-04      Mickel Angelo Castoverde  optimized context window for generation speed (8192 -> 4096)
======================================================================
*/
package javaollama;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;

import io.github.ollama4j.models.request.ThinkMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class OllamaService {

    private static final String DEFAULT_MODEL = "phi3.5:latest";
    private static final int TIMEOUT_SECONDS = 120;
    private static final String SYSTEM_PROMPT = "You are a helpful AI assistant. Answer the user's questions directly and concisely.";

    private Ollama api;
    private String modelName;
    private ConversationHistory history;
    private String pdfContext;

    /*
     * ======================================================================
     * METHOD NAME : getDefaultModel
     * DESCRIPTION : Returns the default model name
     * PRE-CONDITION : None
     * POST-CONDITION : Returns the default model name string
     * ======================================================================
     */
    public static String getDefaultModel() {
        return DEFAULT_MODEL;
    }

    /*
     * ======================================================================
     * METHOD NAME : OllamaService
     * DESCRIPTION : Default constructor for the service
     * PRE-CONDITION : Ollama server should be reachable
     * POST-CONDITION : Service initialized with default settings
     * ======================================================================
     */
    public OllamaService() {
        this(new Ollama());
    }

    /*
     * ======================================================================
     * METHOD NAME : OllamaService
     * DESCRIPTION : Constructor for dependency injection (testing)
     * PRE-CONDITION : Valid Ollama api instance
     * POST-CONDITION : Service initialized with injected api
     * ======================================================================
     */
    public OllamaService(Ollama api) {
        this.api = api;
        this.api.setRequestTimeoutSeconds(TIMEOUT_SECONDS);
        this.modelName = DEFAULT_MODEL;
        this.history = new ConversationHistory();
        this.pdfContext = "";
    }

    /*
     * ======================================================================
     * METHOD NAME : isServerRunning
     * DESCRIPTION : Checks if the ollama server is running
     * PRE-CONDITION : None
     * POST-CONDITION : Returns true if server operates, false otherwise
     * ======================================================================
     */
    public boolean isServerRunning() {
        try {
            api.listModels();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * ======================================================================
     * METHOD NAME : getApi
     * DESCRIPTION : Gets the api instance
     * PRE-CONDITION : None
     * POST-CONDITION : Returns the api instance
     * ======================================================================
     */
    public Ollama getApi() {
        return api;
    }

    /*
     * ======================================================================
     * METHOD NAME : setApi
     * DESCRIPTION : Sets the api instance
     * PRE-CONDITION : api is not null
     * POST-CONDITION : api instance is updated
     * ======================================================================
     */
    public void setApi(Ollama api) {
        this.api = api;
    }

    /*
     * ======================================================================
     * METHOD NAME : getModelName
     * DESCRIPTION : Gets the model name
     * PRE-CONDITION : None
     * POST-CONDITION : Returns the model name
     * ======================================================================
     */
    public String getModelName() {
        return modelName;
    }

    /*
     * ======================================================================
     * METHOD NAME : setModelName
     * DESCRIPTION : Sets the model to be used for chat
     * PRE-CONDITION : modelName is a valid string
     * POST-CONDITION : modelName is updated
     * ======================================================================
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /*
     * ======================================================================
     * METHOD NAME : setModel
     * DESCRIPTION : Legacy method for setting model name
     * PRE-CONDITION : modelName is a valid string
     * POST-CONDITION : modelName is updated
     * ======================================================================
     */
    public void setModel(String modelName) {
        setModelName(modelName);
    }

    /*
     * ======================================================================
     * METHOD NAME : getHistory
     * DESCRIPTION : Gets the current conversation history
     * PRE-CONDITION : None
     * POST-CONDITION : Returns the conversation history
     * ======================================================================
     */
    public ConversationHistory getHistory() {
        return history;
    }

    /*
     * ======================================================================
     * METHOD NAME : setHistory
     * DESCRIPTION : Sets the conversation history
     * PRE-CONDITION : history is valid
     * POST-CONDITION : history is updated
     * ======================================================================
     */
    public void setHistory(ConversationHistory history) {
        this.history = history;
    }

    /*
     * ======================================================================
     * METHOD NAME : getPdfContext
     * DESCRIPTION : Gets the pdf context
     * PRE-CONDITION : None
     * POST-CONDITION : Returns the pdf context string
     * ======================================================================
     */
    public String getPdfContext() {
        return pdfContext;
    }

    /*
     * ======================================================================
     * METHOD NAME : setPdfContext
     * DESCRIPTION : Sets the context extracted from a pdf file
     * PRE-CONDITION : pdfContext is a valid string
     * POST-CONDITION : pdfContext is updated
     * ======================================================================
     */
    public void setPdfContext(String pdfContext) {
        this.pdfContext = pdfContext;
    }

    /*
     * ======================================================================
     * METHOD NAME : setContext
     * DESCRIPTION : Legacy method for setting pdf context
     * PRE-CONDITION : context is a valid string
     * POST-CONDITION : pdfContext is updated
     * ======================================================================
     */
    public void setContext(String context) {
        setPdfContext(context);
    }

    /*
     * ======================================================================
     * METHOD NAME : clearHistory
     * DESCRIPTION : Clears the conversation history and the pdf context
     * PRE-CONDITION : None
     * POST-CONDITION : History is empty and context is null
     * ======================================================================
     */
    public void clearHistory() {
        history.clear();
        this.pdfContext = null;
    }

    /*
     * ======================================================================
     * METHOD NAME : chat
     * DESCRIPTION : Sends a prompt to the ollama model and retrieves the response
     * PRE-CONDITION : Server is running, prompt is valid
     * POST-CONDITION : Returns the chat response from the model
     * ======================================================================
     */
    public ChatResponse chat(String prompt) throws OllamaServiceException {
        try {
            String finalPrompt = prompt;
            if (pdfContext != null && !pdfContext.isEmpty()) {
                // context stuffing: prepend pdf content to the prompt
                finalPrompt = "Context:\n" + pdfContext + "\n\nUser Question:\n" + prompt;
            }

            // add the user's message to history
            history.addUserMessage(finalPrompt);

            List<OllamaChatMessage> messages = new ArrayList<>();

            // add System Prompt
            messages.add(new OllamaChatMessage(OllamaChatMessageRole.SYSTEM, SYSTEM_PROMPT));

            for (ConversationHistory.Message msg : history.getMessages()) {
                OllamaChatMessageRole role = msg.getRole().equalsIgnoreCase("user") ? OllamaChatMessageRole.USER
                        : OllamaChatMessageRole.ASSISTANT;
                messages.add(new OllamaChatMessage(role, msg.getContent()));
            }

            OllamaChatRequest request = new OllamaChatRequest(modelName, ThinkMode.DISABLED, messages);

            // performance options
            Map<String, Object> options = new HashMap<>();
            options.put("num_ctx", 16384);
            options.put("num_batch", 2048);
            options.put("temperature", 0.3);
            options.put("top_k", 40);
            options.put("top_p", 0.9);
            options.put("repeat_penalty", 1.1);
            options.put("num_predict", 512);
            options.put("num_keep", 16384);

            List<String> stopTokens = new ArrayList<>();
            stopTokens.add("User:");
            stopTokens.add("System:");
            stopTokens.add("Assistant:");
            stopTokens.add("-----");
            options.put("stop", stopTokens);

            request.setOptions(options);

            // execute the chat request
            OllamaChatResult result = api.chat(request, response -> {
            });

            String responseText = result.getResponseModel().getMessage().getResponse();
            String finalResponse = responseText != null ? responseText : "";

            // add the assistant's response to history
            history.addAssistantMessage(finalResponse);

            return new ChatResponse(finalResponse);

        } catch (Exception e) {
            throw new OllamaServiceException("Failed to communicate with Ollama model: " + modelName, e);
        }
    }

    /*
     * ======================================================================
     * METHOD NAME : writeOutput
     * DESCRIPTION : Writes the object state to the console
     * PRE-CONDITION : None
     * POST-CONDITION : Object state is printed to stdout
     * ======================================================================
     */
    public void writeOutput() {
        System.out.println("OllamaService Status:");
        System.out.println("Model Name: " + modelName);
        System.out.println("Timeout: " + TIMEOUT_SECONDS + " seconds");
        System.out.println("Context Loaded: " + (pdfContext != null && !pdfContext.isEmpty()));
        System.out.println("History Size: " + (history != null ? history.size() : 0));
    }
}
