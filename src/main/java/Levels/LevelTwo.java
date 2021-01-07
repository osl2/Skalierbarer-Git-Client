package Levels;

import Commands.ICommand;

import java.util.List;

public class LevelTwo implements ILevel{

    /**
     * Returns the name of the Level.
     * @return the name of the level.
     */
    @Override
    public String getLevelName() {
        throw new AssertionError("not implemented");
    }

    /**
     * Returns a list of all git commands of this level.
     * @return the list of git commands.
     */
    @Override
    public List<ICommand> getCommands() {
        throw new AssertionError("not implemented");}
}
