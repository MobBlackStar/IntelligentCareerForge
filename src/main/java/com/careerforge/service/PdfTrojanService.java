package com.careerforge.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * // FEDI: The PDF Engine.
 * // This doesn't just export a CV. It exports the "Trojan Horse" (CV + 30-Day Action Plan).
 */
public class PdfTrojanService {

    /*
     * FEYNMAN COMMENT: How does iTextPDF work?
     * Imagine making a physical book.
     * 1. You buy empty paper (The Document).
     * 2. You hire a scribe with a pen (The PdfWriter).
     * 3. You open the book, dictate the words (document.add), and close the book.
     */

    public void generateTrojanPDF(String targetFileName, String userName, String cvContent, String actionPlan) {

        // 1. The Empty Paper
        Document document = new Document();

        try {
            // 2. The Scribe (Writes the document to your hard drive)
            PdfWriter.getInstance(document, new FileOutputStream(targetFileName + ".pdf"));

            // 3. Open the book to start writing
            document.open();

            // === PAGE 1: THE FEDI-STANDARD CV ===
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            Paragraph title = new Paragraph(userName + " - Elite Candidate", titleFont);
            title.setSpacingAfter(20);
            document.add(title);

            Paragraph cvBody = new Paragraph(cvContent, bodyFont);
            document.add(cvBody);

            // === PAGE 2: THE TROJAN HORSE (The Flex) ===
            // This forces the PDF to create a brand new page
            document.newPage();

            Paragraph trojanTitle = new Paragraph("Strategic 30-Day Action Plan", titleFont);
            trojanTitle.setSpacingAfter(20);
            document.add(trojanTitle);

            Paragraph trojanBody = new Paragraph(actionPlan, bodyFont);
            document.add(trojanBody);

            // 4. Close the book! (If you don't close it, the file will be corrupted)
            document.close();

            IO.println("✅ TROJAN DEPLOYED: Successfully exported " + targetFileName + ".pdf");

        } catch (DocumentException | FileNotFoundException e) {
            IO.println("❌ Failed to forge PDF: " + e.getMessage());
        }
    }
}