import java.io.Serializable;

public class WorkoutExercise implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Exercise exercise;
    private int targetSets;
    private int targetReps;
    private Integer targetWeight;

    public WorkoutExercise(Exercise exercise, int targetSets, int targetReps) {
        this(exercise, targetSets, targetReps, null);
    }

    public WorkoutExercise(Exercise exercise, int targetSets, int targetReps, Integer targetWeight) {
        if (exercise == null) {
            throw new IllegalArgumentException("exercise must not be null");
        }
        validateSets(targetSets);
        validateReps(targetReps);
        validateWeight(targetWeight);
        this.exercise = exercise;
        this.targetSets = targetSets;
        this.targetReps = targetReps;
        this.targetWeight = targetWeight;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public int getTargetSets() {
        return targetSets;
    }

    public int getTargetReps() {
        return targetReps;
    }

    public Integer getTargetWeight() {
        return targetWeight;
    }

    public void setTargetSets(int targetSets) {
        validateSets(targetSets);
        this.targetSets = targetSets;
    }

    public void setTargetReps(int targetReps) {
        validateReps(targetReps);
        this.targetReps = targetReps;
    }

    public void setTargetWeight(Integer targetWeight) {
        validateWeight(targetWeight);
        this.targetWeight = targetWeight;
    }

    public String getDisplayText() {
        StringBuilder text = new StringBuilder();
        text.append(exercise.getName())
                .append(": ")
                .append(targetSets)
                .append(" sets x ")
                .append(targetReps)
                .append(" reps");
        if (targetWeight != null) {
            text.append(" @ ").append(targetWeight).append(" lb");
        }
        return text.toString();
    }

    private static void validateSets(int targetSets) {
        if (targetSets <= 0) {
            throw new IllegalArgumentException("targetSets must be positive");
        }
    }

    private static void validateReps(int targetReps) {
        if (targetReps <= 0) {
            throw new IllegalArgumentException("targetReps must be positive");
        }
    }

    private static void validateWeight(Integer targetWeight) {
        if (targetWeight != null && targetWeight < 0) {
            throw new IllegalArgumentException("targetWeight must not be negative");
        }
    }

    @Override
    public String toString() {
        return "WorkoutExercise{" + getDisplayText() + "}";
    }
}
