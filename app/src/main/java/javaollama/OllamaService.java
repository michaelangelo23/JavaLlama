package javaollama;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.response.OllamaResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class OllamaService {
    
    private static final String OLLAMA_HOST = "http://localhost:11434";
    private final OllamaAPI api;
    private String modelName;
    
    public OllamaService() {
        this.api = new OllamaAPI(OLLAMA_HOST);
        this.api.setRequestTimeoutSeconds(60);
    }
    
    public boolean isServerRunning() {
        try {
            api.listModels();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public List<io.github.ollama4j.models.response.Model> getAvailableModels() {
        try {
            return api.listModels();
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }
    
    public void setModel(String modelName) {
        this.modelName = modelName;
    }
    
    public String chat(String prompt) throws OllamaBaseException, IOException, InterruptedException {
        OllamaResult result = api.generate(modelName, prompt, new HashMap<>());
        String response = result.getResponse();
        
        if (isEmpty(response) && hasThinking(result)) {
            response = parseThinkingResponse(result.getThinking());
        }
        
        return response != null ? response : "";
    }
    
    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }
    
    private boolean hasThinking(OllamaResult result) {
        return result.getThinking() != null && !result.getThinking().isEmpty();
    }
    
    private String parseThinkingResponse(String thinking) {
        System.out.println("\n[Model is thinking...]");
        
        String[] fields = {"answer", "reasoning", "thoughts"};
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
            String searchKey = "\"" + fieldName + "\"";
            int fieldStart = json.indexOf(searchKey);
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
    
    public String getModelName() {
        return modelName;
    }
    
    public OllamaAPI getAPI() {
        return api;
    }
}
