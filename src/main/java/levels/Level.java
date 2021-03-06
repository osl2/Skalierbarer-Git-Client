/*-
 * ========================LICENSE_START=================================
 * Git-Client
 * ======================================================================
 * Copyright (C) 2020 - 2021 The Git-Client Project Authors
 * ======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package levels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import commands.ICommandGUI;

import java.util.List;
import java.util.Objects;


/**
 * Encapsulates a set of commands a user may use.
 * Usually configured by data.json
 */
public class Level {
    @JsonProperty("name")
    private final String name; //name as a unique identifier
    private final int id; //For comparing
    private List<ICommandGUI> commands;

    /**
     * Constructor
     *
     * @param name     Name of the level
     * @param commands List of allowed Commands
     * @param id       ID used for sorting, higher means lower in lists
     */
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

    /**
     * Compares two lists of {@link commands.ICommandGUI} interfaces
     * <p>
     * Returns true if both interfaces contain the same classes.
     *
     * @param a list A
     * @param b list B
     * @return true if equal
     */
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
