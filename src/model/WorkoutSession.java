package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import observer.WorkoutObserver;

/**
 * Subject side of the Observer pattern. Represents one instance of
 * actually performing a Workout, and pushes live progress to any
 * registered WorkoutObserver (e.g. ActiveWorkoutFrame) as sets are
 * completed.
 */
public class WorkoutSession implements Serializable {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final long serialVersionUID = 1L;

    private final String id;
    private final Workout workout;
    private final LocalDateTime date;
    private final List<ExerciseSet> sets;

    private transient List<WorkoutObserver> observers;

    public WorkoutSession(Workout workout) {
        if (workout == null) {
            throw new IllegalArgumentException("workout must not be null");
        }
        this.id = UUID.randomUUID().toString();
        this.workout = workout;
        this.date = LocalDateTime.now();
        this.sets = new ArrayList<>();
        for (WorkoutExercise we : workout.getExercises()) {
            sets.add(new ExerciseSet(we));
        }
        this.observers = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Workout getWorkout() {
        return workout;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<ExerciseSet> getSets() {
        return Collections.unmodifiableList(sets);
    }

    public boolean isFinished() {
        for (ExerciseSet set : sets) {
            if (!set.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    public int getCompletedCount() {
        int count = 0;
        for (ExerciseSet set : sets) {
            if (set.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    public void markSetComplete(int index, int actualReps, Integer actualWeight) {
        ExerciseSet set = sets.get(index);
        set.complete(actualReps, actualWeight);
        notifySetCompleted(set);
        if (isFinished()) {
            notifySessionFinished();
        }
    }

    // ----- Observer pattern -----

    public void addObserver(WorkoutObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("observer must not be null");
        }
        getObservers().add(observer);
    }

    public void removeObserver(WorkoutObserver observer) {
        getObservers().remove(observer);
    }

    private List<WorkoutObserver> getObservers() {
        if (observers == null) {
            observers = new ArrayList<>();
        }
        return observers;
    }

    private void notifySetCompleted(ExerciseSet set) {
        for (WorkoutObserver observer : getObservers()) {
            observer.onSetCompleted(this, set);
        }
    }

    private void notifySessionFinished() {
        for (WorkoutObserver observer : getObservers()) {
            observer.onSessionFinished(this);
        }
    }

    @Override
    public String toString() {
        return workout.getName() + " - " + date.format(DISPLAY_FORMAT)
                + " (" + getCompletedCount() + "/" + sets.size() + ")";
    }
}
