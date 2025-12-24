# JavaLlama

## Overview
An AI chatbot program in Java for interacting with Ollama, a local Large Language Model (LLM) runner.
The application enhances responses by retrieving text from user-provided documents and feeding this content into the model (Context Stuffing).

## Features 
- Document Upload and extract content using **Apache PDFBox**.
- **JavaFX** interface with Prompt Field and Conversation history.
- Model Integration with **Ollama** via the **Ollama4j** library.
- Built using **Java 21** and **Gradle**.
- Unit testing with **JUnit 5** and **Mockito**.

## Getting Started

### Build the Project
```bash
./gradlew build
```

### Run the Application
```bash
./gradlew run
```

### Run Tests
```bash
./gradlew test

```

