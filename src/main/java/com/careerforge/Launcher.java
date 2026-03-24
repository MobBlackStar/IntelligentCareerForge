package com.careerforge;

/**
 * // SARAH: This is the "Bypass Key".
 * // Because this class is 'Normal' and doesn't extend Application,
 * // it bypasses the "Missing Components" error.
 */
public class Launcher {
    public static void main(String[] args) {
        // We call the main method of our UI Bridge!
        MainApp.main(args);
    }
}