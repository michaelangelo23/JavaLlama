package javaollama;

import java.util.ArrayList;
import java.util.List;

// chat history storage
public class ConversationHistory {
    private List<Message> messages;
    
    public ConversationHistory() {
        this.messages = new ArrayList<>();
    }
    
    public void addUserMessage(String content) {
        messages.add(new Message("user", content));
    }
    
    public void addAssistantMessage(String content) {
        messages.add(new Message("assistant", content));
    }
    
    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }
    
    public void clear() {
        messages.clear();
    }
    
    public int size() {
        return messages.size();
    }
    
    // chat message
    public static class Message {
        public final String role;
        public final String content;
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
