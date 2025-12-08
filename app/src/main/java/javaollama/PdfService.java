/**
======================================================================
CLASS NAME : PdfService
DESCRIPTION : Service class for handling pdf document operations
AUTHOR     : Mickel Angelo Castoverde
COPYRIGHT  : macastroverde 2025
REVISION HISTORY
Date:           By:             Description:
2025-12-06      Mickel Angelo Castoverde  Creation of the program
======================================================================
*/
package javaollama;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;
import java.io.File;
import java.io.IOException;

public class PdfService {

    /*
     * ======================================================================
     * METHOD NAME : PdfService
     * DESCRIPTION : Default constructor
     * PRE-CONDITION : None
     * POST-CONDITION : Service initialized
     * ======================================================================
     */
    public PdfService() {
    }

    /*
     * ======================================================================
     * METHOD NAME : extractText
     * DESCRIPTION : Extracts text content from a pdf file
     * PRE-CONDITION : pdfFile exists and is readable
     * POST-CONDITION : Returns the extracted text
     * ======================================================================
     */
    public String extractText(File pdfFile) throws PdfProcessingException {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new PdfProcessingException("File not found or is null");
        }

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new PdfProcessingException("Failed to extract text from PDF: " + pdfFile.getName(), e);
        }
    }

    /*
     * ======================================================================
     * METHOD NAME : writeOutput
     * DESCRIPTION : Writes the object state to the console
     * PRE-CONDITION : None
     * POST-CONDITION : Object state is printed to stdout
     * ======================================================================
     */
    public void writeOutput() {
        System.out.println("PdfService Status:");
        System.out.println("Ready to extract text from PDF documents.");
    }
}
