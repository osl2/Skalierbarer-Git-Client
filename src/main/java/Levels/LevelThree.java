import java.util.List;

/*
Überlegung: Level 3 kann alles, was Level 2 auch kann. Wenn Level 2 schon einen Befehl ausführen kann, dann kann Level 3
diese Methode von Level 2 erben
 */
public class LevelThree extends LevelTwo implements ILevel{

    /**
     * OPTIONAL?
     * @return Returns the current level as an integer number
     */
    public int getLevelNumber(){
        //return 3;
    }

    /**
     *
     * @return Returns a String representation of the current level
     */
    public String getLevelString(){
        //return "Kompetenzstufe 3";
    }

    /**
     * OPTIONAL
     * Performs push if allowed, otherwise, does nothing
     */
    public void executePush(){
    }

    /**
     * OPTIONAL
     * @return Returns a list of all commands that can be invoked at the current level
     */
    public List<ICommand> getAvailableCommands(){

    }
}
