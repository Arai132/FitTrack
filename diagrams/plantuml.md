# Use Case Diagram
```plantuml
left to right direction

actor User

rectangle "FitTrack" {

usecase "View Profile" as VP

usecase "Create Profile" as CP
usecase "Edit Profile" as EP
usecase "Delete Profile" as DP

usecase "Create Workout" as CW
usecase "Edit Workout" as EW
usecase "Delete Workout" as DW
usecase "View Workout" as VW

usecase "Create Exercise" as CE
usecase "View Exercise" as VE
usecase "Edit Exercise" as EE
usecase "Add Exercise\nto Workout" as AE
usecase "Remove Exercise\nfrom Workout" as RE
usecase "Delete Exercise" as DE

usecase "Start Workout" as SW
usecase "Track Current Workout" as TCW
usecase "Mark Set Complete" as MSC

usecase "View Workout History" as VWH
usecase "Search Workout History" as SWH
}

User --> VP
User --> CP

VP <.. EP : <<extend>>
EP <.. DP : <<extend>>

VP <.. VW : <<extend>>
VP <.. CW : <<extend>>
VW <.. EW : <<extend>>
VP <.. VE : <<extend>>
VE <.. EE : <<extend>>
EE <.. DE : <<extend>>

VP <.. CE : <<extend>>

EW <.. DW : <<extend>>
EW <.. AE : <<extend>>
EW <.. RE : <<extend>>

VP <.. SW : <<extend>>

VP <.. VWH : <<extend>>
VWH <.. SWH : <<extend>>

SW <.. TCW : <<extend>>
TCW ..> MSC : <<include>>

@enduml
```

# Class Diagram

Non-GUI layers only (model, patterns, persistence, facade) — `controller`/`view` (Swing) intentionally omitted for now.

```plantuml
left to right direction
skinparam classAttributeIconSize 0

package model {
    class Profile {
        -id: String
        -name: String
        -heightCm: double
        -currentWeightKg: double
        -exercises: List<Exercise>
        -workouts: List<Workout>
        -history: List<WorkoutSession>
        +addExercise(exercise: Exercise)
        +removeExercise(exercise: Exercise)
        +addWorkout(workout: Workout)
        +getActiveWorkouts(): List<Workout>
        +addToHistory(session: WorkoutSession)
    }

    class Exercise {
        -id: String
        -name: String
        -category: String
    }

    class Workout {
        -id: String
        -name: String
        -exercises: List<WorkoutExercise>
        -isActive: boolean
        +addExercise(exercise: Exercise, targetSets: int, targetReps: int)
        +removeExerciseFor(exercise: Exercise)
        +archive()
        +reactivate()
    }

    class WorkoutExercise {
        -exercise: Exercise
        -targetSets: int
        -targetReps: int
        -targetWeight: Integer
        +getDisplayText(): String
    }

    class WorkoutSession {
        -id: String
        -workout: Workout
        -date: LocalDateTime
        -sets: List<ExerciseSet>
        -observers: List<WorkoutObserver>
        +markSetComplete(index: int, actualReps: int, actualWeight: Integer)
        +isFinished(): boolean
        +getCompletedCount(): int
        +addObserver(observer: WorkoutObserver)
        +removeObserver(observer: WorkoutObserver)
    }

    class ExerciseSet {
        -workoutExercise: WorkoutExercise
        -completed: boolean
        -actualReps: int
        -actualWeight: Integer
        +complete(actualReps: int, actualWeight: Integer)
    }

    Profile "1" *-- "0..*" Exercise : owns
    Profile "1" *-- "0..*" Workout : owns
    Profile "1" *-- "0..*" WorkoutSession : history
    Workout "1" *-- "0..*" WorkoutExercise : contains
    WorkoutExercise "0..*" --> "1" Exercise : targets
    WorkoutSession "1" --> "1" Workout : based on
    WorkoutSession "1" *-- "1..*" ExerciseSet : logs
    ExerciseSet "0..*" --> "1" WorkoutExercise : targets
}

package observer {
    interface WorkoutObserver {
        +onSetCompleted(session: WorkoutSession, set: ExerciseSet)
        +onSessionFinished(session: WorkoutSession)
    }
}

WorkoutSession "1" o-- "0..*" WorkoutObserver : notifies

package strategy {
    interface SearchStrategy {
        +search(sessions: List<WorkoutSession>, query: String): List<WorkoutSession>
    }

    class NameSearchStrategy {
        +search(sessions: List<WorkoutSession>, query: String): List<WorkoutSession>
    }

    class DateSearchStrategy {
        +search(sessions: List<WorkoutSession>, query: String): List<WorkoutSession>
    }

    class ExerciseSearchStrategy {
        +search(sessions: List<WorkoutSession>, query: String): List<WorkoutSession>
    }

    NameSearchStrategy ..|> SearchStrategy
    DateSearchStrategy ..|> SearchStrategy
    ExerciseSearchStrategy ..|> SearchStrategy
}

package report {
    class ProgressAnalyzer {
        +summarize(history: List<WorkoutSession>): ProgressSummary
        +trendFor(history: List<WorkoutSession>, exercise: Exercise): List<ExerciseTrendPoint>
    }

    class ProgressSummary {
        -totalSessions: int
        -completedSessions: int
        -totalSetsLogged: int
    }

    class ExerciseTrendPoint {
        -date: LocalDateTime
        -actualReps: int
        -actualWeight: Integer
    }

    ProgressAnalyzer ..> ProgressSummary : creates
    ProgressAnalyzer ..> ExerciseTrendPoint : creates
}

package data {
    class DataStore {
        -{static} instance: DataStore
        -profiles: List<Profile>
        -currentProfile: Profile
        +{static} getInstance(): DataStore
        +addProfile(profile: Profile)
        +removeProfile(profile: Profile)
        +getCurrentProfile(): Profile
        +setCurrentProfile(profile: Profile)
    }

    class SerializedObjectStore {
        -file: File
        +load(): List<Profile>
        +save(profiles: List<Profile>)
    }

    class StorageException {
        +StorageException(message: String, cause: Throwable)
    }

    StorageException --|> RuntimeException
    DataStore "1" o-- "0..*" Profile : holds
    SerializedObjectStore ..> Profile : (de)serializes
    SerializedObjectStore ..> StorageException : throws
}

package facade {
    class FitTrackFacade {
        -store: DataStore
        -objectStore: SerializedObjectStore
        -progressAnalyzer: ProgressAnalyzer
        +createProfile(name: String, heightCm: double, weightKg: double): Profile
        +createExercise(name: String, category: String): Exercise
        +createWorkout(name: String): Workout
        +addExerciseToWorkout(workout: Workout, exercise: Exercise, sets: int, reps: int)
        +startWorkout(workout: Workout): WorkoutSession
        +finishAndSaveSession(session: WorkoutSession)
        +searchHistory(strategy: SearchStrategy, query: String): List<WorkoutSession>
        +getProgressSummary(): ProgressSummary
        +getExerciseTrend(exercise: Exercise): List<ExerciseTrendPoint>
    }
}

FitTrackFacade --> DataStore : uses
FitTrackFacade --> SerializedObjectStore : uses
FitTrackFacade --> ProgressAnalyzer : uses
FitTrackFacade ..> SearchStrategy : uses
FitTrackFacade ..> Profile : creates/mutates
FitTrackFacade ..> Workout : creates/mutates
FitTrackFacade ..> Exercise : creates/mutates
FitTrackFacade ..> WorkoutSession : creates

@enduml
```
