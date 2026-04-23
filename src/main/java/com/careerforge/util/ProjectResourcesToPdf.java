package application.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.awt.Desktop;

public class ProjectResourcesToPdf {

    public static void main(String[] args) {
        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        String pdfFileName = desktopPath + "Project_Resources_Export.pdf";

        Document document = new Document(PageSize.A4, 30, 30, 30, 30);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFileName));
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new BaseColor(102, 0, 204)); // Purple for resources
            Font codeFont = FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.DARK_GRAY);

            // Title
            Paragraph title = new Paragraph("PROJECT RESOURCES EXPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);
            document.add(new Chunk(new LineSeparator(1f, 100f, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, -5f)));

            // Scan the Resources directory
            Path sourceDir = Paths.get("src/main/resources");
            if (Files.exists(sourceDir)) {
                try (Stream<Path> paths = Files.walk(sourceDir)) {
                    paths.filter(Files::isRegularFile)
                         // Ensure we don't try to parse binary images like .png/.jpg as text!
                         .filter(p -> {
                             String name = p.toString().toLowerCase();
                             return name.endsWith(".fxml") || name.endsWith(".css") || name.endsWith(".properties") || name.endsWith(".txt") || name.endsWith(".json");
                         })
                         .forEach(path -> {
                             // Extract relative folder path
                             String relativePath = sourceDir.relativize(path).toString();
                             String folderName = relativePath.contains(File.separator) ? 
                                     relativePath.substring(0, relativePath.lastIndexOf(File.separator)) : "[ROOT RESOURCES]";
                             
                             addFileToPDF(document, path.toFile(), folderName, headerFont, codeFont);
                         });
                }
            } else {
                 document.add(new Paragraph("No resources folder found at src/main/resources", headerFont));
            }

            document.close();
            System.out.println("✅ Resources scanned successfully! Saved to: " + pdfFileName);

            // Automatically open the PDF
            File pdfFile = new File(pdfFileName);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addFileToPDF(Document document, File file, String folderName, Font headerFont, Font codeFont) {
        try {
            document.add(Chunk.NEWLINE);
            Paragraph header = new Paragraph("File: " + file.getName() + "  |  Folder: " + folderName, headerFont);
            header.setSpacingAfter(5f);
            document.add(header);
            document.add(new Chunk(new LineSeparator(0.5f, 100f, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, -5f)));
            
            // Read file content and add as code block
            String content = new String(Files.readAllBytes(file.toPath()));
            Paragraph codeParagraph = new Paragraph(content, codeFont);
            codeParagraph.setSpacingBefore(10f);
            codeParagraph.setSpacingAfter(20f);
            
            document.add(codeParagraph);
            
        } catch (Exception e) {
            System.err.println("Failed to read file: " + file.getName());
        }
    }
}
