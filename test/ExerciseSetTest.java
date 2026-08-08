import model.Exercise;
import model.ExerciseSet;
import model.WorkoutExercise;

public class ExerciseSetTest {

    public static void main(String[] args) {
        TestSupport.Suite s = TestSupport.suite("ExerciseSetTest");

        s.run("constructor rejects null workoutExercise", () -> TestSupport.assertThrows(
                IllegalArgumentException.class, () -> new ExerciseSet(null),
                "should reject null workoutExercise"));

        s.run("new set starts incomplete with zero reps and no weight", () -> {
            WorkoutExercise we = new WorkoutExercise(new Exercise("Bench Press"), 3, 10);
            ExerciseSet set = new ExerciseSet(we);
            TestSupport.assertFalse(set.isCompleted(), "should start incomplete");
            TestSupport.assertEquals(0, set.getActualReps(), "initial actualReps");
            TestSupport.assertNull(set.getActualWeight(), "initial actualWeight");
            TestSupport.assertEquals(we, set.getWorkoutExercise(), "workoutExercise reference");
        });

        s.run("complete() records reps, weight, and flips completed flag", () -> {
            WorkoutExercise we = new WorkoutExercise(new Exercise("Bench Press"), 3, 10);
            ExerciseSet set = new ExerciseSet(we);
            set.complete(8, 135);
            TestSupport.assertTrue(set.isCompleted(), "should be completed");
            TestSupport.assertEquals(8, set.getActualReps(), "actualReps after complete");
            TestSupport.assertEquals(135, set.getActualWeight(), "actualWeight after complete");
        });

        s.run("complete() accepts null weight (bodyweight exercises)", () -> {
            WorkoutExercise we = new WorkoutExercise(new Exercise("Push Up"), 3, 15);
            ExerciseSet set = new ExerciseSet(we);
            set.complete(15, null);
            TestSupport.assertTrue(set.isCompleted(), "should be completed");
            TestSupport.assertNull(set.getActualWeight(), "actualWeight should remain null");
        });

        s.run("toString before completion shows target reps", () -> {
            WorkoutExercise we = new WorkoutExercise(new Exercise("Squat"), 3, 10);
            ExerciseSet set = new ExerciseSet(we);
            String text = set.toString();
            TestSupport.assertTrue(text.startsWith("[ ]"), "should show unchecked box");
            TestSupport.assertTrue(text.contains("target 10 reps"), "should show target reps");
        });

        s.run("toString after completion shows actual reps and weight", () -> {
            WorkoutExercise we = new WorkoutExercise(new Exercise("Squat"), 3, 10);
            ExerciseSet set = new ExerciseSet(we);
            set.complete(9, 225);
            String text = set.toString();
            TestSupport.assertTrue(text.startsWith("[x]"), "should show checked box");
            TestSupport.assertTrue(text.contains("9 reps") && text.contains("225 lb"), "should show actual reps/weight");
        });

        s.summary();
    }
}
