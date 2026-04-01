package com.careerforge.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.application.Platform;

public class SkillTreeController {

    @FXML private Pane skillCanvas;

    @FXML
    public void initialize() {
        Platform.runLater(this::drawTree);
    }

    private void drawTree() {
        if (skillCanvas == null) return;
        skillCanvas.getChildren().clear();
        drawNode(400, 250, "ALPHA CANDIDATE", "#58a6ff");
        drawNode(200, 150, "JAVA MASTER", "#58a6ff");
        drawNode(600, 150, "AWS CLOUD", "#ef4444");
        connect(400, 250, 200, 150);
        connect(400, 250, 600, 150);
    }

    // THE MISSING LINK: This is the method the FXML was screaming for!
    @FXML
    public void handleLevelUp() {
        System.out.println("🌳 SKILL TREE: Analyzing your career path...");
        // Visual proof it works: add a temporary green node!
        drawNode(400, 100, "NEW POTENTIAL", "#00ff00");
    }

    private void drawNode(double x, double y, String name, String color) {
        Circle c = new Circle(x, y, 20);
        c.setStyle("-fx-fill: " + color + "; -fx-effect: dropshadow(gaussian, " + color + ", 15, 0.5, 0, 0);");
        Text t = new Text(x - 30, y + 40, name);
        t.setStyle("-fx-fill: white; -fx-font-weight: bold;");
        skillCanvas.getChildren().addAll(c, t);
    }

    private void connect(double x1, double y1, double x2, double y2) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStyle("-fx-stroke: #30363d; -fx-stroke-width: 2;");
        skillCanvas.getChildren().add(0, l);
    }
}