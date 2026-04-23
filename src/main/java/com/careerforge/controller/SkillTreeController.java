package com.careerforge.controller;

import com.careerforge.dao.SkillDAO;
import com.careerforge.model.Skill;
import com.careerforge.util.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SkillTreeController {

    @FXML private Pane skillCanvas;
    private SkillDAO skillDAO = new SkillDAO();

    @FXML
    public void initialize() {
        IO.println("🌳 SKILL TREE: Hydrating RPG Node Map from Database...");
        Platform.runLater(this::drawTree);
    }

    private void drawTree() {
        if (skillCanvas == null) return;
        skillCanvas.getChildren().clear();

        int userId = (UserSession.getInstance().isLoggedIn()) ? UserSession.getInstance().getActiveUser().getId() : 1;

        CompletableFuture.runAsync(() -> {
            try {
                ArrayList<Skill> rawSkills = skillDAO.getSkillsByUserId(userId);

                // FEDI-STANDARD DATA CLEANUP: Remove duplicate skills so the tree isn't chaotic
                ArrayList<Skill> uniqueSkills = new ArrayList<>();
                Set<String> seenNames = new HashSet<>();

                for (Skill s : rawSkills) {
                    String cleanName = s.getSkillName().trim().toLowerCase();
                    if (!seenNames.contains(cleanName)) {
                        seenNames.add(cleanName);
                        uniqueSkills.add(s);
                    }
                }

                Platform.runLater(() -> {
                    // Shifted center slightly right to balance the sidebar
                    double centerX = 450.0;
                    double centerY = 250.0;

                    double nodeRadius = 160.0; // Distance of the physical nodes
                    double textRadius = 190.0; // Distance of the text labels

                    double angleStep = 360.0 / (uniqueSkills.isEmpty() ? 1 : uniqueSkills.size());

                    // 1. Draw all connections and nodes FIRST
                    for (int i = 0; i < uniqueSkills.size(); i++) {
                        Skill skill = uniqueSkills.get(i);
                        double angle = Math.toRadians(i * angleStep);

                        double nodeX = centerX + nodeRadius * Math.cos(angle);
                        double nodeY = centerY + nodeRadius * Math.sin(angle);

                        // Draw connection line
                        Line connection = new Line(centerX, centerY, nodeX, nodeY);
                        connection.setStyle("-fx-stroke: #30363d; -fx-stroke-width: 2;");
                        skillCanvas.getChildren().add(connection);

                        // Draw Node
                        String color = skill.isAcquired() ? "#58a6ff" : "#ef4444";
                        Circle node = new Circle(nodeX, nodeY, 15);
                        node.setStyle("-fx-fill: " + color + "; -fx-effect: dropshadow(gaussian, " + color + ", 15, 0.5, 0, 0);");

                        node.setOnMouseEntered(e -> node.setRadius(20));
                        node.setOnMouseExited(e -> node.setRadius(15));
                        skillCanvas.getChildren().add(node);

                        // 2. QUADRANT ALIGNMENT: Draw Text cleanly based on circle position
                        double textX = centerX + textRadius * Math.cos(angle);
                        double textY = centerY + textRadius * Math.sin(angle);

                        Text label = new Text(skill.getSkillName());
                        label.setStyle("-fx-fill: #e6edf3; -fx-font-weight: bold; -fx-font-size: 12px;");

                        // If the node is on the left side of the circle, push the text to the left!
                        if (Math.cos(angle) < 0) {
                            label.setX(textX - label.getLayoutBounds().getWidth());
                        } else {
                            label.setX(textX);
                        }

                        // Center text vertically
                        label.setY(textY + (label.getLayoutBounds().getHeight() / 4));

                        skillCanvas.getChildren().add(label);
                    }

                    // 3. Draw Center Node LAST so it sits on top of all lines!
                    drawCenterNode(centerX, centerY, "ALPHA CANDIDATE", "#00f2fe");

                    IO.println("✅ SKILL TREE RENDERED: Math stabilized. Duplicates purged.");
                });
            } catch (Exception e) {
                IO.println("❌ Failed to render Skill Tree: " + e.getMessage());
            }
        });
    }

    @FXML
    public void handleLevelUp() {
        IO.println("🌳 SKILL TREE: Triggering Gemini Growth Plan...");
        drawCenterNode(450, 100, "NEW POTENTIAL", "#00ff00");
    }

    private void drawCenterNode(double x, double y, String name, String color) {
        Circle c = new Circle(x, y, 30); // Made the center node bigger
        c.setStyle("-fx-fill: " + color + "; -fx-effect: dropshadow(gaussian, " + color + ", 30, 0.8, 0, 0);");

        Text t = new Text(name);
        // Center the text perfectly inside the glowing node
        t.setX(x - (t.getLayoutBounds().getWidth() / 2));
        t.setY(y + 5);
        t.setStyle("-fx-fill: #050510; -fx-font-weight: bold; -fx-font-size: 12px;"); // Dark text for contrast

        skillCanvas.getChildren().addAll(c, t);
    }
}