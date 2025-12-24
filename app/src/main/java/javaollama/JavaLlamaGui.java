/**
======================================================================
CLASS NAME : JavaLlamaGui
DESCRIPTION : Main application class for the javallama chat interface, handling ui layout and user interactions
AUTHOR     : Mickel Angelo Castoverde
COPYRIGHT  : macastroverde 2025
REVISION HISTORY
Date:           By:             Description:
2025-12-06      Mickel Angelo Castoverde  Creation of the program
2025-12-08      Mickel Angelo Castoverde  Updated to use centralized model name from OllamaService
======================================================================
*/
package javaollama;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;

public class JavaLlamaGui extends Application {

    private OllamaService ollama;
    private PdfService pdfService;
    private OllamaServerManager serverManager;
    private TextArea chatArea;
    private TextField inputField;
    private Button sendButton;
    private Label statusLabel;
    private volatile Thread thinkingThread;

    /*
     * ======================================================================
     * METHOD NAME : start
     * DESCRIPTION : Starts the primary stage of the application
     * PRE-CONDITION : JavaFX runtime initialized
     * POST-CONDITION : GUI window is displayed and services initialized
     * ======================================================================
     */
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

    /*
     * ======================================================================
     * METHOD NAME : createTopBar
     * DESCRIPTION : Creates the top bar layout and controls
     * PRE-CONDITION : None
     * POST-CONDITION : Returns the constructed HBox for top bar
     * ======================================================================
     */
    private HBox createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(5));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label modelLabel = new Label("Model: " + OllamaService.getDefaultModel());
        modelLabel.setStyle("-fx-font-weight: bold;");

        Button clearButton = new Button("Clear Chat");
        clearButton.setOnAction(e -> clearChat());

        Button uploadButton = new Button("Upload PDF");
        uploadButton.setOnAction(e -> uploadPdf());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statusLabel = new Label("Initializing...");
        statusLabel.setStyle("-fx-text-fill: orange;");

        topBar.getChildren().addAll(
                modelLabel, clearButton, uploadButton, spacer, statusLabel);

        return topBar;
    }

    /*
     * ======================================================================
     * METHOD NAME : createChatArea
     * DESCRIPTION : Creates the chat display area
     * PRE-CONDITION : None
     * POST-CONDITION : Returns the constructed VBox for chat area
     * ======================================================================
     */
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

    /*
     * ======================================================================
     * METHOD NAME : createInputArea
     * DESCRIPTION : Creates the input controls area
     * PRE-CONDITION : None
     * POST-CONDITION : Returns the constructed HBox for input area
     * ======================================================================
     */
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

    /*
     * ======================================================================
     * METHOD NAME : initializeOllama
     * DESCRIPTION : Initializes the ollama service and checks server status
     * PRE-CONDITION : None
     * POST-CONDITION : Background thread started to check server connectivity
     * ======================================================================
     */
    private void initializeOllama() {
        new Thread(() -> {
            try {
                Platform.runLater(() -> {
                    statusLabel.setText("Checking Ollama...");
                    statusLabel.setStyle("-fx-text-fill: orange;");
                });

                ollama = new OllamaService();
                pdfService = new PdfService();
                serverManager = new OllamaServerManager();
                // Ensure server is stopped even if the program is killed via terminal (Ctrl+C)
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (serverManager != null) {
                        serverManager.stopServer();
                    }
                }));

                // Logic Moved to OllamaServerManager
                serverManager.ensureServerRunning(ollama, status -> {
                    Platform.runLater(() -> {
                        statusLabel.setText(status);
                        // Optional: Log technical steps to chat if needed, or just keep it simple
                        // appendToChat("System", status);
                    });
                });

                ollama.setModel(OllamaService.getDefaultModel());

                Platform.runLater(() -> {
                    statusLabel.setText("Connected");
                    statusLabel.setStyle("-fx-text-fill: green;");
                    sendButton.setDisable(false);
                    appendToChat("System", "Ready with " + OllamaService.getDefaultModel());
                    writeOutput(); // demonstrates object output on startup
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

    /*
     * ======================================================================
     * METHOD NAME : sendMessage
     * DESCRIPTION : Sends the chat message and handles response
     * PRE-CONDITION : Input field has text
     * POST-CONDITION : Message sent, thinking status shown, response awaited
     * ======================================================================
     */
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty())
            return;

        inputField.clear();
        inputField.setDisable(true);
        sendButton.setDisable(true);

        appendToChat("You", message);

        long startTime = System.currentTimeMillis();
        appendThinkingStatus(startTime);

        new Thread(() -> {
            try {
                ChatResponse response = ollama.chat(message);
                long thinkingTime = System.currentTimeMillis() - startTime;
                Platform.runLater(() -> {
                    removeThinkingStatus();
                    appendResponse(response.getResponse(), thinkingTime);
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

    /*
     * ======================================================================
     * METHOD NAME : clearChat
     * DESCRIPTION : Clears the chat history
     * PRE-CONDITION : None
     * POST-CONDITION : Chat area cleared and service history reset
     * ======================================================================
     */
    private void clearChat() {
        chatArea.clear();
        ollama.clearHistory();
        statusLabel.setText("Connected");
        statusLabel.setStyle("-fx-text-fill: green;");
        appendToChat("System", "Chat cleared");
    }

    /*
     * ======================================================================
     * METHOD NAME : uploadPdf
     * DESCRIPTION : Handles pdf upload and text extraction
     * PRE-CONDITION : User selects a valid PDF file
     * POST-CONDITION : PDF text extracted and set as context
     * ======================================================================
     */
    private void uploadPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select PDF File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                String text = pdfService.extractText(selectedFile);
                ollama.setContext(text);
                Platform.runLater(() -> {
                    statusLabel.setText("PDF Loaded: " + selectedFile.getName());
                    statusLabel.setStyle("-fx-text-fill: blue;");
                    appendToChat("System", "Context loaded from " + selectedFile.getName());
                });
            } catch (PdfProcessingException e) {
                Platform.runLater(() -> showAlert("Error", "Failed to read PDF: " + e.getMessage()));
            }
        }
    }

    /*
     * ======================================================================
     * METHOD NAME : appendToChat
     * DESCRIPTION : Appends a message to the chat area
     * PRE-CONDITION : Messages are valid strings
     * POST-CONDITION : Message displayed in chat area
     * ======================================================================
     */
    private void appendToChat(String sender, String message) {
        chatArea.appendText(sender + ": " + message + "\n");
    }

    /*
     * ======================================================================
     * METHOD NAME : appendThinkingStatus
     * DESCRIPTION : Shows the thinking timer
     * PRE-CONDITION : startTime is valid timestamp
     * POST-CONDITION : Thinking status appended and timer thread started
     * ======================================================================
     */
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

    /*
     * ======================================================================
     * METHOD NAME : removeThinkingStatus
     * DESCRIPTION : Removes the thinking timer
     * PRE-CONDITION : None
     * POST-CONDITION : Thinking thread stopped and text removed from chat
     * ======================================================================
     */
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

    /*
     * ======================================================================
     * METHOD NAME : appendResponse
     * DESCRIPTION : Appends the response with timing info
     * PRE-CONDITION : Response is valid string
     * POST-CONDITION : Response formatted and added to chat
     * ======================================================================
     */
    private void appendResponse(String response, long thinkingTimeMs) {
        double thinkingTimeSec = thinkingTimeMs / 1000.0;
        chatArea.appendText(String.format("\nAssistant (%.1fs): %s\n\n", thinkingTimeSec, response));
    }

    /*
     * ======================================================================
     * METHOD NAME : showAlert
     * DESCRIPTION : Shows an error popup
     * PRE-CONDITION : None
     * POST-CONDITION : Alert dialog displayed and waited
     * ======================================================================
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /*
     * ======================================================================
     * METHOD NAME : getChatArea
     * DESCRIPTION : Gets the chat area
     * PRE-CONDITION : None
     * POST-CONDITION : Returns the chat area instance
     * ======================================================================
     */
    public TextArea getChatArea() {
        return chatArea;
    }

    /*
     * ======================================================================
     * METHOD NAME : getInputField
     * DESCRIPTION : Gets the input field
     * PRE-CONDITION : None
     * POST-CONDITION : Returns the input field instance
     * ======================================================================
     */
    public TextField getInputField() {
        return inputField;
    }

    /*
     * ======================================================================
     * METHOD NAME : getSendButton
     * DESCRIPTION : Gets the send button
     * PRE-CONDITION : None
     * POST-CONDITION : Returns the send button instance
     * ======================================================================
     */
    public Button getSendButton() {
        return sendButton;
    }

    /*
     * ======================================================================
     * METHOD NAME : getStatusLabel
     * DESCRIPTION : Gets the status label
     * PRE-CONDITION : None
     * POST-CONDITION : Returns the status label instance
     * ======================================================================
     */
    public Label getStatusLabel() {
        return statusLabel;
    }

    /*
     * ======================================================================
     * METHOD NAME : writeOutput
     * DESCRIPTION : Writes the object state to the console
     * PRE-CONDITION : None
     * POST-CONDITION : Object state printed to stdout
     * ======================================================================
     */
    public void writeOutput() {
        System.out.println("JavaLlamaGui Status:");
        System.out.println("Model: " + OllamaService.getDefaultModel());
        System.out.println("Status: " + statusLabel.getText());
    }

    /*
     * ======================================================================
     * METHOD NAME : main
     * DESCRIPTION : Main entry point
     * PRE-CONDITION : None
     * POST-CONDITION : Application launched
     * ======================================================================
     */
    /*
     * ======================================================================
     * METHOD NAME : stop
     * DESCRIPTION : Cleanup method called when application stops
     * PRE-CONDITION : None
     * POST-CONDITION : Server stopped and application exits
     * ======================================================================
     */
    @Override
    public void stop() throws Exception {
        if (serverManager != null) {
            serverManager.stopServer();
        }
        super.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
