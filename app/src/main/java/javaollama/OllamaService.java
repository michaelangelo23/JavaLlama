package javaollama;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.models.generate.OllamaGenerateRequest;
import io.github.ollama4j.models.request.ThinkMode;
import io.github.ollama4j.utils.OptionsBuilder;

import java.io.IOException;

// ollama API wrapper
public class OllamaService {
    
    private final Ollama api;
    private String modelName;
    private ConversationHistory history;
    
    public OllamaService() {
        this.api = new Ollama();
        this.api.setRequestTimeoutSeconds(120);
        this.history = new ConversationHistory();
    }
    
    public boolean isServerRunning() {
        try {
            api.listModels();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void setModel(String modelName) {
        this.modelName = modelName;
    }
    
    // send prompt and get response
    public String chat(String prompt) throws OllamaException, IOException, InterruptedException {
        history.addUserMessage(prompt);
        
        OllamaResult result = api.generate(
            OllamaGenerateRequest.builder()
                .withModel(modelName)
                .withPrompt(prompt)
                .withRaw(false)
                .withThink(ThinkMode.DISABLED)
                .withOptions(new OptionsBuilder().setTemperature(0.7f).build())
                .build(),
            null);
        
        String response = result.getResponse();
        
        if (isEmpty(response) && result.getThinking() != null && !result.getThinking().isEmpty()) {
            response = parseThinkingResponse(result.getThinking());
        } else if (!isEmpty(response)) {
            response = cleanResponse(response);
        }
        
        String finalResponse = response != null ? response : "";
        history.addAssistantMessage(finalResponse);
        
        return finalResponse;
    }
    
    // clean up JSON responses
    private String cleanResponse(String response) {
        String trimmed = response.trim();
        
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            String extracted = tryParseJsonResponse(trimmed);
            if (!extracted.equals(trimmed)) {
                return extracted;
            }
            
            // try extracting from weird {"text": value} format
            try {
                int firstQuote = trimmed.indexOf("\"");
                int secondQuote = trimmed.indexOf("\"", firstQuote + 1);
                if (firstQuote != -1 && secondQuote != -1) {
                    String possibleResponse = trimmed.substring(firstQuote + 1, secondQuote);
                    if (possibleResponse.length() > 10 && !possibleResponse.contains("_")) {
                        return possibleResponse;
                    }
                }
            } catch (Exception e) {
            }
        }
        
        return response;
    }
    
    private String tryParseJsonResponse(String response) {
        String trimmed = response.trim();
        if (!trimmed.startsWith("{")) {
            return response;
        }
        
        String[] fields = {"response", "content", "text", "message", "answer"};
        for (String field : fields) {
            String extracted = extractJsonField(trimmed, field);
            if (!isEmpty(extracted) && !extracted.equals(trimmed)) {
                return extracted;
            }
        }
        
        return response;
    }
    
    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }
    
    private String parseThinkingResponse(String thinking) {
        String[] fields = {"text", "output", "answer"};
        for (String field : fields) {
            String extracted = extractJsonField(thinking, field);
            if (!isEmpty(extracted)) {
                return extracted;
            }
        }
        return thinking;
    }
    
    private String extractJsonField(String json, String fieldName) {
        try {
            int fieldStart = json.indexOf("\"" + fieldName + "\"");
            if (fieldStart == -1) return null;
            
            int colonIndex = json.indexOf(":", fieldStart);
            int valueStart = json.indexOf("\"", colonIndex);
            if (valueStart == -1) return null;
            
            valueStart++;
            int valueEnd = findClosingQuote(json, valueStart);
            
            return valueEnd > valueStart ? json.substring(valueStart, valueEnd) : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private int findClosingQuote(String json, int start) {
        int pos = start;
        while (pos < json.length()) {
            pos = json.indexOf("\"", pos);
            if (pos == -1) return -1;
            if (json.charAt(pos - 1) != '\\') {
                return pos;
            }
            pos++;
        }
        return -1;
    }
    
    public ConversationHistory getHistory() {
        return history;
    }
    
    public void clearHistory() {
        history.clear();
    }
}
