import com.careerforge.dao.ApplicationDAO;
import com.careerforge.dao.CVDAO;
import com.careerforge.dao.JobOfferDAO;
import com.careerforge.dao.SkillDAO;
import com.careerforge.dao.UserDAO;
import com.careerforge.model.Application;
import com.careerforge.model.CV;
import com.careerforge.model.JobOffer;
import com.careerforge.model.Skill;
import com.careerforge.model.User;

/*
 * FEDI-STANDARD SEED ENGINE
 * Run this ONCE to populate the entire MySQL Database with beautiful test data for Sarah.
 * Runner Script (No package declaration. No public class).
 */
void main() {
    IO.println("=== 🌱 INITIATING FEDI-STANDARD DATABASE SEEDING ===");

    UserDAO userDAO = new UserDAO();
    JobOfferDAO jobDAO = new JobOfferDAO();
    ApplicationDAO appDAO = new ApplicationDAO();
    SkillDAO skillDAO = new SkillDAO();
    CVDAO cvDAO = new CVDAO();

    try {
        // 1. Inject The Architect & The UI Master
        IO.println("Injecting Users...");
        userDAO.create(new User("Fedi The Architect", "fedi@forge.com", "pass123", "Chief Technology Officer"));
        userDAO.create(new User("Sarah The Designer", "sarah@forge.com", "pass123", "Lead UI Gamification"));

        // 2. Inject Job Offers (The Market)
        IO.println("Injecting Job Offers...");
        jobDAO.create(new JobOffer("Senior Backend Engineer", "Vermeg", "Looking for Java Spring Boot experts.", "Scaling backend architecture."));
        jobDAO.create(new JobOffer("Data Scientist", "InstaDeep", "Must know Python, TensorFlow, and AI pipelines.", "Need faster AI model training."));
        jobDAO.create(new JobOffer("Lead UX Designer", "Google", "Design addictive dark-mode interfaces.", "User retention is dropping."));

        // 3. Inject Applications (The War Room Kanban Cards)
        IO.println("Injecting Kanban Cards...");
        // Assuming Fedi is User ID 1, Vermeg is Job 1, InstaDeep is Job 2
        appDAO.create(new Application(1, 1, 1, "TARGETED", "Pending...", "Pending...", ""));
        appDAO.create(new Application(1, 2, 1, "APPLIED", "Fedi is elite.", "Plan deployed.", ""));

        // Assuming Sarah is User ID 2, Google is Job 3
        appDAO.create(new Application(2, 3, 1, "INTERVIEWING", "Sarah's UI is perfect.", "Plan deployed.", ""));

        // 4. Inject RPG Skills (The Skill Tree)
        IO.println("Injecting RPG Skills...");
        skillDAO.create(new Skill(1, "Java JDK 25", true));
        skillDAO.create(new Skill(1, "MySQL", true));
        skillDAO.create(new Skill(1, "AWS Cloud", false)); // Missing skill (Red Node)
        skillDAO.create(new Skill(2, "JavaFX CSS", true));
        skillDAO.create(new Skill(2, "React.js", false)); // Missing skill (Red Node)

        // 5. Inject The Phantoms (The ATS Sandbox)
        IO.println("Injecting Phantom Candidates...");
        // user_id is NULL for ghosts! Job 1 (Vermeg).
        cvDAO.create(new CV(null, 1, false, true, "Amine (Ghost)", "Basic Java concepts.", 45));
        cvDAO.create(new CV(null, 1, false, true, "Nour (Ghost)", "Advanced Python and Cloud architecture.", 88));
        cvDAO.create(new CV(null, 1, false, true, "The Perfect Candidate", "Absolute master of Spring Boot and JVM.", 99));

        IO.println("\n✅ SEEDING COMPLETE! The Omni-Database is fully populated.");
        IO.println("Sarah can now design the UI with rich, live data.");

    } catch (Exception e) {
        IO.println("❌ Seeding failed: " + e.getMessage());
    }
}