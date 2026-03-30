import com.careerforge.MainApp;

/*
 * FEDI-STANDARD LAUNCHER
 * No public class. No static args. Pure execution.
 */
void main() {
    IO.println("=== 🚀 INITIATING OMNI-CHIMERA UI ===");

    // FEYNMAN COMMENT: JavaFX usually expects the 'args' from the old main method.
    // Since void main() hides them, we bypass it by feeding it a brand new, empty String array!
    MainApp.launch(MainApp.class, new String[0]);
}