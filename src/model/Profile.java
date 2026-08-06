package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Profile implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;
    private double heightCm;
    private double currentWeightKg;

    private final List<Exercise> exercises;
    private final List<Workout> workouts;
    private final List<WorkoutSession> history;

    public Profile(String name, double heightCm, double weightKg) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.heightCm = heightCm;
        this.currentWeightKg = weightKg;
        this.exercises = new ArrayList<>();
        this.workouts = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        this.name = name;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public double getCurrentWeightKg() {
        return currentWeightKg;
    }

    public void setCurrentWeightKg(double currentWeightKg) {
        this.currentWeightKg = currentWeightKg;
    }

    // ----- exercises -----

    public List<Exercise> getExercises() {
        return Collections.unmodifiableList(exercises);
    }

    public void addExercise(Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("exercise must not be null");
        }
        exercises.add(exercise);
    }

    public void removeExercise(Exercise exercise) {
        exercises.remove(exercise);
    }

    // ----- workouts -----

    public List<Workout> getWorkouts() {
        return Collections.unmodifiableList(workouts);
    }

    public void addWorkout(Workout workout) {
        if (workout == null) {
            throw new IllegalArgumentException("workout must not be null");
        }
        workouts.add(workout);
    }

    public List<Workout> getActiveWorkouts() {
        List<Workout> active = new ArrayList<>();
        for (Workout w : workouts) {
            if (w.isActive()) {
                active.add(w);
            }
        }
        return active;
    }

    // ----- history -----

    public List<WorkoutSession> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void addToHistory(WorkoutSession session) {
        if (session == null) {
            throw new IllegalArgumentException("session must not be null");
        }
        history.add(session);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Profile)) {
            return false;
        }
        return id.equals(((Profile) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
