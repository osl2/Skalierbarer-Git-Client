package Levels;

import Commands.ICommand;

import java.util.List;

/*
Überlegung: Level 3 kann alles, was Level 2 auch kann. Wenn Level 2 schon einen Befehl ausführen kann, dann kann Level 3
diese Methode von Level 2 erben
 */
public class LevelThree implements ILevel{


//    /**
//     * OPTIONAL
//     * Performs push if allowed, otherwise, does nothing
//     */
//    public void executePush(){
//    }

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
     * Method to get a list of the commands that can be executed on that level
     * @return Returns a list of all commands that can be invoked at the current level
     */
    public List<ICommand> getCommands(){
        throw new AssertionError("not implemented");
    }
}
