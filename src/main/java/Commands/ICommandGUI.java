package Commands;

/**
 * Represents the Command in a Command in the Main window
 */
public interface ICommandGUI {

    String getCommandLine(String userInput);

    String getName();

    String getDescription();

}