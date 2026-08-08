import model.Exercise;
import model.Profile;
import model.Workout;
import model.WorkoutSession;

public class ProfileTest {

    public static void main(String[] args) {
        TestSupport.Suite s = TestSupport.suite("ProfileTest");

        s.run("constructor rejects null/blank name", () -> {
            TestSupport.assertThrows(IllegalArgumentException.class, () -> new Profile(null, 180, 80), "null name");
            TestSupport.assertThrows(IllegalArgumentException.class, () -> new Profile("  ", 180, 80), "blank name");
        });

        s.run("constructor sets fields and starts with empty collections", () -> {
            Profile p = new Profile("Alex", 180.0, 75.5);
            TestSupport.assertEquals("Alex", p.getName(), "name");
            TestSupport.assertEquals(180.0, p.getHeightCm(), "heightCm");
            TestSupport.assertEquals(75.5, p.getCurrentWeightKg(), "currentWeightKg");
            TestSupport.assertTrue(p.getExercises().isEmpty(), "exercises should start empty");
            TestSupport.assertTrue(p.getWorkouts().isEmpty(), "workouts should start empty");
            TestSupport.assertTrue(p.getHistory().isEmpty(), "history should start empty");
        });

        s.run("setName rejects blank", () -> {
            Profile p = new Profile("Alex", 180, 75);
            TestSupport.assertThrows(IllegalArgumentException.class, () -> p.setName(""), "blank name");
        });

        s.run("setHeightCm and setCurrentWeightKg update state", () -> {
            Profile p = new Profile("Alex", 180, 75);
            p.setHeightCm(182.5);
            p.setCurrentWeightKg(74.0);
            TestSupport.assertEquals(182.5, p.getHeightCm(), "heightCm after set");
            TestSupport.assertEquals(74.0, p.getCurrentWeightKg(), "currentWeightKg after set");
        });

        s.run("addExercise rejects null and appends otherwise", () -> {
            Profile p = new Profile("Alex", 180, 75);
            TestSupport.assertThrows(IllegalArgumentException.class, () -> p.addExercise(null), "null exercise");
            Exercise e = new Exercise("Squat");
            p.addExercise(e);
            TestSupport.assertEquals(1, p.getExercises().size(), "exercise count");
            TestSupport.assertTrue(p.getExercises().contains(e), "should contain added exercise");
        });

        s.run("removeExercise removes a previously added exercise", () -> {
            Profile p = new Profile("Alex", 180, 75);
            Exercise e = new Exercise("Squat");
            p.addExercise(e);
            p.removeExercise(e);
            TestSupport.assertTrue(p.getExercises().isEmpty(), "should be removed");
        });

        s.run("addWorkout rejects null and appends otherwise", () -> {
            Profile p = new Profile("Alex", 180, 75);
            TestSupport.assertThrows(IllegalArgumentException.class, () -> p.addWorkout(null), "null workout");
            Workout w = new Workout("Push Day");
            p.addWorkout(w);
            TestSupport.assertEquals(1, p.getWorkouts().size(), "workout count");
        });

        s.run("getActiveWorkouts filters out archived workouts", () -> {
            Profile p = new Profile("Alex", 180, 75);
            Workout active = new Workout("Push Day");
            Workout archived = new Workout("Old Routine");
            archived.archive();
            p.addWorkout(active);
            p.addWorkout(archived);

            TestSupport.assertEquals(1, p.getActiveWorkouts().size(), "only one workout should be active");
            TestSupport.assertTrue(p.getActiveWorkouts().contains(active), "active workout should be included");
        });

        s.run("addToHistory rejects null and appends otherwise", () -> {
            Profile p = new Profile("Alex", 180, 75);
            TestSupport.assertThrows(IllegalArgumentException.class, () -> p.addToHistory(null), "null session");
            Workout w = new Workout("Push Day");
            w.addExercise(new Exercise("Bench Press"), 3, 8);
            WorkoutSession session = new WorkoutSession(w);
            p.addToHistory(session);
            TestSupport.assertEquals(1, p.getHistory().size(), "history count");
        });

        s.run("getExercises/getWorkouts/getHistory return unmodifiable views", () -> {
            Profile p = new Profile("Alex", 180, 75);
            TestSupport.assertThrows(UnsupportedOperationException.class,
                    () -> p.getExercises().add(new Exercise("Squat")), "exercises list should be unmodifiable");
            TestSupport.assertThrows(UnsupportedOperationException.class,
                    () -> p.getWorkouts().add(new Workout("Leg Day")), "workouts list should be unmodifiable");
        });

        s.run("equals/hashCode are identity-based via id, not name", () -> {
            Profile a = new Profile("Alex", 180, 75);
            Profile b = new Profile("Alex", 180, 75);
            TestSupport.assertFalse(a.equals(b), "two profiles with same name but different id should not be equal");
            TestSupport.assertTrue(a.equals(a), "a profile should equal itself");
        });

        s.run("toString returns the profile name", () -> {
            Profile p = new Profile("Alex", 180, 75);
            TestSupport.assertEquals("Alex", p.toString(), "toString should be the name");
        });

        s.summary();
    }
}
