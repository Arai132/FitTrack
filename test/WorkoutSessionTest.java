import java.util.ArrayList;
import java.util.List;

import model.Exercise;
import model.ExerciseSet;
import model.Workout;
import model.WorkoutSession;
import observer.WorkoutObserver;

public class WorkoutSessionTest {

    public static void main(String[] args) {
        TestSupport.Suite s = TestSupport.suite("WorkoutSessionTest");

        s.run("constructor rejects null workout", () -> TestSupport.assertThrows(
                IllegalArgumentException.class, () -> new WorkoutSession(null), "should reject null workout"));

        s.run("constructor creates one ExerciseSet per WorkoutExercise, all incomplete", () -> {
            Workout w = new Workout("Push Day");
            w.addExercise(new Exercise("Bench Press"), 3, 8);
            w.addExercise(new Exercise("Push Up"), 3, 15);
            WorkoutSession session = new WorkoutSession(w);

            TestSupport.assertEquals(2, session.getSets().size(), "should create one set per workout exercise");
            TestSupport.assertFalse(session.isFinished(), "should not be finished yet");
            TestSupport.assertEquals(0, session.getCompletedCount(), "nothing completed yet");
        });

        s.run("markSetComplete completes the targeted set and updates counts", () -> {
            Workout w = new Workout("Push Day");
            w.addExercise(new Exercise("Bench Press"), 3, 8);
            w.addExercise(new Exercise("Push Up"), 3, 15);
            WorkoutSession session = new WorkoutSession(w);

            session.markSetComplete(0, 8, 135);

            TestSupport.assertEquals(1, session.getCompletedCount(), "one set should be completed");
            TestSupport.assertFalse(session.isFinished(), "session should not be finished yet");
            ExerciseSet completed = session.getSets().get(0);
            TestSupport.assertTrue(completed.isCompleted(), "targeted set should be complete");
            TestSupport.assertEquals(8, completed.getActualReps(), "actual reps recorded");
        });

        s.run("isFinished becomes true only once every set is completed", () -> {
            Workout w = new Workout("Push Day");
            w.addExercise(new Exercise("Bench Press"), 3, 8);
            WorkoutSession session = new WorkoutSession(w);

            session.markSetComplete(0, 8, 135);

            TestSupport.assertTrue(session.isFinished(), "single-set session should be finished after completion");
        });

        s.run("observer receives onSetCompleted for every completed set", () -> {
            Workout w = new Workout("Push Day");
            w.addExercise(new Exercise("Bench Press"), 3, 8);
            w.addExercise(new Exercise("Push Up"), 3, 15);
            WorkoutSession session = new WorkoutSession(w);
            RecordingObserver observer = new RecordingObserver();
            session.addObserver(observer);

            session.markSetComplete(0, 8, 135);

            TestSupport.assertEquals(1, observer.setCompletedEvents.size(), "one onSetCompleted event expected");
            TestSupport.assertFalse(observer.sessionFinished, "session should not be reported finished yet");
        });

        s.run("observer receives onSessionFinished once the last set completes", () -> {
            Workout w = new Workout("Push Day");
            w.addExercise(new Exercise("Bench Press"), 3, 8);
            WorkoutSession session = new WorkoutSession(w);
            RecordingObserver observer = new RecordingObserver();
            session.addObserver(observer);

            session.markSetComplete(0, 8, 135);

            TestSupport.assertEquals(1, observer.setCompletedEvents.size(), "one onSetCompleted event expected");
            TestSupport.assertTrue(observer.sessionFinished, "session should be reported finished");
        });

        s.run("removeObserver stops further notifications", () -> {
            Workout w = new Workout("Push Day");
            w.addExercise(new Exercise("Bench Press"), 3, 8);
            w.addExercise(new Exercise("Push Up"), 3, 15);
            WorkoutSession session = new WorkoutSession(w);
            RecordingObserver observer = new RecordingObserver();
            session.addObserver(observer);
            session.removeObserver(observer);

            session.markSetComplete(0, 8, 135);

            TestSupport.assertTrue(observer.setCompletedEvents.isEmpty(), "removed observer should get no events");
        });

        s.run("addObserver rejects null", () -> {
            Workout w = new Workout("Push Day");
            w.addExercise(new Exercise("Bench Press"), 3, 8);
            WorkoutSession session = new WorkoutSession(w);
            TestSupport.assertThrows(IllegalArgumentException.class, () -> session.addObserver(null),
                    "should reject null observer");
        });

        s.run("toString includes workout name and completion progress", () -> {
            Workout w = new Workout("Push Day");
            w.addExercise(new Exercise("Bench Press"), 3, 8);
            WorkoutSession session = new WorkoutSession(w);
            String text = session.toString();
            TestSupport.assertTrue(text.contains("Push Day") && text.contains("0/1"), "toString content: " + text);
        });

        s.summary();
    }

    private static final class RecordingObserver implements WorkoutObserver {
        final List<ExerciseSet> setCompletedEvents = new ArrayList<>();
        boolean sessionFinished = false;

        @Override
        public void onSetCompleted(WorkoutSession session, ExerciseSet set) {
            setCompletedEvents.add(set);
        }

        @Override
        public void onSessionFinished(WorkoutSession session) {
            sessionFinished = true;
        }
    }
}
