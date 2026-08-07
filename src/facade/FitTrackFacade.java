package facade;

import java.util.List;

import data.DataStore;
import data.SerializedObjectStore;
import model.Exercise;
import model.Profile;
import model.Workout;
import model.WorkoutExercise;
import model.WorkoutSession;
import report.ExerciseTrendPoint;
import report.ProgressAnalyzer;
import report.ProgressSummary;
import strategy.SearchStrategy;

public class FitTrackFacade {

    private final DataStore store = DataStore.getInstance();
    private final SerializedObjectStore objectStore = new SerializedObjectStore();
    private final ProgressAnalyzer progressAnalyzer = new ProgressAnalyzer();

    public FitTrackFacade() {
        if (store.getProfiles().isEmpty()) {
            for (Profile profile : objectStore.load()) {
                store.addProfile(profile);
            }
        }
    }

    // ===================== PROFILES (multi) =====================

    public Profile createProfile(String name, double heightCm, double weightKg) {
        Profile profile = new Profile(name, heightCm, weightKg);
        store.addProfile(profile);
        store.setCurrentProfile(profile); // new one becomes current
        persist();
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
        persist();
    }

    public void deleteProfile(Profile profile) {
        store.removeProfile(profile);
        persist();
    }

    // ===================== EXERCISES (current profile) =====================

    public Exercise createExercise(String name, String category) {
        Profile p = requireProfile();
        Exercise e = new Exercise(name, category);
        p.addExercise(e);
        persist();
        return e;
    }

    public void updateExercise(Exercise exercise, String newName, String newCategory) {
        requireProfile();
        exercise.setName(newName);
        exercise.setCategory(newCategory);
        persist();
    }

    /**
     * Cascades into every workout that references the exercise, not just the
     * library list.
     */
    public void deleteExercise(Exercise exercise) {
        Profile p = requireProfile();
        for (Workout w : p.getWorkouts()) {
            w.removeExerciseFor(exercise);
        }
        p.removeExercise(exercise);
        persist();
    }

    public List<Exercise> getAllExercises() {
        return requireProfile().getExercises();
    }

    // ===================== WORKOUTS (current profile) =====================

    public Workout createWorkout(String name) {
        Profile p = requireProfile();
        Workout w = new Workout(name);
        p.addWorkout(w);
        persist();
        return w;
    }

    public void updateWorkoutName(Workout workout, String newName) {
        requireProfile();
        workout.setName(newName);
        persist();
    }

    /** Soft-delete – history stays valid */
    public void deleteWorkout(Workout workout) {
        requireProfile();
        workout.archive();
        persist();
    }

    public void reactivateWorkout(Workout workout) {
        requireProfile();
        workout.reactivate();
        persist();
    }

    public void addExerciseToWorkout(Workout workout, Exercise exercise,
            int sets, int reps) {
        requireProfile();
        workout.addExercise(exercise, sets, reps);
        persist();
    }

    public void addExerciseToWorkout(Workout workout, Exercise exercise,
            int sets, int reps, Integer weight) {
        requireProfile();
        workout.addExercise(exercise, sets, reps, weight);
        persist();
    }

    public void removeExerciseFromWorkout(Workout workout, WorkoutExercise we) {
        requireProfile();
        workout.removeExercise(we);
        persist();
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
        persist();
    }

    // ===================== HISTORY =====================

    public List<WorkoutSession> getHistory() {
        return requireProfile().getHistory();
    }

    public List<WorkoutSession> searchHistory(SearchStrategy strategy, String query) {
        return strategy.search(requireProfile().getHistory(), query);
    }

    // ===================== PROGRESS =====================

    public ProgressSummary getProgressSummary() {
        return progressAnalyzer.summarize(requireProfile().getHistory());
    }

    public List<ExerciseTrendPoint> getExerciseTrend(Exercise exercise) {
        return progressAnalyzer.trendFor(requireProfile().getHistory(), exercise);
    }

    // ===================== helper =====================

    private Profile requireProfile() {
        Profile p = store.getCurrentProfile();
        if (p == null) {
            throw new IllegalStateException("No profile selected. Create or switch to a profile first.");
        }
        return p;
    }

    private void persist() {
        objectStore.save(store.getProfiles());
    }
}