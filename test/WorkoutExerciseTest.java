import model.Exercise;
import model.WorkoutExercise;

public class WorkoutExerciseTest {

    public static void main(String[] args) {
        TestSupport.Suite s = TestSupport.suite("WorkoutExerciseTest");

        s.run("constructor rejects null exercise", () -> TestSupport.assertThrows(
                IllegalArgumentException.class, () -> new WorkoutExercise(null, 3, 10),
                "should reject null exercise"));

        s.run("constructor rejects zero/negative sets", () -> {
            Exercise e = new Exercise("Squat");
            TestSupport.assertThrows(IllegalArgumentException.class, () -> new WorkoutExercise(e, 0, 10),
                    "should reject zero sets");
            TestSupport.assertThrows(IllegalArgumentException.class, () -> new WorkoutExercise(e, -1, 10),
                    "should reject negative sets");
        });

        s.run("constructor rejects zero/negative reps", () -> {
            Exercise e = new Exercise("Squat");
            TestSupport.assertThrows(IllegalArgumentException.class, () -> new WorkoutExercise(e, 3, 0),
                    "should reject zero reps");
            TestSupport.assertThrows(IllegalArgumentException.class, () -> new WorkoutExercise(e, 3, -5),
                    "should reject negative reps");
        });

        s.run("constructor rejects negative weight but allows null", () -> {
            Exercise e = new Exercise("Squat");
            TestSupport.assertThrows(IllegalArgumentException.class, () -> new WorkoutExercise(e, 3, 10, -1),
                    "should reject negative weight");
            WorkoutExercise we = new WorkoutExercise(e, 3, 10, null);
            TestSupport.assertNull(we.getTargetWeight(), "null weight should be allowed");
        });

        s.run("three-arg constructor defaults weight to null", () -> {
            WorkoutExercise we = new WorkoutExercise(new Exercise("Squat"), 3, 10);
            TestSupport.assertNull(we.getTargetWeight(), "default weight");
        });

        s.run("setters validate like constructor", () -> {
            WorkoutExercise we = new WorkoutExercise(new Exercise("Squat"), 3, 10);
            TestSupport.assertThrows(IllegalArgumentException.class, () -> we.setTargetSets(0), "setTargetSets(0)");
            TestSupport.assertThrows(IllegalArgumentException.class, () -> we.setTargetReps(0), "setTargetReps(0)");
            TestSupport.assertThrows(IllegalArgumentException.class, () -> we.setTargetWeight(-10), "setTargetWeight(-10)");

            we.setTargetSets(5);
            we.setTargetReps(12);
            we.setTargetWeight(200);
            TestSupport.assertEquals(5, we.getTargetSets(), "targetSets after set");
            TestSupport.assertEquals(12, we.getTargetReps(), "targetReps after set");
            TestSupport.assertEquals(200, we.getTargetWeight(), "targetWeight after set");
        });

        s.run("getDisplayText includes weight when present", () -> {
            WorkoutExercise we = new WorkoutExercise(new Exercise("Bench Press"), 3, 8, 135);
            String text = we.getDisplayText();
            TestSupport.assertEquals("Bench Press: 3 sets x 8 reps @ 135 lb", text, "display text with weight");
        });

        s.run("getDisplayText omits weight when absent", () -> {
            WorkoutExercise we = new WorkoutExercise(new Exercise("Push Up"), 3, 15);
            String text = we.getDisplayText();
            TestSupport.assertEquals("Push Up: 3 sets x 15 reps", text, "display text without weight");
        });

        s.summary();
    }
}
