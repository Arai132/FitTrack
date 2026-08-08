import java.util.ArrayList;
import java.util.List;

import model.Exercise;
import model.Workout;
import model.WorkoutSession;
import strategy.DateSearchStrategy;
import strategy.ExerciseSearchStrategy;
import strategy.NameSearchStrategy;
import strategy.SearchStrategy;

public class SearchStrategyTest {

    public static void main(String[] args) {
        TestSupport.Suite s = TestSupport.suite("SearchStrategyTest");

        s.run("NameSearchStrategy matches workout name case-insensitively", () -> {
            Workout pushDay = new Workout("Push Day");
            pushDay.addExercise(new Exercise("Bench Press"), 3, 8);
            Workout legDay = new Workout("Leg Day");
            legDay.addExercise(new Exercise("Squat"), 3, 5);
            List<WorkoutSession> sessions = sessionsFor(pushDay, legDay);

            SearchStrategy strategy = new NameSearchStrategy();
            List<WorkoutSession> results = strategy.search(sessions, "push");

            TestSupport.assertEquals(1, results.size(), "should match only the Push Day session");
            TestSupport.assertEquals("Push Day", results.get(0).getWorkout().getName(), "matched workout name");
        });

        s.run("NameSearchStrategy returns everything for an empty/blank query", () -> {
            List<WorkoutSession> sessions = sessionsFor(workoutNamed("Push Day"), workoutNamed("Leg Day"));
            SearchStrategy strategy = new NameSearchStrategy();

            TestSupport.assertEquals(2, strategy.search(sessions, "").size(), "empty query should return all");
            TestSupport.assertEquals(2, strategy.search(sessions, "   ").size(), "blank query should return all");
            TestSupport.assertEquals(2, strategy.search(sessions, null).size(), "null query should return all");
        });

        s.run("ExerciseSearchStrategy matches any exercise name in the session", () -> {
            Workout pushDay = new Workout("Push Day");
            pushDay.addExercise(new Exercise("Bench Press"), 3, 8);
            Workout legDay = new Workout("Leg Day");
            legDay.addExercise(new Exercise("Squat"), 3, 5);
            List<WorkoutSession> sessions = sessionsFor(pushDay, legDay);

            SearchStrategy strategy = new ExerciseSearchStrategy();
            List<WorkoutSession> results = strategy.search(sessions, "squat");

            TestSupport.assertEquals(1, results.size(), "should match only the session containing Squat");
            TestSupport.assertEquals("Leg Day", results.get(0).getWorkout().getName(), "matched via exercise name");
        });

        s.run("ExerciseSearchStrategy is case-insensitive and returns all for empty query", () -> {
            Workout pushDay = new Workout("Push Day");
            pushDay.addExercise(new Exercise("Bench Press"), 3, 8);
            List<WorkoutSession> sessions = sessionsFor(pushDay);
            SearchStrategy strategy = new ExerciseSearchStrategy();

            TestSupport.assertEquals(1, strategy.search(sessions, "BENCH").size(), "should be case-insensitive");
            TestSupport.assertEquals(1, strategy.search(sessions, "").size(), "empty query should return all");
        });

        s.run("DateSearchStrategy matches sessions by yyyy-MM-dd substring", () -> {
            List<WorkoutSession> sessions = sessionsFor(workoutNamed("Push Day"));
            // Sessions are stamped with LocalDateTime.now(); use the current year as a
            // substring that is guaranteed to be present without depending on the clock.
            String currentYear = String.valueOf(java.time.LocalDate.now().getYear());
            SearchStrategy strategy = new DateSearchStrategy();

            TestSupport.assertEquals(1, strategy.search(sessions, currentYear).size(),
                    "should match sessions from the current year");
            TestSupport.assertTrue(strategy.search(sessions, "1900").isEmpty(),
                    "should not match an unrelated year");
        });

        s.run("DateSearchStrategy returns everything for an empty query", () -> {
            List<WorkoutSession> sessions = sessionsFor(workoutNamed("Push Day"), workoutNamed("Leg Day"));
            SearchStrategy strategy = new DateSearchStrategy();
            TestSupport.assertEquals(2, strategy.search(sessions, "").size(), "empty query should return all");
        });

        s.summary();
    }

    private static Workout workoutNamed(String name) {
        Workout w = new Workout(name);
        w.addExercise(new Exercise("Placeholder"), 1, 1);
        return w;
    }

    private static List<WorkoutSession> sessionsFor(Workout... workouts) {
        List<WorkoutSession> sessions = new ArrayList<>();
        for (Workout w : workouts) {
            sessions.add(new WorkoutSession(w));
        }
        return sessions;
    }
}
