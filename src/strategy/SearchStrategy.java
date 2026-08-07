package strategy;

import java.util.List;

import model.WorkoutSession;

/**
 * Strategy pattern: pluggable ways to search workout history.
 */
public interface SearchStrategy {

    List<WorkoutSession> search(List<WorkoutSession> sessions, String query);
}
