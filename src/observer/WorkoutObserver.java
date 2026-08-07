package observer;

import model.ExerciseSet;
import model.WorkoutSession;

/**
 * Observer side of the Observer pattern: views register with a
 * WorkoutSession (the Subject) to be pushed live progress updates.
 */
public interface WorkoutObserver {

    void onSetCompleted(WorkoutSession session, ExerciseSet set);

    void onSessionFinished(WorkoutSession session);
}
