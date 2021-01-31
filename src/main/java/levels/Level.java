package levels;

import commands.ICommand;
import java.util.List;



public class Level {
  private final String name; //name as a unique identifier
  private final int id; //For comparing
  List<ICommand> commands;

  public Level(String name, List<ICommand> commands, int id) {
    this.commands = commands;
    this.name = name;
    this.id = id;
  }

  /**
   * Method to get the name of the level for example to display it.
   *
   * @return name of the level
   */
  String getLevelName() {
    return name;
  }

  /**
   * Returns a list of commands that are available in that level.
   *
   * @return List of callable commands
   */
  public List<ICommand> getCommands() {

    return commands;
  }

  /**
   * Method to get the id of the Level
   * @return Returns the id
   */
  public int getId() {
    return id;
  }
}
