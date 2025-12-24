/**
======================================================================
CLASS NAME : PdfServiceTest
DESCRIPTION : Unit tests for the PdfService class
AUTHOR     : Mickel Angelo Castoverde
COPYRIGHT  : macastroverde 2025
REVISION HISTORY
Date:           By:                       Description:
2025-12-06      Mickel Angelo Castoverde  Creation of the program
======================================================================
*/
package javaollama;

import org.junit.jupiter.api.Test;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class PdfServiceTest {

    /*
     * ======================================================================
     * METHOD NAME : testExtractTextFromValidPdf
     * DESCRIPTION : Tests text extraction from a generated valid PDF
     * PRE-CONDITION : Temp PDF file created
     * POST-CONDITION : Extracted text matches content
     * ======================================================================
     */
    @Test
    void testExtractTextFromValidPdf() throws IOException, PdfProcessingException {
        PdfService service = new PdfService();
        File tempFile = File.createTempFile("test", ".pdf");
        tempFile.deleteOnExit();

        // create a simple pdf with "hello world"
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                contents.beginText();
                // pdfbox 3.0.x way of getting standard fonts
                contents.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contents.newLineAtOffset(100, 700);
                contents.showText("Hello World");
                contents.endText();
            }
            doc.save(tempFile);
        }

        String text = service.extractText(tempFile);
        assertNotNull(text);
        assertTrue(text.contains("Hello World"));
    }

    /*
     * ======================================================================
     * METHOD NAME : testExtractTextFromNonExistentFile
     * DESCRIPTION : Tests error handling for non-existent file
     * PRE-CONDITION : File does not exist
     * POST-CONDITION : Throws PdfProcessingException
     * ======================================================================
     */
    @Test
    void testExtractTextFromNonExistentFile() {
        PdfService service = new PdfService();
        File nonExistent = new File("does_not_exist.pdf");
        assertThrows(PdfProcessingException.class, () -> service.extractText(nonExistent));
    }
}