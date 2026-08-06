package model;

import java.io.Serializable;

/**
 * One actual logged set performed during a WorkoutSession,
 * tied back to the WorkoutExercise it was targeting.
 */
public class ExerciseSet implements Serializable {

    private static final long serialVersionUID = 1L;

    private final WorkoutExercise workoutExercise;
    private boolean completed;
    private int actualReps;
    private Integer actualWeight;

    public ExerciseSet(WorkoutExercise workoutExercise) {
        if (workoutExercise == null) {
            throw new IllegalArgumentException("workoutExercise must not be null");
        }
        this.workoutExercise = workoutExercise;
        this.completed = false;
        this.actualReps = 0;
        this.actualWeight = null;
    }

    public WorkoutExercise getWorkoutExercise() {
        return workoutExercise;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getActualReps() {
        return actualReps;
    }

    public Integer getActualWeight() {
        return actualWeight;
    }

    public void complete(int actualReps, Integer actualWeight) {
        this.actualReps = actualReps;
        this.actualWeight = actualWeight;
        this.completed = true;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(completed ? "[x] " : "[ ] ").append(workoutExercise.getExercise().getName());
        if (completed) {
            text.append(" - ").append(actualReps).append(" reps");
            if (actualWeight != null) {
                text.append(" @ ").append(actualWeight).append(" lb");
            }
        } else {
            text.append(" - target ").append(workoutExercise.getTargetReps()).append(" reps");
        }
        return text.toString();
    }
}
