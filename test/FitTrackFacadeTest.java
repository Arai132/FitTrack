import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import data.DataStore;
import facade.FitTrackFacade;
import model.Exercise;
import model.Profile;
import model.Workout;
import model.WorkoutExercise;
import model.WorkoutSession;
import report.ProgressSummary;
import strategy.NameSearchStrategy;

/**
 * FitTrackFacade always persists to "fittrack_data.ser" relative to the
 * working directory (no dependency injection for the path). To avoid ever
 * touching a real save file, any pre-existing file in the working
 * directory is backed up before the suite runs and restored afterwards,
 * and each scenario starts from a clean singleton + clean file.
 */
public class FitTrackFacadeTest {

    private static final File DATA_FILE = new File("fittrack_data.ser");
    private static final File BACKUP_FILE = new File("fittrack_data.ser.test-backup");

    public static void main(String[] args) {
        boolean hadExistingData = DATA_FILE.exists();
        if (hadExistingData) {
            DATA_FILE.renameTo(BACKUP_FILE);
        }

        try {
            runSuite();
        } finally {
            DATA_FILE.delete();
            if (hadExistingData) {
                BACKUP_FILE.renameTo(DATA_FILE);
            }
        }
    }

    private static void runSuite() {
        TestSupport.Suite s = TestSupport.suite("FitTrackFacadeTest");

        s.run("createProfile registers it and makes it current", () -> {
            FitTrackFacade facade = freshFacade();
            Profile p = facade.createProfile("Alex", 180, 75);
            TestSupport.assertEquals(1, facade.getAllProfiles().size(), "should have one profile");
            TestSupport.assertEquals(p, facade.getCurrentProfile(), "new profile should become current");
            TestSupport.assertEquals(p, facade.getProfile(), "getProfile() alias should match getCurrentProfile()");
        });

        s.run("operations that need a profile fail with none selected", () -> {
            FitTrackFacade facade = freshFacade();
            TestSupport.assertThrows(IllegalStateException.class,
                    () -> facade.createExercise("Squat", "Legs"), "createExercise without a profile");
            TestSupport.assertThrows(IllegalStateException.class,
                    () -> facade.createWorkout("Push Day"), "createWorkout without a profile");
        });

        s.run("switchProfile rejects null and changes current profile", () -> {
            FitTrackFacade facade = freshFacade();
            Profile a = facade.createProfile("Alex", 180, 75);
            Profile b = facade.createProfile("Sam", 170, 65);
            TestSupport.assertThrows(IllegalArgumentException.class, () -> facade.switchProfile(null),
                    "should reject null profile");

            facade.switchProfile(a);
            TestSupport.assertEquals(a, facade.getCurrentProfile(), "should switch back to first profile");
            TestSupport.assertEquals(2, facade.getAllProfiles().size(), "both profiles should still be registered");
        });

        s.run("updateProfile mutates the current profile", () -> {
            FitTrackFacade facade = freshFacade();
            facade.createProfile("Alex", 180, 75);
            facade.updateProfile("Alexandra", 182, 76.5);

            Profile current = facade.getCurrentProfile();
            TestSupport.assertEquals("Alexandra", current.getName(), "name");
            TestSupport.assertEquals(182.0, current.getHeightCm(), "heightCm");
            TestSupport.assertEquals(76.5, current.getCurrentWeightKg(), "currentWeightKg");
        });

        s.run("deleteProfile removes it from the store", () -> {
            FitTrackFacade facade = freshFacade();
            Profile p = facade.createProfile("Alex", 180, 75);
            facade.deleteProfile(p);
            TestSupport.assertTrue(facade.getAllProfiles().isEmpty(), "profile should be removed");
        });

        s.run("createExercise adds to the current profile's library", () -> {
            FitTrackFacade facade = freshFacade();
            facade.createProfile("Alex", 180, 75);
            Exercise e = facade.createExercise("Squat", "Legs");
            TestSupport.assertEquals(1, facade.getAllExercises().size(), "should have one exercise");
            TestSupport.assertTrue(facade.getAllExercises().contains(e), "should contain created exercise");
        });

        s.run("updateExercise renames and re-categorizes", () -> {
            FitTrackFacade facade = freshFacade();
            facade.createProfile("Alex", 180, 75);
            Exercise e = facade.createExercise("Squat", "Legs");
            facade.updateExercise(e, "Front Squat", "Quads");
            TestSupport.assertEquals("Front Squat", e.getName(), "name");
            TestSupport.assertEquals("Quads", e.getCategory(), "category");
        });

        s.run("deleteExercise cascades into every workout referencing it", () -> {
            FitTrackFacade facade = freshFacade();
            facade.createProfile("Alex", 180, 75);
            Exercise squat = facade.createExercise("Squat", "Legs");
            Workout legDay = facade.createWorkout("Leg Day");
            facade.addExerciseToWorkout(legDay, squat, 3, 5, 225);

            facade.deleteExercise(squat);

            TestSupport.assertTrue(facade.getAllExercises().isEmpty(), "exercise library should be empty");
            TestSupport.assertTrue(legDay.getExercises().isEmpty(), "workout should no longer reference the exercise");
        });

        s.run("createWorkout starts active; deleteWorkout archives instead of removing", () -> {
            FitTrackFacade facade = freshFacade();
            facade.createProfile("Alex", 180, 75);
            Workout w = facade.createWorkout("Push Day");

            TestSupport.assertEquals(1, facade.getActiveWorkouts().size(), "should start active");

            facade.deleteWorkout(w);
            TestSupport.assertTrue(facade.getActiveWorkouts().isEmpty(), "archived workout should not be active");
            TestSupport.assertEquals(1, facade.getAllWorkouts().size(), "archived workout should still exist");

            facade.reactivateWorkout(w);
            TestSupport.assertEquals(1, facade.getActiveWorkouts().size(), "should be active again");
        });

        s.run("updateWorkoutName renames the workout", () -> {
            FitTrackFacade facade = freshFacade();
            facade.createProfile("Alex", 180, 75);
            Workout w = facade.createWorkout("Push Day");
            facade.updateWorkoutName(w, "Upper Body");
            TestSupport.assertEquals("Upper Body", w.getName(), "name after update");
        });

        s.run("addExerciseToWorkout overloads and removeExerciseFromWorkout", () -> {
            FitTrackFacade facade = freshFacade();
            facade.createProfile("Alex", 180, 75);
            Exercise bench = facade.createExercise("Bench Press", "Chest");
            Workout w = facade.createWorkout("Push Day");

            facade.addExerciseToWorkout(w, bench, 3, 8);
            TestSupport.assertEquals(1, w.getExercises().size(), "should have one workout exercise");
            TestSupport.assertNull(w.getExercises().get(0).getTargetWeight(), "no-weight overload");

            WorkoutExercise we = w.getExercises().get(0);
            facade.removeExerciseFromWorkout(w, we);
            TestSupport.assertTrue(w.getExercises().isEmpty(), "should be removed");
        });

        s.run("startWorkout rejects archived workouts and workouts with no exercises", () -> {
            FitTrackFacade facade = freshFacade();
            facade.createProfile("Alex", 180, 75);

            Workout empty = facade.createWorkout("Empty Day");
            TestSupport.assertThrows(IllegalArgumentException.class, () -> facade.startWorkout(empty),
                    "should reject a workout with no exercises");

            Workout archived = facade.createWorkout("Archived Day");
            facade.addExerciseToWorkout(archived, facade.createExercise("Squat", "Legs"), 3, 5);
            facade.deleteWorkout(archived);
            TestSupport.assertThrows(IllegalStateException.class, () -> facade.startWorkout(archived),
                    "should reject an archived workout");
        });

        s.run("startWorkout returns a session tied to the workout", () -> {
            FitTrackFacade facade = freshFacade();
            facade.createProfile("Alex", 180, 75);
            Exercise squat = facade.createExercise("Squat", "Legs");
            Workout legDay = facade.createWorkout("Leg Day");
            facade.addExerciseToWorkout(legDay, squat, 3, 5);

            WorkoutSession session = facade.startWorkout(legDay);
            TestSupport.assertEquals(legDay, session.getWorkout(), "session should reference the workout");
            TestSupport.assertTrue(facade.getHistory().isEmpty(), "session should not be in history until finished");
        });

        s.run("finishAndSaveSession adds the session to history", () -> {
            FitTrackFacade facade = freshFacade();
            facade.createProfile("Alex", 180, 75);
            Exercise squat = facade.createExercise("Squat", "Legs");
            Workout legDay = facade.createWorkout("Leg Day");
            facade.addExerciseToWorkout(legDay, squat, 3, 5);

            WorkoutSession session = facade.startWorkout(legDay);
            session.markSetComplete(0, 5, 225);
            facade.finishAndSaveSession(session);

            TestSupport.assertEquals(1, facade.getHistory().size(), "history should contain the finished session");
        });

        s.run("searchHistory delegates to the given SearchStrategy", () -> {
            FitTrackFacade facade = freshFacade();
            facade.createProfile("Alex", 180, 75);
            Exercise squat = facade.createExercise("Squat", "Legs");
            Workout legDay = facade.createWorkout("Leg Day");
            facade.addExerciseToWorkout(legDay, squat, 3, 5);
            WorkoutSession session = facade.startWorkout(legDay);
            session.markSetComplete(0, 5, 225);
            facade.finishAndSaveSession(session);

            List<WorkoutSession> found = facade.searchHistory(new NameSearchStrategy(), "leg");
            TestSupport.assertEquals(1, found.size(), "should find the session via the strategy");

            List<WorkoutSession> notFound = facade.searchHistory(new NameSearchStrategy(), "push");
            TestSupport.assertTrue(notFound.isEmpty(), "should find nothing for a non-matching query");
        });

        s.run("getProgressSummary reflects finished sessions", () -> {
            FitTrackFacade facade = freshFacade();
            facade.createProfile("Alex", 180, 75);
            Exercise squat = facade.createExercise("Squat", "Legs");
            Workout legDay = facade.createWorkout("Leg Day");
            facade.addExerciseToWorkout(legDay, squat, 1, 5);

            WorkoutSession session = facade.startWorkout(legDay);
            session.markSetComplete(0, 5, 225);
            facade.finishAndSaveSession(session);

            ProgressSummary summary = facade.getProgressSummary();
            TestSupport.assertEquals(1, summary.getTotalSessions(), "totalSessions");
            TestSupport.assertEquals(1, summary.getCompletedSessions(), "completedSessions");
            TestSupport.assertEquals(1, summary.getTotalSetsLogged(), "totalSetsLogged");
        });

        s.run("getExerciseTrend reflects logged sets for that exercise", () -> {
            FitTrackFacade facade = freshFacade();
            facade.createProfile("Alex", 180, 75);
            Exercise squat = facade.createExercise("Squat", "Legs");
            Workout legDay = facade.createWorkout("Leg Day");
            facade.addExerciseToWorkout(legDay, squat, 1, 5);

            WorkoutSession session = facade.startWorkout(legDay);
            session.markSetComplete(0, 5, 225);
            facade.finishAndSaveSession(session);

            TestSupport.assertEquals(1, facade.getExerciseTrend(squat).size(), "trend should have one data point");
        });

        s.summary();
    }

    private static FitTrackFacade freshFacade() {
        resetDataStoreSingleton();
        DATA_FILE.delete();
        return new FitTrackFacade();
    }

    private static void resetDataStoreSingleton() {
        try {
            Field instanceField = DataStore.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to reset DataStore singleton for test isolation", e);
        }
    }
}
