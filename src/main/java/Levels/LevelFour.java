package Levels;

import Commands.ICommand;

import java.util.List;

public class LevelFour implements ILevel {
    /**
     * Method to get the name of the level for example to display it
     *
     * @return name of the level
     */
    @Override
    public String getLevelName() {
        throw new AssertionError("not implemented");
    }

    /**
     * Returns a list of commands that are available in that level
     *
     * @return List of available commands
     */
    @Override
    public List<ICommand> getCommands() {
        throw new AssertionError("not implemented");
    }
}
