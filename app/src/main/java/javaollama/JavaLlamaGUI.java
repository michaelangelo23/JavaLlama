package javaollama;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

// chat GUI
public class JavaLlamaGUI extends Application {
    
    private static final String MODEL_NAME = "qwen3-vl:2b";
    private OllamaService ollama;
    private TextArea chatArea;
    private TextField inputField;
    private Button sendButton;
    private Label statusLabel;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaLlama");
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        root.setTop(createTopBar());
        root.setCenter(createChatArea());
        root.setBottom(createInputArea());
        
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        initializeOllama();
    }
    
    // top bar setup
    private HBox createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(5));
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Label modelLabel = new Label("Model: " + MODEL_NAME);
        modelLabel.setStyle("-fx-font-weight: bold;");
        
        Button clearButton = new Button("Clear Chat");
        clearButton.setOnAction(e -> clearChat());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        statusLabel = new Label("Initializing...");
        statusLabel.setStyle("-fx-text-fill: orange;");
        
        topBar.getChildren().addAll(
            modelLabel, clearButton, spacer, statusLabel
        );
        
        return topBar;
    }
    
    // chat display
    private VBox createChatArea() {
        VBox chatBox = new VBox(5);
        chatBox.setPadding(new Insets(5));
        
        Label chatLabel = new Label("Conversation");
        chatLabel.setStyle("-fx-font-weight: bold;");
        
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12px;");
        VBox.setVgrow(chatArea, Priority.ALWAYS);
        
        chatBox.getChildren().addAll(chatLabel, chatArea);
        return chatBox;
    }
    
    // input controls
    private HBox createInputArea() {
        HBox inputBox = new HBox(5);
        inputBox.setPadding(new Insets(5));
        inputBox.setAlignment(Pos.CENTER);
        
        inputField = new TextField();
        inputField.setPromptText("Type your message here...");
        inputField.setOnAction(e -> sendMessage());
        HBox.setHgrow(inputField, Priority.ALWAYS);
        
        sendButton = new Button("Send");
        sendButton.setPrefWidth(80);
        sendButton.setOnAction(e -> sendMessage());
        sendButton.setDisable(true);
        
        inputBox.getChildren().addAll(inputField, sendButton);
        return inputBox;
    }
    // setup ollama
    private void initializeOllama() {
        new Thread(() -> {
            try {
                Platform.runLater(() -> {
                    statusLabel.setText("Checking Ollama...");
                    statusLabel.setStyle("-fx-text-fill: orange;");
                });
                
                ollama = new OllamaService();
                
                // check server
                if (!ollama.isServerRunning()) {
                    Platform.runLater(() -> {
                        statusLabel.setText("Starting Ollama server...");
                        appendToChat("System", "Ollama server not running. Attempting to start...");
                    });
                    
                    if (!startOllamaServer()) {
                        Platform.runLater(() -> {
                            statusLabel.setText("Failed to start server");
                            statusLabel.setStyle("-fx-text-fill: red;");
                            showAlert("Error", "Could not start Ollama server. Please start it manually.");
                        });
                        return;
                    }
                    
                    Thread.sleep(3000); // wait for server
                }
                
                Platform.runLater(() -> {
                    statusLabel.setText("Verifying server...");
                    appendToChat("System", "Ollama server is running. Checking model availability...");
                });
                
                if (!ollama.isServerRunning()) {
                    Platform.runLater(() -> {
                        statusLabel.setText("Server not responding");
                        statusLabel.setStyle("-fx-text-fill: red;");
                        showAlert("Error", "Ollama server is not responding.");
                    });
                    return;
                }
                
                ollama.setModel(MODEL_NAME);
                
                Platform.runLater(() -> {
                    statusLabel.setText("Connected");
                    statusLabel.setStyle("-fx-text-fill: green;");
                    sendButton.setDisable(false);
                    appendToChat("System", "Ready with " + MODEL_NAME);
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Connection failed");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    showAlert("Error", "Failed to connect: " + e.getMessage());
                });
            }
        }).start();
    }
    
    // start ollama
    private boolean startOllamaServer() {
        try {
            ProcessBuilder pb = new ProcessBuilder("ollama", "serve");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            Thread.sleep(2000); // wait for startup
            return process.isAlive();
        } catch (Exception e) {
            return false;
        }
    }
    
    // send chat message
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;
        
        inputField.clear();
        inputField.setDisable(true);
        sendButton.setDisable(true);
        
        appendToChat("You", message);
        
        long startTime = System.currentTimeMillis();
        appendThinkingStatus(startTime);
        
        new Thread(() -> {
            try {
                String response = ollama.chat(message);
                long thinkingTime = System.currentTimeMillis() - startTime;
                Platform.runLater(() -> {
                    removeThinkingStatus();
                    appendResponse(response, thinkingTime);
                    inputField.setDisable(false);
                    sendButton.setDisable(false);
                    inputField.requestFocus();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    removeThinkingStatus();
                    appendToChat("Error", e.getMessage());
                    inputField.setDisable(false);
                    sendButton.setDisable(false);
                });
            }
        }).start();
    }
    
    // clear history
    private void clearChat() {
        chatArea.clear();
        ollama.clearHistory();
        appendToChat("System", "Chat cleared");
    }
    
    // add to chat
    private void appendToChat(String sender, String message) {
        chatArea.appendText(sender + ": " + message + "\n");
    }
    
    private volatile Thread thinkingThread;
    
    // show thinking timer
    private void appendThinkingStatus(long startTime) {
        chatArea.appendText("\n[Thinking... 0.0s]\n");
        
        thinkingThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(100);
                    double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
                    Platform.runLater(() -> {
                        String text = chatArea.getText();
                        int thinkingIndex = text.lastIndexOf("[Thinking...");
                        if (thinkingIndex != -1) {
                            int endIndex = text.indexOf("]\n", thinkingIndex);
                            if (endIndex != -1) {
                                String newText = text.substring(0, thinkingIndex) + 
                                               String.format("[Thinking... %.1fs]", elapsed) + 
                                               text.substring(endIndex);
                                chatArea.setText(newText);
                                chatArea.setScrollTop(Double.MAX_VALUE);
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thinkingThread.setDaemon(true);
        thinkingThread.start();
    }
    
    // remove thinking timer
    private void removeThinkingStatus() {
        if (thinkingThread != null) {
            thinkingThread.interrupt();
            thinkingThread = null;
        }
        String text = chatArea.getText();
        int thinkingIndex = text.lastIndexOf("[Thinking...");
        if (thinkingIndex != -1) {
            int endIndex = text.indexOf("]\n", thinkingIndex);
            if (endIndex != -1) {
                chatArea.setText(text.substring(0, thinkingIndex) + text.substring(endIndex + 2));
            }
        }
    }
    
    // add response with time
    private void appendResponse(String response, long thinkingTimeMs) {
        double thinkingTimeSec = thinkingTimeMs / 1000.0;
        chatArea.appendText(String.format("\nAssistant (%.1fs): %s\n\n", thinkingTimeSec, response));
    }
    
    // error popup
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
