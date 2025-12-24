/**
======================================================================
CLASS NAME : ChatResponse
DESCRIPTION : Represents the response from the ollama chat model
AUTHOR     : Mickel Angelo Castoverde
COPYRIGHT  : macastroverde 2025
REVISION HISTORY
Date:           By:             Description:
2025-12-06      Mickel Angelo Castoverde  Creation of the program
======================================================================
*/
package javaollama;

public class ChatResponse {
    private final String response;

    /*
     * ======================================================================
     * METHOD NAME : ChatResponse
     * DESCRIPTION : Parameterized constructor
     * PRE-CONDITION : response string is valid
     * POST-CONDITION : Instance created with response text
     * ======================================================================
     */
    public ChatResponse(String response) {
        this.response = response;
    }

    /*
     * ======================================================================
     * METHOD NAME : getResponse
     * DESCRIPTION : Accessor for the response text
     * PRE-CONDITION : None
     * POST-CONDITION : Returns the response string
     * ======================================================================
     */
    public String getResponse() {
        return response;
    }

    /*
     * ======================================================================
     * METHOD NAME : toString
     * DESCRIPTION : Returns string representation of the object
     * PRE-CONDITION : None
     * POST-CONDITION : Returns formatted string
     * ======================================================================
     */
    @Override
    public String toString() {
        return "ChatResponse{" +
                "response='" + response + '\'' +
                '}';
    }
}
