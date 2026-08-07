package strategy;

import java.util.ArrayList;
import java.util.List;

import model.WorkoutSession;

/**
 * Matches sessions whose underlying workout name contains the query
 * (case-insensitive). Empty query returns all sessions.
 */
public class NameSearchStrategy implements SearchStrategy {

    @Override
    public List<WorkoutSession> search(List<WorkoutSession> sessions, String query) {
        List<WorkoutSession> results = new ArrayList<>();
        String needle = query == null ? "" : query.trim().toLowerCase();
        for (WorkoutSession session : sessions) {
            if (needle.isEmpty() || session.getWorkout().getName().toLowerCase().contains(needle)) {
                results.add(session);
            }
        }
        return results;
    }
}
