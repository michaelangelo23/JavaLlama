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
    private Process process;

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
            // pb.inheritIO(); // to prevent buffer blocking by inheriting IO (fixing the
            // process pause when
            // running the code)
            this.process = pb.start();

            // give it a moment to start
            Thread.sleep(2000);
            return process.isAlive();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * ======================================================================
     * METHOD NAME : stopServer
     * DESCRIPTION : Stops the ollama server process
     * PRE-CONDITION : None
     * POST-CONDITION : Server process is terminated
     * ======================================================================
     */
    public void stopServer() {
        if (process != null && process.isAlive()) {
            process.destroy();
        }
    }
}
