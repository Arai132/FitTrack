package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//we store only profiles

import model.Profile;

public class DataStore {
    private static DataStore instance;

    private final List<Profile> profiles = new ArrayList<>();
    private Profile currentProfile; // the one the UI is using

    private DataStore() {
    }

    // the ai put synchronized, but again, this isn't needed even though it is good.
    public static synchronized DataStore getInstance() {
        if (instance == null)
            instance = new DataStore();
        return instance;
    }

    // ---- all profiles ----
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

    // ---- current profile ----
    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public void setCurrentProfile(Profile p) {
        if (p != null && !profiles.contains(p)) {
            profiles.add(p); // safety
        }
        this.currentProfile = p;
    }
}