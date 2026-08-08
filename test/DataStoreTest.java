import java.lang.reflect.Field;

import data.DataStore;
import model.Profile;

public class DataStoreTest {

    public static void main(String[] args) {
        TestSupport.Suite s = TestSupport.suite("DataStoreTest");

        s.run("getInstance returns the same instance", () -> {
            resetSingleton();
            DataStore a = DataStore.getInstance();
            DataStore b = DataStore.getInstance();
            TestSupport.assertTrue(a == b, "getInstance should always return the same object");
        });

        s.run("addProfile adds a profile and ignores duplicates", () -> {
            resetSingleton();
            DataStore store = DataStore.getInstance();
            Profile p = new Profile("Alex", 180, 75);
            store.addProfile(p);
            store.addProfile(p);
            TestSupport.assertEquals(1, store.getProfiles().size(), "duplicate add should be a no-op");
        });

        s.run("addProfile ignores null", () -> {
            resetSingleton();
            DataStore store = DataStore.getInstance();
            store.addProfile(null);
            TestSupport.assertTrue(store.getProfiles().isEmpty(), "null add should be a no-op");
        });

        s.run("setCurrentProfile also registers the profile if new", () -> {
            resetSingleton();
            DataStore store = DataStore.getInstance();
            Profile p = new Profile("Alex", 180, 75);
            store.setCurrentProfile(p);
            TestSupport.assertEquals(p, store.getCurrentProfile(), "current profile should be set");
            TestSupport.assertTrue(store.getProfiles().contains(p), "profile should be auto-registered");
        });

        s.run("removeProfile falls back to another profile when current is removed", () -> {
            resetSingleton();
            DataStore store = DataStore.getInstance();
            Profile first = new Profile("Alex", 180, 75);
            Profile second = new Profile("Sam", 170, 65);
            store.addProfile(first);
            store.addProfile(second);
            store.setCurrentProfile(first);

            store.removeProfile(first);

            TestSupport.assertEquals(second, store.getCurrentProfile(), "should fall back to remaining profile");
            TestSupport.assertEquals(1, store.getProfiles().size(), "removed profile should be gone from the list");
        });

        s.run("removeProfile sets current to null when the last profile is removed", () -> {
            resetSingleton();
            DataStore store = DataStore.getInstance();
            Profile only = new Profile("Alex", 180, 75);
            store.setCurrentProfile(only);

            store.removeProfile(only);

            TestSupport.assertNull(store.getCurrentProfile(), "current should become null");
            TestSupport.assertTrue(store.getProfiles().isEmpty(), "profile list should be empty");
        });

        s.run("getProfiles returns an unmodifiable view", () -> {
            resetSingleton();
            DataStore store = DataStore.getInstance();
            TestSupport.assertThrows(UnsupportedOperationException.class,
                    () -> store.getProfiles().add(new Profile("Alex", 180, 75)), "list should be unmodifiable");
        });

        s.summary();
    }

    /**
     * DataStore is a singleton with private static state; using
     * reflection here keeps each test isolated without adding a
     * test-only reset method to production code.
     */
    private static void resetSingleton() {
        try {
            Field instanceField = DataStore.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to reset DataStore singleton for test isolation", e);
        }
    }
}
