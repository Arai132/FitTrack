package report;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.Exercise;
import model.ExerciseSet;
import model.WorkoutSession;

/**
 * Computes aggregated progress stats and per-exercise trends from a
 * profile's workout history. Kept separate from Profile and
 * FitTrackFacade so those stay focused on state and coordination
 * rather than computation.
 */
public class ProgressAnalyzer {

    public ProgressSummary summarize(List<WorkoutSession> history) {
        int completedSessions = 0;
        int totalSetsLogged = 0;
        for (WorkoutSession session : history) {
            if (session.isFinished()) {
                completedSessions++;
            }
            totalSetsLogged += session.getCompletedCount();
        }
        return new ProgressSummary(history.size(), completedSessions, totalSetsLogged);
    }

    public List<ExerciseTrendPoint> trendFor(List<WorkoutSession> history, Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("exercise must not be null");
        }
        List<ExerciseTrendPoint> trend = new ArrayList<>();
        for (WorkoutSession session : history) {
            for (ExerciseSet set : session.getSets()) {
                if (set.isCompleted() && set.getWorkoutExercise().getExercise().equals(exercise)) {
                    trend.add(new ExerciseTrendPoint(session.getDate(), set.getActualReps(), set.getActualWeight()));
                }
            }
        }
        trend.sort(Comparator.comparing(ExerciseTrendPoint::getDate));
        return trend;
    }
}
