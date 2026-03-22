package com.careerforge.dao;

import java.util.ArrayList;

/**
 * // FEDI: This interface forces all our DAOs to follow the same rules.
 */
public interface ICrud<T> {

    /*
     * FEYNMAN COMMENT: What is <T>? It stands for "Type" (Generics).
     * Imagine this interface is a blank mold for a sword.
     * When we implement this for the User table, T becomes 'User'.
     * When we implement it for the CV table, T becomes 'CV'.
     * This saves us from writing 5 different interfaces!
     */

    public void create(T entity);
    public ArrayList<T> readAll();
    public void update(T entity);
    public void delete(int id);
}