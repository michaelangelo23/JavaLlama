/**
======================================================================
CLASS NAME : OllamaServiceException
DESCRIPTION : Custom exception for ollama service related errors
AUTHOR     : Mickel Angelo Castoverde
COPYRIGHT  : macastroverde 2025
REVISION HISTORY
Date:           By:                       Description:
2025-12-03      Mickel Angelo Castoverde  Creation of the program
======================================================================
*/
package javaollama;

public class OllamaServiceException extends Exception {

    /*
     * ======================================================================
     * METHOD NAME : OllamaServiceException
     * DESCRIPTION : Constructor with message
     * PRE-CONDITION : message is valid
     * POST-CONDITION : Exception initialized with message
     * ======================================================================
     */
    public OllamaServiceException(String message) {
        super(message);
    }

    /*
     * ======================================================================
     * METHOD NAME : OllamaServiceException
     * DESCRIPTION : Constructor with message and cause
     * PRE-CONDITION : message and cause are valid
     * POST-CONDITION : Exception initialized with message and cause
     * ======================================================================
     */
    public OllamaServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
