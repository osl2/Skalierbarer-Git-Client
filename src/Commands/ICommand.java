public interface ICommand {

    public void execute(ILevel level);
    public boolean isAllowed(ILevel level);
    public ILevel getMinimumLevel();
    public String getCommandName();
    public String getGitCommand();
    public String getCommandDescription();

}
