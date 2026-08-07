package strategy;

import java.util.ArrayList;
import java.util.List;

import model.ExerciseSet;
import model.WorkoutSession;

/**
 * Matches sessions whose workout includes an exercise whose name
 * contains the query (case-insensitive). Empty query returns all
 * sessions.
 */
public class ExerciseSearchStrategy implements SearchStrategy {

    @Override
    public List<WorkoutSession> search(List<WorkoutSession> sessions, String query) {
        List<WorkoutSession> results = new ArrayList<>();
        String needle = query == null ? "" : query.trim().toLowerCase();
        for (WorkoutSession session : sessions) {
            if (needle.isEmpty() || containsExercise(session, needle)) {
                results.add(session);
            }
        }
        return results;
    }

    private boolean containsExercise(WorkoutSession session, String needle) {
        for (ExerciseSet set : session.getSets()) {
            String name = set.getWorkoutExercise().getExercise().getName().toLowerCase();
            if (name.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
