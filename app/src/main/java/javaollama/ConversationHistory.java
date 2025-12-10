/**
======================================================================
CLASS NAME : ConversationHistory
DESCRIPTION : Manages the history of a conversation
AUTHOR     : Mickel Angelo Castoverde
COPYRIGHT  : macastroverde 2025
REVISION HISTORY
Date:           By:             Description:
2025-12-04      Mickel Angelo Castoverde  Creation of the program
======================================================================
*/
package javaollama;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConversationHistory {
    private List<Message> messages;

    /*
     * ======================================================================
     * METHOD NAME : ConversationHistory
     * DESCRIPTION : Default constructor
     * PRE-CONDITION : None
     * POST-CONDITION : Initializes empty message list
     * ======================================================================
     */
    public ConversationHistory() {
        this.messages = new ArrayList<>();
    }

    /*
     * ======================================================================
     * METHOD NAME : addUserMessage
     * DESCRIPTION : Adds a message from the user to the history
     * PRE-CONDITION : content is valid string
     * POST-CONDITION : User message added to list
     * ======================================================================
     */
    public void addUserMessage(String content) {
        messages.add(new Message("user", content));
    }

    /*
     * ======================================================================
     * METHOD NAME : addAssistantMessage
     * DESCRIPTION : Adds a message from the ai assistant to the history
     * PRE-CONDITION : content is valid string
     * POST-CONDITION : Assistant message added to list
     * ======================================================================
     */
    public void addAssistantMessage(String content) {
        messages.add(new Message("assistant", content));
    }

    /*
     * ======================================================================
     * METHOD NAME : getMessages
     * DESCRIPTION : Retrieves a copy of the messages in the history
     * PRE-CONDITION : None
     * POST-CONDITION : Returns list of messages
     * ======================================================================
     */
    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    /*
     * ======================================================================
     * METHOD NAME : clear
     * DESCRIPTION : Clears all messages from the history
     * PRE-CONDITION : None
     * POST-CONDITION : Message list is empty
     * ======================================================================
     */
    public void clear() {
        messages.clear();
    }

    /*
     * ======================================================================
     * METHOD NAME : size
     * DESCRIPTION : Returns the number of messages in the history
     * PRE-CONDITION : None
     * POST-CONDITION : Returns integer count of messages
     * ======================================================================
     */
    public int size() {
        return messages.size();
    }

    /**
     * ======================================================================
     * CLASS NAME : Message
     * DESCRIPTION : Represents a single message in the conversation
     * AUTHOR : Mickel Angelo Castoverde
     * COPYRIGHT : macastroverde 2025
     * REVISION HISTORY
     * Date: By: Description:
     * 2025-12-06 Mickel Angelo Castoverde Creation of the program
     * ======================================================================
     */
    public static class Message {
        private final String role;
        private final String content;

        /*
         * ======================================================================
         * METHOD NAME : Message
         * DESCRIPTION : Parameterized constructor
         * PRE-CONDITION : role and content are valid
         * POST-CONDITION : Message object initialized
         * ======================================================================
         */
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        /*
         * ======================================================================
         * METHOD NAME : getRole
         * DESCRIPTION : Accessor for the message role
         * PRE-CONDITION : None
         * POST-CONDITION : Returns role string
         * ======================================================================
         */
        public String getRole() {
            return role;
        }

        /*
         * ======================================================================
         * METHOD NAME : getContent
         * DESCRIPTION : Accessor for the message content
         * PRE-CONDITION : None
         * POST-CONDITION : Returns content string
         * ======================================================================
         */
        public String getContent() {
            return content;
        }

        /*
         * ======================================================================
         * METHOD NAME : toString
         * DESCRIPTION : Returns string representation
         * PRE-CONDITION : None
         * POST-CONDITION : Returns formatted string
         * ======================================================================
         */
        @Override
        public String toString() {
            return "Message{" +
                    "role='" + role + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }

        /*
         * ======================================================================
         * METHOD NAME : equals
         * DESCRIPTION : Checks equality with another object
         * PRE-CONDITION : None
         * POST-CONDITION : Returns true if objects match
         * ======================================================================
         */
        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Message message = (Message) o;
            return Objects.equals(role, message.role) &&
                    Objects.equals(content, message.content);
        }

        /*
         * ======================================================================
         * METHOD NAME : hashCode
         * DESCRIPTION : Generates hash code
         * PRE-CONDITION : None
         * POST-CONDITION : Returns integer hash
         * ======================================================================
         */
        @Override
        public int hashCode() {
            return Objects.hash(role, content);
        }
    }
}
