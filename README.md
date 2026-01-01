# JavaLlama
[![Java 21](https://img.shields.io/badge/Java-21-blue?style=flat-square&logo=java)]()
[![Build: Gradle](https://img.shields.io/badge/Build-Gradle-green?style=flat-square&logo=gradle)]()
[![Testing: JUnit 5](https://img.shields.io/badge/Testing-JUnit%205-brightgreen?style=flat-square&logo=junit5)]()
[![Mocking: Mockito](https://img.shields.io/badge/Mocking-Mockito-00C853?style=flat-square)]()
[![LLM: Ollama](https://img.shields.io/badge/LLM-Ollama-0ea5e9?style=flat-square)]()
[![PDF: Apache PDFBox](https://img.shields.io/badge/PDF-Apache%20PDFBox-F2C037?style=flat-square)]()

## Overview
An AI chatbot program in Java for interacting with Ollama, a local Large Language Model (LLM) runner. The application enhances responses by retrieving text from user-provided documents and feeding this content into the model (Context Stuffing).

## Features 
- Document upload and content extraction using Apache PDFBox.
- JavaFX interface with prompt field and conversation history.
- Model integration with Ollama via the Ollama4j library.
- Built using Java 21 and Gradle.
- Unit testing with JUnit 5 and Mockito.

## Requirements
- Java 21 (LTS)
- Gradle (wrapper included)
- Ollama installed & running locally (for model interaction)

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

## Configuration
- Configure Ollama host/port or model settings in application configuration (see src/main/resources or application properties in the codebase).
- Drop PDFs into the UI to extract and include context in prompts.
