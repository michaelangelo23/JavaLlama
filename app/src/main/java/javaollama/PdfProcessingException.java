/**
======================================================================
CLASS NAME : PdfProcessingException
DESCRIPTION : Custom exception for pdf processing errors
AUTHOR     : Mickel Angelo Castoverde
COPYRIGHT  : macastroverde 2025
REVISION HISTORY
Date:           By:             Description:
2025-12-06      Mickel Angelo Castoverde  Creation of the program
======================================================================
*/
package javaollama;

public class PdfProcessingException extends Exception {

    /*
     * ======================================================================
     * METHOD NAME : PdfProcessingException
     * DESCRIPTION : Constructor with message
     * PRE-CONDITION : message is valid
     * POST-CONDITION : Exception initialized with message
     * ======================================================================
     */
    public PdfProcessingException(String message) {
        super(message);
    }

    /*
     * ======================================================================
     * METHOD NAME : PdfProcessingException
     * DESCRIPTION : Constructor with message and cause
     * PRE-CONDITION : message and cause are valid
     * POST-CONDITION : Exception initialized with message and cause
     * ======================================================================
     */
    public PdfProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
