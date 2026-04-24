package com.careerforge.core;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene; // THE MISSING SYMBOL 1
import javafx.scene.layout.StackPane;
import javafx.stage.Stage; // THE MISSING SYMBOL 2
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javafx.stage.Window;

/**
 * // FEDI: THE SOLANUM CORE (Heart of the Omni-Chimera)
 * // Sarah: I have added the "Gateway Logic" to handle Login/Register navigation.
 */
public class SolanumCore {

    private static SolanumCore instance;
    private StackPane mainContent;

    private final Map<String, List<Consumer<Object>>> eventListeners = new ConcurrentHashMap<>();
    private final Map<String, Parent> quantumMoonCache = new HashMap<>();

    private SolanumCore() {
        System.out.println("🌌 SOLANUM CORE: The Eye of the Universe is open.");
    }

    public static SolanumCore getInstance() {
        if (instance == null) {
            instance = new SolanumCore();
        }
        return instance;
    }

    public void setMainContent(StackPane pane) {
        this.mainContent = pane;
    }

    /**
     * THE QUANTUM MOON (Updated for Root Transition)
     */
    public Parent observe(String fxmlFile) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/view/" + fxmlFile));

            // Use Platform.runLater to ensure we are on the UI Thread
            Platform.runLater(() -> {
                if (mainContent != null) {
                    mainContent.getChildren().clear();
                    mainContent.getChildren().add(view);
                } else {
                    // SEAMLESS TRANSITION: Safely find the primary stage
                    javafx.stage.Window window = javafx.stage.Window.getWindows().get(0);
                    if (window instanceof javafx.stage.Stage stage) {
                        Scene newScene = new Scene(view);
                        // Re-inject the global CSS
                        String css = getClass().getResource("/css/style.css").toExternalForm();
                        newScene.getStylesheets().add(css);
                        stage.setScene(newScene);
                    }
                }
            });
            return view;
        } catch (IOException e) {
            System.out.println("❌ CORE CRASH: " + fxmlFile);
            return null;
        }
    }

    // --- Fedi's Event System ---
    public void listen(String event, Consumer<Object> action) { eventListeners.computeIfAbsent(event, k -> new ArrayList<>()).add(action); }
    public void broadcast(String event, Object payload) {
        List<Consumer<Object>> listeners = eventListeners.getOrDefault(event, new ArrayList<>());
        for (Consumer<Object> listener : listeners) Platform.runLater(() -> listener.accept(payload));
    }
}