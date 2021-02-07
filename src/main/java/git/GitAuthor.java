package git;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a single author in Git
 */
public class GitAuthor {
    private String name;
    private String email;

    /* Is only instantiated inside the git Package */
    @JsonCreator
    GitAuthor(
            @JsonProperty("name") String name,
            @JsonProperty("email") String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GitAuthor)) return false;
        GitAuthor gitAuthor = (GitAuthor) o;
        return (name.equals(gitAuthor.name) && email.equals(gitAuthor.email));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }
}
