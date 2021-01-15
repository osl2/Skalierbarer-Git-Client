package levels;

import commands.ICommand;

import java.util.List;

public class Level {
  //    int getLevelNumber();

  /**
   * Method to get the name of the level for example to display it
   *
   * @return name of the level
   */
  String getLevelName() {
    return "";
  }

  /**
   * Returns a list of commands that are available in that level
   *
   * @return List of callable commands
   */
  public List<ICommand> getCommands() {
    return null;
  }

}
