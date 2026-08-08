import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data.SerializedObjectStore;
import data.StorageException;
import model.Exercise;
import model.Profile;
import model.Workout;

public class SerializedObjectStoreTest {

    public static void main(String[] args) {
        TestSupport.Suite s = TestSupport.suite("SerializedObjectStoreTest");

        s.run("constructor rejects null file", () -> TestSupport.assertThrows(IllegalArgumentException.class,
                () -> new SerializedObjectStore(null), "should reject null file"));

        s.run("load() on a missing file returns an empty list", () -> {
            File file = newNonExistentTempPath();
            SerializedObjectStore store = new SerializedObjectStore(file);
            List<Profile> loaded = store.load();
            TestSupport.assertTrue(loaded.isEmpty(), "missing file should load as empty list");
        });

        s.run("save() then load() round-trips profile data", () -> {
            File file = newNonExistentTempPath();
            try {
                SerializedObjectStore store = new SerializedObjectStore(file);

                Profile profile = new Profile("Alex", 180, 75);
                Exercise squat = new Exercise("Squat", "Legs");
                profile.addExercise(squat);
                Workout legDay = new Workout("Leg Day");
                legDay.addExercise(squat, 3, 5, 225);
                profile.addWorkout(legDay);

                List<Profile> toSave = new ArrayList<>();
                toSave.add(profile);
                store.save(toSave);

                List<Profile> loaded = store.load();

                TestSupport.assertEquals(1, loaded.size(), "should load one profile");
                Profile loadedProfile = loaded.get(0);
                TestSupport.assertEquals("Alex", loadedProfile.getName(), "profile name should round-trip");
                TestSupport.assertEquals(1, loadedProfile.getExercises().size(), "exercises should round-trip");
                TestSupport.assertEquals(1, loadedProfile.getWorkouts().size(), "workouts should round-trip");
                TestSupport.assertEquals(225,
                        loadedProfile.getWorkouts().get(0).getExercises().get(0).getTargetWeight(),
                        "nested workout exercise data should round-trip");
            } finally {
                file.delete();
            }
        });

        s.run("save() overwrites a previous save", () -> {
            File file = newNonExistentTempPath();
            try {
                SerializedObjectStore store = new SerializedObjectStore(file);

                List<Profile> first = new ArrayList<>();
                first.add(new Profile("Alex", 180, 75));
                store.save(first);

                List<Profile> second = new ArrayList<>();
                second.add(new Profile("Sam", 170, 65));
                second.add(new Profile("Jo", 165, 60));
                store.save(second);

                List<Profile> loaded = store.load();
                TestSupport.assertEquals(2, loaded.size(), "second save should fully replace the first");
            } finally {
                file.delete();
            }
        });

        s.run("load() on a corrupt file wraps the failure as StorageException", () -> {
            File file = newNonExistentTempPath();
            try {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("this is not a serialized object stream");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                SerializedObjectStore store = new SerializedObjectStore(file);
                TestSupport.assertThrows(StorageException.class, store::load,
                        "corrupt file should raise StorageException");
            } finally {
                file.delete();
            }
        });

        s.summary();
    }

    private static File newNonExistentTempPath() {
        try {
            File file = File.createTempFile("fittrack-test-", ".ser");
            file.delete();
            file.deleteOnExit();
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Could not create temp file for test", e);
        }
    }
}
