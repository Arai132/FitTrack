package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import model.Profile;

/**
 * Handles saving/loading a profile's data to/from local disk via
 * plain Java object serialization. Decoupled from DataStore and the
 * GUI so persistence mechanics stay in one place.
 */
public class SerializedObjectStore {

    private static final String DEFAULT_FILE_NAME = "fittrack_data.ser";

    private final File file;

    public SerializedObjectStore() {
        this(new File(DEFAULT_FILE_NAME));
    }

    public SerializedObjectStore(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file must not be null");
        }
        this.file = file;
    }

    @SuppressWarnings("unchecked")
    public List<Profile> load() {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Profile>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new StorageException("Failed to load data from " + file.getPath(), e);
        }
    }

    public void save(List<Profile> profiles) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(new ArrayList<>(profiles));
        } catch (IOException e) {
            throw new StorageException("Failed to save data to " + file.getPath(), e);
        }
    }
}
