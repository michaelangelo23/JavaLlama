/**
======================================================================
CLASS NAME : OllamaServerManager
DESCRIPTION : Manages the local ollama server process
AUTHOR     : Mickel Angelo Castoverde
COPYRIGHT  : macastroverde 2025
REVISION HISTORY
Date:           By:             Description:
2025-12-06      Mickel Angelo Castoverde  Creation of the program
======================================================================
*/
package javaollama;

public class OllamaServerManager {

    /*
     * ======================================================================
     * METHOD NAME : OllamaServerManager
     * DESCRIPTION : Default constructor
     * PRE-CONDITION : None
     * POST-CONDITION : Instance created
     * ======================================================================
     */
    public OllamaServerManager() {
    }

    /*
     * ======================================================================
     * METHOD NAME : startServer
     * DESCRIPTION : Starts the ollama server process
     * PRE-CONDITION : System has ollama installed
     * POST-CONDITION : Returns true if server started, false otherwise
     * ======================================================================
     */
    public boolean startServer() {
        try {
            ProcessBuilder pb = new ProcessBuilder("ollama", "serve");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // give it a moment to start
            Thread.sleep(2000);
            return process.isAlive();
        } catch (Exception e) {
            return false;
        }
    }
}
