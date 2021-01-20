package commands;

public class Stash implements ICommand, ICommandGUI {
    private String stashMessage;
    private Subcommand subcommand;

    public void setSubcommand(Subcommand subcommand) {
        this.subcommand = subcommand;
    }

    public boolean execute() {
        return false;
    }

    public void setStashMessage(String stashMessage) {
        this.stashMessage = stashMessage;
    }

    public String getErrorMessage() {
        return null;
    }

    public String getCommandLine(String userInput) {
        return null;
    }

    public String getName() {
        return "Stash";
    }

    public String getDescription() {
        return "Open Stash UI";
    }

    public void onButtonClicked() {

    }

    enum Subcommand {CLEAR, DROP, CREATE, APPLY, STORE}

}
