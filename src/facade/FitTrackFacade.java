package facade;

import java.util.List;

import data.DataStore;
import model.Exercise;
import model.Profile;
import model.Workout;
import model.WorkoutExercise;
import strategy.SearchStrategy;

public class FitTrackFacade {

    private final DataStore store = DataStore.getInstance();

    // ===================== PROFILES (multi) =====================

    public Profile createProfile(String name, double heightCm, double weightKg) {
        Profile profile = new Profile(name, heightCm, weightKg);
        store.addProfile(profile);
        store.setCurrentProfile(profile); // new one becomes current
        return profile;
    }

    public List<Profile> getAllProfiles() {
        return store.getProfiles();
    }

    public Profile getCurrentProfile() {
        return store.getCurrentProfile();
    }

    /** Convenience alias used by most windows */
    public Profile getProfile() {
        return getCurrentProfile();
    }

    public void switchProfile(Profile profile) {
        if (profile == null)
            throw new IllegalArgumentException("profile must not be null");
        store.setCurrentProfile(profile);
    }

    public void updateProfile(String name, double heightCm, double weightKg) {
        Profile p = requireProfile();
        p.setName(name);
        p.setHeightCm(heightCm);
        p.setCurrentWeightKg(weightKg);
    }

    public void deleteProfile(Profile profile) {
        store.removeProfile(profile);
    }

    // ===================== EXERCISES (current profile) =====================

    public Exercise createExercise(String name, String category) {
        Profile p = requireProfile();
        Exercise e = new Exercise(name, category);
        p.addExercise(e);
        return e;
    }

    public void updateExercise(Exercise exercise, String newName, String newCategory) {
        requireProfile();
        exercise.setName(newName);
        exercise.setCategory(newCategory);
    }

    public void deleteExercise(Exercise exercise) {
        Profile p = requireProfile();
        for (Workout w : p.getWorkouts()) {
            w.removeExerciseFor(exercise);
        }
        p.removeExercise(exercise);
    }

    public List<Exercise> getAllExercises() {
        return requireProfile().getExercises();
    }

    // ===================== WORKOUTS (current profile) =====================

    public Workout createWorkout(String name) {
        Profile p = requireProfile();
        Workout w = new Workout(name);
        p.addWorkout(w);
        return w;
    }

    public void updateWorkoutName(Workout workout, String newName) {
        requireProfile();
        workout.setName(newName);
    }

    /** Soft-delete – history stays valid */
    public void deleteWorkout(Workout workout) {
        requireProfile();
        workout.archive();
    }

    public void reactivateWorkout(Workout workout) {
        requireProfile();
        workout.reactivate();
    }

    public void addExerciseToWorkout(Workout workout, Exercise exercise,
            int sets, int reps) {
        requireProfile();
        workout.addExercise(exercise, sets, reps);
    }

    public void addExerciseToWorkout(Workout workout, Exercise exercise,
            int sets, int reps, Integer weight) {
        requireProfile();
        workout.addExercise(exercise, sets, reps, weight);
    }

    public void removeExerciseFromWorkout(Workout workout, WorkoutExercise we) {
        requireProfile();
        workout.removeExercise(we);
    }

    public List<Workout> getActiveWorkouts() {
        return requireProfile().getActiveWorkouts();
    }

    public List<Workout> getAllWorkouts() {
        return requireProfile().getWorkouts();
    }

    // ===================== TRACKING =====================

    /**
     * Creates a WorkoutSession (the Subject).
     * The view must register itself as a WorkoutObserver afterwards.
     */
    public WorkoutSession startWorkout(Workout workout) {
        requireProfile();
        if (!workout.isActive()) {
            throw new IllegalStateException("Cannot start an archived workout");
        }
        if (workout.getExercises().isEmpty()) {
            throw new IllegalArgumentException("Workout must contain at least one exercise");
        }
        return new WorkoutSession(workout); // Subject is born here
    }

    public void finishAndSaveSession(WorkoutSession session) {
        requireProfile().addToHistory(session);
    }

    // ===================== HISTORY =====================

    public List<WorkoutSession> getHistory() {
        return requireProfile().getHistory();
    }

    public List<WorkoutSession> searchHistory(SearchStrategy strategy, String query) {
        return strategy.search(requireProfile().getHistory(), query);
    }

    // ===================== helper =====================

    private Profile requireProfile() {
        Profile p = store.getCurrentProfile();
        if (p == null) {
            throw new IllegalStateException("No profile selected. Create or switch to a profile first.");
        }
        return p;
    }
}