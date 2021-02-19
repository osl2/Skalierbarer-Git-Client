package levels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import commands.ICommandGUI;

import java.util.List;
import java.util.Objects;


public class Level {
    @JsonProperty("name")
    private final String name; //name as a unique identifier
    private final int id; //For comparing
    private List<ICommandGUI> commands;

    @JsonCreator
    public Level(@JsonProperty("name") String name,
                 @JsonProperty("commands") List<ICommandGUI> commands,
                 @JsonProperty("id") int id) {
        this.commands = commands;
        this.name = name;
        this.id = id;
    }

    /**
     * Method to get the name of the level for example to display it.
     *
     * @return name of the level
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a list of commands that are available in that level.
     *
     * @return List of callable commands
     */
    public List<ICommandGUI> getCommands() {
        return commands;
    }

    /**
     * Method to get the id of the Level
     *
     * @return Returns the id
     */
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Level)) return false;
        Level level = (Level) o;

        // Check if commands contains instances of the same classes

        return id == level.id && name.equals(level.name) && iCommandGUIEquals(commands, level.commands);
    }

    public static boolean iCommandGUIEquals(List<ICommandGUI> a, List<ICommandGUI> b) {
        return a.stream()
                .allMatch( // for all Commands in a
                        e -> b.stream()
                                .anyMatch( // there exists
                                        x -> e.getClass() == x.getClass() // a matching Command in b
                                )
                );
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}
