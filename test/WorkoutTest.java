import model.Exercise;
import model.Workout;
import model.WorkoutExercise;

public class WorkoutTest {

    public static void main(String[] args) {
        TestSupport.Suite s = TestSupport.suite("WorkoutTest");

        s.run("constructor rejects null/blank name", () -> {
            TestSupport.assertThrows(IllegalArgumentException.class, () -> new Workout(null), "null name");
            TestSupport.assertThrows(IllegalArgumentException.class, () -> new Workout("  "), "blank name");
        });

        s.run("new workout is active with no exercises", () -> {
            Workout w = new Workout("Push Day");
            TestSupport.assertTrue(w.isActive(), "should start active");
            TestSupport.assertTrue(w.getExercises().isEmpty(), "should start with no exercises");
        });

        s.run("addExercise(WorkoutExercise) rejects null", () -> {
            Workout w = new Workout("Push Day");
            TestSupport.assertThrows(IllegalArgumentException.class, () -> w.addExercise((WorkoutExercise) null),
                    "should reject null");
        });

        s.run("addExercise convenience overloads build a WorkoutExercise", () -> {
            Workout w = new Workout("Push Day");
            Exercise bench = new Exercise("Bench Press");
            Exercise pushup = new Exercise("Push Up");

            w.addExercise(bench, 3, 8, 135);
            w.addExercise(pushup, 3, 15);

            TestSupport.assertEquals(2, w.getExercises().size(), "should have two exercises");
            TestSupport.assertEquals(bench, w.getExercises().get(0).getExercise(), "first exercise ref");
            TestSupport.assertEquals(135, w.getExercises().get(0).getTargetWeight(), "first exercise weight");
            TestSupport.assertNull(w.getExercises().get(1).getTargetWeight(), "second exercise weight");
        });

        s.run("getExercises returns an unmodifiable view", () -> {
            Workout w = new Workout("Push Day");
            w.addExercise(new Exercise("Bench Press"), 3, 8);
            TestSupport.assertThrows(UnsupportedOperationException.class,
                    () -> w.getExercises().add(null), "list should be unmodifiable");
        });

        s.run("removeExercise removes the given WorkoutExercise", () -> {
            Workout w = new Workout("Push Day");
            WorkoutExercise we = new WorkoutExercise(new Exercise("Bench Press"), 3, 8);
            w.addExercise(we);
            w.removeExercise(we);
            TestSupport.assertTrue(w.getExercises().isEmpty(), "should be removed");
        });

        s.run("removeExerciseFor removes the matching entry by underlying Exercise", () -> {
            Workout w = new Workout("Push Day");
            Exercise bench = new Exercise("Bench Press");
            Exercise fly = new Exercise("Chest Fly");
            w.addExercise(bench, 3, 8);
            w.addExercise(fly, 3, 12);

            w.removeExerciseFor(bench);

            TestSupport.assertEquals(1, w.getExercises().size(), "one exercise should remain");
            TestSupport.assertEquals(fly, w.getExercises().get(0).getExercise(), "remaining exercise");
        });

        s.run("removeExerciseFor is a no-op when exercise is not present", () -> {
            Workout w = new Workout("Push Day");
            w.addExercise(new Exercise("Bench Press"), 3, 8);
            w.removeExerciseFor(new Exercise("Deadlift"));
            TestSupport.assertEquals(1, w.getExercises().size(), "nothing should be removed");
        });

        s.run("archive/reactivate toggle isActive", () -> {
            Workout w = new Workout("Push Day");
            w.archive();
            TestSupport.assertFalse(w.isActive(), "should be archived");
            w.reactivate();
            TestSupport.assertTrue(w.isActive(), "should be reactivated");
        });

        s.run("setName rejects blank", () -> {
            Workout w = new Workout("Push Day");
            TestSupport.assertThrows(IllegalArgumentException.class, () -> w.setName(""), "blank name");
        });

        s.summary();
    }
}
