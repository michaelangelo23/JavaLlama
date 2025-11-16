package javaollama;

import java.util.List;
import java.util.Scanner;

public class App {
    
    private OllamaService ollama;
    private Scanner scanner;
    
    public static void main(String[] args) {
        new App().run();
    }
    
    public void run() {
        System.out.println("JavaLlama Local Chatbot\n");
        ollama = new OllamaService();
        
        if (!checkServerConnection()) return;
        
        List<io.github.ollama4j.models.response.Model> models = ollama.getAvailableModels();
        if (!validateModels(models)) return;
        
        String selectedModel = selectModel(models);
        ollama.setModel(selectedModel);
        
        testChat();
    }
    
    private boolean checkServerConnection() {
        System.out.println("Checking Ollama server connection...");
        if (!ollama.isServerRunning()) {
            System.err.println("Ollama server is not running!");
            System.err.println("Please start Ollama and try again.");
            return false;
        }
        System.out.println("Ollama server is running\n");
        return true;
    }
        
    private boolean validateModels(List<io.github.ollama4j.models.response.Model> models) {
        System.out.println("Getting available models...");
        if (models.isEmpty()) {
            System.err.println("No models found!");
            System.err.println("Please pull a model first using: ollama pull <model-name>");
            return false;
        }
        return true;
    }
    
    private String selectModel(List<io.github.ollama4j.models.response.Model> models) {
        if (models.size() == 1) {
            return selectSingleModel(models.get(0));
        }
        return selectFromMultipleModels(models);
    }
    
    private String selectSingleModel(io.github.ollama4j.models.response.Model model) {
        String modelName = model.getName();
        System.out.println("Found 1 model: " + modelName);
        System.out.println("Using this model automatically\n");
        return modelName;
    }
    
    private String selectFromMultipleModels(List<io.github.ollama4j.models.response.Model> models) {
        displayModels(models);
        
        if (System.console() == null) {
            return selectDefaultModel(models);
        }
        
        return selectModelInteractively(models);
    }
    
    private void displayModels(List<io.github.ollama4j.models.response.Model> models) {
        System.out.println("Found " + models.size() + " models:");
        for (int i = 0; i < models.size(); i++) {
            System.out.println((i + 1) + ". " + models.get(i).getName());
        }
    }
    
    private String selectDefaultModel(List<io.github.ollama4j.models.response.Model> models) {
        String modelName = models.get(0).getName();
        System.out.println("\nNot running in interactive mode, using first model automatically.");
        System.out.println("Selected: " + modelName + "\n");
        return modelName;
    }
    
    private String selectModelInteractively(List<io.github.ollama4j.models.response.Model> models) {
        scanner = new Scanner(System.in);
        int choice = getUserChoice(models.size());
        scanner.close();
        
        String modelName = models.get(choice - 1).getName();
        System.out.println("Selected: " + modelName + "\n");
        return modelName;
    }
    
    private int getUserChoice(int maxChoice) {
        int choice = -1;
        while (choice < 1 || choice > maxChoice) {
            System.out.print("\nSelect a model (1-" + maxChoice + "): ");
            try {
                choice = scanner.nextInt();
                if (choice < 1 || choice > maxChoice) {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
        return choice;
    }
        
    private void testChat() {
        System.out.println("Testing chat functionality");
        System.out.println("Sending prompt: 'Explain what Java programming is in one sentence.'\n");
        
        try {
            String response = ollama.chat("Explain what Java programming is in one sentence.");
            displayResponse(response);
        } catch (Exception e) {
            System.err.println("Error during chat: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void displayResponse(String response) {
        System.out.println("JavaLlama:");
        System.out.println(response);
        System.out.println("\nChat test successful");
    }
}
