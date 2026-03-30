package com.careerforge.core;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * // FEDI: THE SOLANUM CORE (The Heart of the Omni-Chimera)
 * // A 6-Dimensional Framework handling Memory, Time, Events, and Offline Survival.
 */
public class SolanumCore {

    private static SolanumCore instance;

    // 1. THE SYNAPTIC EVENT BUS (The Nervous System)
    // Allows screens to talk to each other blindly without knowing the other exists.
    private final Map<String, List<Consumer<Object>>> eventListeners = new ConcurrentHashMap<>();

    // 2. THE ASH TWIN PROJECT (The Time Loop / Ctrl+Z)
    // Stores the exact reverse-actions of whatever the user just did.
    private final Stack<Runnable> ashTwinMemory = new Stack<>();

    // 3. THE QUANTUM MOON (Observation-Based RAM)
    // Stores UI screens only when observed.
    private final Map<String, Parent> quantumMoonCache = new HashMap<>();

    // 4. THE NOMAI MASK (Offline Circuit Breaker)
    // If the database dies, tasks are trapped here until the universe restarts.
    private final Queue<Runnable> nomaiMask = new LinkedList<>();
    private boolean isUniverseOnline = true; // Simulates Database/Internet connection

    private SolanumCore() {
        IO.println("🌌 SOLANUM CORE: The Eye of the Universe is open.");
    }

    public static SolanumCore getInstance() {
        if (instance == null) {
            instance = new SolanumCore();
        }
        return instance;
    }

    // =========================================================================================
    // 🧬 FEATURE 1: THE SYNAPTIC EVENT BUS
    // =========================================================================================

    /*
     * FEYNMAN COMMENT: Imagine a radio station.
     * Any screen can 'listen' to a specific frequency (eventName).
     * When the War Room 'broadcasts' a new job, every screen listening instantly updates!
     */
    public void listen(String eventName, Consumer<Object> action) {
        eventListeners.computeIfAbsent(eventName, k -> new ArrayList<>()).add(action);
        IO.println("📡 SYNAPSE: A new observer is listening to [" + eventName + "]");
    }

    public void broadcast(String eventName, Object payload) {
        IO.println("📢 BROADCAST: Firing event [" + eventName + "] across the Core.");
        List<Consumer<Object>> listeners = eventListeners.getOrDefault(eventName, new ArrayList<>());
        for (Consumer<Object> listener : listeners) {
            // Force UI updates to run safely on the JavaFX Thread
            Platform.runLater(() -> listener.accept(payload));
        }
    }

    // =========================================================================================
    // 🌑 FEATURE 2: THE ASH TWIN PROJECT (TIME REVERSAL)
    // =========================================================================================

    /*
     * FEYNMAN COMMENT: Every time Fedi drops a card, he sends a "Memory" to the Ash Twin.
     * The memory contains exactly how to UNDO what he just did.
     */
    public void sendToAshTwin(Runnable undoAction) {
        ashTwinMemory.push(undoAction);
        IO.println("⏳ ASH TWIN: Action recorded. Time loop prepared.");
    }

    public void rewindTime() {
        if (!ashTwinMemory.isEmpty()) {
            IO.println("⏪ TIME REVERSAL INITIATED: Rewinding last action...");
            Runnable pastAction = ashTwinMemory.pop();
            pastAction.run(); // Executes the exact reverse of the last action!
        } else {
            IO.println("⚠️ ASH TWIN EMPTY: You are at the beginning of the timeline.");
        }
    }

    // =========================================================================================
    // 🪐 FEATURE 3: THE QUANTUM MOON (LAZY LOADING)
    // =========================================================================================

    /*
     * FEYNMAN COMMENT: The UI screen does not exist until Sarah clicks the button.
     * When she looks away, it stays in the Cache. If RAM gets full, we can wipe the Cache!
     */
    public Parent observe(String fxmlPath) {
        if (quantumMoonCache.containsKey(fxmlPath)) {
            IO.println("🪐 QUANTUM MOON: " + fxmlPath + " materialized from Cache (Zero RAM cost).");
            return quantumMoonCache.get(fxmlPath);
        }

        try {
            IO.println("🔭 OBSERVATION: " + fxmlPath + " is being rendered for the first time...");
            Parent root = FXMLLoader.load(getClass().getResource("/view/" + fxmlPath));
            quantumMoonCache.put(fxmlPath, root);
            return root;
        } catch (Exception e) {
            IO.println("❌ MACROSCOPIC FAILURE: Could not observe " + fxmlPath + ". " + e.getMessage());
            return null;
        }
    }

    public void collapseQuantumMoon() {
        // Frees up 500MB of RAM instantly by destroying the UI cache.
        quantumMoonCache.clear();
        IO.println("💥 COLLAPSE: The Quantum Moon has vanished. Memory freed.");
    }

    // =========================================================================================
    // 🎭 FEATURE 4: THE NOMAI MASK (CIRCUIT BREAKER)
    // =========================================================================================

    public void setUniverseOnline(boolean status) {
        this.isUniverseOnline = status;
        if (status) {
            IO.println("🌐 UNIVERSE ONLINE: Connection restored. Flushing the Nomai Mask...");
            flushNomaiMask();
        } else {
            IO.println("🔴 UNIVERSE OFFLINE: Database connection lost. Mask activated.");
        }
    }

    /*
     * FEYNMAN COMMENT: If the Database dies, the app doesn't crash.
     * It wraps the SQL query in a box (Runnable) and stores it in the Mask.
     * When the DB wakes up, it automatically runs the box!
     */
    public void executeOrMask(Runnable dangerousTask) {
        if (isUniverseOnline) {
            try {
                dangerousTask.run();
            } catch (Exception e) {
                IO.println("⚠️ RUNTIME CRASH AVERTED: Task failed. Sending to Nomai Mask. " + e.getMessage());
                nomaiMask.add(dangerousTask);
            }
        } else {
            IO.println("🎭 NOMAI MASK: Universe offline. Task cached for later.");
            nomaiMask.add(dangerousTask);
        }
    }

    private void flushNomaiMask() {
        while (!nomaiMask.isEmpty()) {
            Runnable savedTask = nomaiMask.poll();
            IO.println("⚡ MASK FLUSH: Executing delayed task...");
            savedTask.run();
        }
    }
}