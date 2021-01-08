package Levels;

import Commands.ICommand;

import java.util.List;

public interface ILevel {
    //    int getLevelNumber();

    /**
     * Method to get the name of the level for example to display it
     * @return name of the level
     */
    String getLevelName();

    /**
     * Returns a list of commands that are available in that level
     * @return List of acaliable commands
     */
    public List<ICommand> getCommands();

}
