package commands;

/**
 * This class represents the git stash command. In order to execute you have to
 * pass a {@link Subcommand} and a stash message if needed.
 */
public class Stash implements ICommand, ICommandGUI {
    private String stashMessage;
    private Subcommand subcommand;

    /**
     * Sets the subcommand that should be executed.
     * @param subcommand the subcommand to execute.
     */
    @SuppressWarnings("unused")
    public void setSubcommand(Subcommand subcommand) {
        this.subcommand = subcommand;
    }

    /**
     * {@inheritDoc}
     */
    public boolean execute() {
        return false;
    }

    /**
     * Sets the stash message when the create option is active.
     * @param stashMessage the stash message.
     */
    @SuppressWarnings("unused")
    public void setStashMessage(String stashMessage) {
        this.stashMessage = stashMessage;
    }

    /**
     * {@inheritDoc}
     */
    public String getCommandLine() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Stash";
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return "Open Stash UI";
    }

    /**
     * {@inheritDoc}
     */
    public void onButtonClicked() {

    }

    /**
     * This subcommands indicate which stash command should be executed.
     */
    enum Subcommand {CLEAR, DROP, CREATE, APPLY, STORE}

}
