import com.careerforge.service.PdfTrojanService;

// THE GAME ENGINE
void main() {
    IO.println("=== 🖨️ AWAKENING THE PDF FORGER ===");

    PdfTrojanService pdfService = new PdfTrojanService();

    String myCV = "EXPERIENCE:\n- Built a God-Tier Java Application using JDK 25.\n- Mastered MySQL, DAOs, and Generative AI Integrations.";
    String myActionPlan = "WEEK 1: Audit your company's backend infrastructure.\nWEEK 2: Deploy Fedi-Standard architecture to increase revenue by 200%.";

    // This will create a file named "Fedi_Touati_CV.pdf" directly in your IntelliJ project folder!
    pdfService.generateTrojanPDF("Fedi_Touati_CV", "Fedi The Architect", myCV, myActionPlan);

    IO.println("=== 🛑 SHUTTING DOWN FORGE ===");
}