package Commands;

import Levels.ILevel;

public interface ICommand {

    boolean execute(ILevel level);
    boolean isAllowed(ILevel level);
    ILevel getMinimumLevel();

}
