import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Exercise implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;
    private String category;

    public Exercise(String name) {
        this(name, null);
    }

    public Exercise(String name, String category) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exercise)) {
            return false;
        }
        Exercise other = (Exercise) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Exercise{id='" + id + "', name='" + name + "', category='" + category + "'}";
    }
}
