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
            pb.inheritIO(); // to prevent buffer blocking by inheriting IO (fixing the process pause when
                            // running the code)
            // commenting pb.inheritIO() can also prevent displaying output in running
            // gradle, only use for debugging
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
            System.out.println("Stopping Ollama server...");
            process.destroy();
            try {
                if (!process.waitFor(3, java.util.concurrent.TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
            } catch (InterruptedException e) {
                process.destroyForcibly();
                Thread.currentThread().interrupt();
            }
        }

        // Force kill any lingering ollama processes (Windows only)
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try {
                new ProcessBuilder("taskkill", "/F", "/IM", "ollama.exe").start();
            } catch (Exception e) {
                // Ignore errors

            }
        }

    }

    /*
     * ======================================================================
     * METHOD NAME : ensureServerRunning
     * DESCRIPTION : Ensures the Ollama server is running and ready
     * PRE-CONDITION : ollama service is initialized
     * POST-CONDITION : Server is running and connected, or exception thrown
     * ======================================================================
     */
    public void ensureServerRunning(OllamaService ollama, java.util.function.Consumer<String> statusCallback)
            throws Exception {
        // Initial check
        if (ollama.isServerRunning()) {
            statusCallback.accept("Server is running");
            return;
        }

        // Server not running, attempt to start
        statusCallback.accept("Starting Ollama server...");
        if (!startServer()) {
            throw new Exception("Could not start Ollama server process");
        }

        // Wait for server to become responsive
        statusCallback.accept("Waiting for server...");
        int attempts = 0;
        while (attempts < 5) {
            Thread.sleep(1000); // Wait 1 second between checks
            if (ollama.isServerRunning()) {
                statusCallback.accept("Server connected");
                return;
            }
            attempts++;
        }

        throw new Exception("Server started but failed to respond after 5 seconds");
    }
}
