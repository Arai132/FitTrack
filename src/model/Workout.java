import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Workout implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;
    private final List<WorkoutExercise> exercises;
    private boolean isActive;

    public Workout(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.exercises = new ArrayList<>();
        this.isActive = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<WorkoutExercise> getExercises() {
        return Collections.unmodifiableList(exercises);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        this.name = name;
    }

    public void addExercise(WorkoutExercise workoutExercise) {
        if (workoutExercise == null) {
            throw new IllegalArgumentException("workoutExercise must not be null");
        }
        exercises.add(workoutExercise);
    }

    public void addExercise(Exercise exercise, int targetSets, int targetReps) {
        addExercise(new WorkoutExercise(exercise, targetSets, targetReps));
    }

    public void addExercise(Exercise exercise, int targetSets, int targetReps, Integer targetWeight) {
        addExercise(new WorkoutExercise(exercise, targetSets, targetReps, targetWeight));
    }

    public void removeExercise(WorkoutExercise workoutExercise) {
        exercises.remove(workoutExercise);
    }

    public void removeExerciseFor(Exercise exercise) {
        for (WorkoutExercise workoutExercise : exercises) {
            if (workoutExercise.getExercise().equals(exercise)) {
                exercises.remove(workoutExercise);
                return;
            }
        }
    }

    public void archive() {
        isActive = false;
    }

    public void reactivate() {
        isActive = true;
    }

    @Override
    public String toString() {
        return "Workout{id='" + id + "', name='" + name + "', exercises=" + exercises.size()
                + ", isActive=" + isActive + "}";
    }
}
