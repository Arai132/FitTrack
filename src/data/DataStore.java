package data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Profile;

public class DataStore {
    private static DataStore instance;

    /**
     * Relative path – works on Windows & Unix because launchers cd to project root
     */
    private static final Path DATA_FILE = Paths.get("data", "fittrack.dat");

    private final List<Profile> profiles = new ArrayList<>();
    private Profile currentProfile;

    private DataStore() {
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // ---- profiles ----
    public List<Profile> getProfiles() {
        return Collections.unmodifiableList(profiles);
    }

    public void addProfile(Profile p) {
        if (p != null && !profiles.contains(p)) {
            profiles.add(p);
        }
    }

    public void removeProfile(Profile p) {
        profiles.remove(p);
        if (currentProfile == p) {
            currentProfile = profiles.isEmpty() ? null : profiles.get(0);
        }
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public void setCurrentProfile(Profile p) {
        if (p != null && !profiles.contains(p)) {
            profiles.add(p);
        }
        this.currentProfile = p;
    }

    // =========================================================
    // PERSISTENCE
    // =========================================================

    /**
     * Saves all profiles + which one is current.
     * Creates the "data" folder if it does not exist.
     */
    public synchronized void save() {
        try {
            Files.createDirectories(DATA_FILE.getParent()); // data/

            try (ObjectOutputStream out = new ObjectOutputStream(
                    Files.newOutputStream(DATA_FILE))) {

                out.writeObject(new ArrayList<>(profiles));

                // store current profile by id (null-safe)
                String currentId = (currentProfile != null) ? currentProfile.getId() : null;
                out.writeObject(currentId);
            }
            System.out.println("Saved to " + DATA_FILE.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Save failed: " + e.getMessage());
        }
    }

    /**
     * Loads profiles from disk if the file exists.
     * Safe to call when the file is missing (fresh start).
     */
    @SuppressWarnings("unchecked")
    public synchronized void load() {
        if (!Files.exists(DATA_FILE)) {
            System.out.println("No save file found (" + DATA_FILE + ") – starting fresh");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(
                Files.newInputStream(DATA_FILE))) {

            List<Profile> loaded = (List<Profile>) in.readObject();
            String currentId = (String) in.readObject();

            profiles.clear();
            profiles.addAll(loaded);

            currentProfile = null;
            if (currentId != null) {
                for (Profile p : profiles) {
                    if (currentId.equals(p.getId())) {
                        currentProfile = p;
                        break;
                    }
                }
            }
            // fallback: first profile
            if (currentProfile == null && !profiles.isEmpty()) {
                currentProfile = profiles.get(0);
            }

            System.out.println("Loaded " + profiles.size() + " profile(s) from "
                    + DATA_FILE.toAbsolutePath());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Load failed: " + e.getMessage());
            profiles.clear();
            currentProfile = null;
        }
    }
}