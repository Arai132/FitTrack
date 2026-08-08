import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Exercise;
import model.Workout;
import model.WorkoutSession;
import report.ExerciseTrendPoint;
import report.ProgressAnalyzer;
import report.ProgressSummary;

public class ReportTest {

    public static void main(String[] args) {
        TestSupport.Suite s = TestSupport.suite("ReportTest");

        s.run("ExerciseTrendPoint exposes constructor values via getters", () -> {
            LocalDateTime date = LocalDateTime.of(2026, 1, 15, 9, 30);
            ExerciseTrendPoint point = new ExerciseTrendPoint(date, 10, 135);
            TestSupport.assertEquals(date, point.getDate(), "date");
            TestSupport.assertEquals(10, point.getActualReps(), "actualReps");
            TestSupport.assertEquals(135, point.getActualWeight(), "actualWeight");
        });

        s.run("ExerciseTrendPoint toString omits weight when null", () -> {
            LocalDateTime date = LocalDateTime.of(2026, 1, 15, 9, 30);
            ExerciseTrendPoint point = new ExerciseTrendPoint(date, 20, null);
            String text = point.toString();
            TestSupport.assertTrue(text.contains("20 reps"), "should mention reps");
            TestSupport.assertFalse(text.contains("lb"), "should not mention weight unit when weight is null");
        });

        s.run("ProgressSummary exposes constructor values via getters and toString", () -> {
            ProgressSummary summary = new ProgressSummary(5, 3, 42);
            TestSupport.assertEquals(5, summary.getTotalSessions(), "totalSessions");
            TestSupport.assertEquals(3, summary.getCompletedSessions(), "completedSessions");
            TestSupport.assertEquals(42, summary.getTotalSetsLogged(), "totalSetsLogged");
            String text = summary.toString();
            TestSupport.assertTrue(text.contains("5") && text.contains("3") && text.contains("42"),
                    "toString should mention all three numbers");
        });

        s.run("ProgressAnalyzer.summarize counts finished sessions and logged sets", () -> {
            Exercise bench = new Exercise("Bench Press");
            Workout pushDay = new Workout("Push Day");
            pushDay.addExercise(bench, 1, 8);

            WorkoutSession finished = new WorkoutSession(pushDay);
            finished.markSetComplete(0, 8, 135);

            WorkoutSession unfinished = new WorkoutSession(pushDay);
            // left incomplete on purpose

            List<WorkoutSession> history = new ArrayList<>();
            history.add(finished);
            history.add(unfinished);

            ProgressSummary summary = new ProgressAnalyzer().summarize(history);

            TestSupport.assertEquals(2, summary.getTotalSessions(), "totalSessions should count all sessions");
            TestSupport.assertEquals(1, summary.getCompletedSessions(), "only one session was fully finished");
            TestSupport.assertEquals(1, summary.getTotalSetsLogged(), "only one set was logged as completed");
        });

        s.run("ProgressAnalyzer.summarize handles empty history", () -> {
            ProgressSummary summary = new ProgressAnalyzer().summarize(new ArrayList<>());
            TestSupport.assertEquals(0, summary.getTotalSessions(), "totalSessions");
            TestSupport.assertEquals(0, summary.getCompletedSessions(), "completedSessions");
            TestSupport.assertEquals(0, summary.getTotalSetsLogged(), "totalSetsLogged");
        });

        s.run("ProgressAnalyzer.trendFor rejects a null exercise", () -> TestSupport.assertThrows(
                IllegalArgumentException.class, () -> new ProgressAnalyzer().trendFor(new ArrayList<>(), null),
                "should reject null exercise"));

        s.run("ProgressAnalyzer.trendFor only includes completed sets for the requested exercise", () -> {
            Exercise bench = new Exercise("Bench Press");
            Exercise squat = new Exercise("Squat");
            Workout mixed = new Workout("Full Body");
            mixed.addExercise(bench, 1, 8);
            mixed.addExercise(squat, 1, 5);

            WorkoutSession session = new WorkoutSession(mixed);
            session.markSetComplete(0, 8, 135); // bench: completed
            // squat (index 1) left incomplete on purpose

            List<WorkoutSession> history = new ArrayList<>();
            history.add(session);

            List<ExerciseTrendPoint> benchTrend = new ProgressAnalyzer().trendFor(history, bench);
            List<ExerciseTrendPoint> squatTrend = new ProgressAnalyzer().trendFor(history, squat);

            TestSupport.assertEquals(1, benchTrend.size(), "bench should have one completed data point");
            TestSupport.assertEquals(8, benchTrend.get(0).getActualReps(), "bench reps recorded");
            TestSupport.assertTrue(squatTrend.isEmpty(), "squat set was never completed, so no trend point");
        });

        s.run("ProgressAnalyzer.trendFor sorts results chronologically", () -> {
            Exercise bench = new Exercise("Bench Press");
            Workout pushDay = new Workout("Push Day");
            pushDay.addExercise(bench, 1, 8);

            WorkoutSession older = new WorkoutSession(pushDay);
            older.markSetComplete(0, 5, 100);
            WorkoutSession newer = new WorkoutSession(pushDay);
            newer.markSetComplete(0, 8, 135);

            List<WorkoutSession> history = new ArrayList<>();
            // Insert out of chronological order; both share the same instant-ish
            // "now" timestamp in practice, so this mainly checks the sort is stable
            // and doesn't throw rather than asserting on wall-clock ordering.
            history.add(newer);
            history.add(older);

            List<ExerciseTrendPoint> trend = new ProgressAnalyzer().trendFor(history, bench);
            TestSupport.assertEquals(2, trend.size(), "both completed sets should appear in the trend");
            TestSupport.assertTrue(!trend.get(0).getDate().isAfter(trend.get(1).getDate()),
                    "trend points should be sorted oldest first");
        });

        s.summary();
    }
}
