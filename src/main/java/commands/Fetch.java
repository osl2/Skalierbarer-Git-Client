package commands;

import controller.GUIController;
import dialogviews.FetchDialogView;
import git.CredentialProviderHolder;
import git.GitBranch;
import git.GitFacade;
import git.GitRemote;
import git.exception.GitException;

import java.util.LinkedList;
import java.util.List;

public class Fetch implements ICommand, ICommandGUI {
  private String errorMessage;
  private String commandLine;
  private String commandName;
  private String commandDescription;
  private LinkedList<GitRemote> remotes = new LinkedList<GitRemote>();

  /**
   * Executes the "git fetch" command. Can only be used after setRemotes was called once.
   *
   * @return True, if it is successfully executed false if not
   */
  public boolean execute()  {
    GitFacade facade = new GitFacade();
    boolean suc = facade.fetchRemotes(remotes);
    return suc;
  }

  public String getErrorMessage() {
    return null;
  }

  /**
   * Returns a list containing all remote names.
   *
   * @return a list with with remote names.
   */

  public void addRemote(GitRemote remote){
    if(remotes.contains(remote) == false) {
      remotes.add(remote);
    }
  }

  public void addBranch(GitRemote remote, GitBranch branch) {
    if (remotes.contains(remote) == false){
      remotes.add(remote);
    }
    remote.addBranch(branch);
  }
  /**
   * Method to get the Commandline input that would be necessarry to execute the command.
   *
   * @return Returns a String representation of the corresponding
   *     git command to display on the command line
   */
  public String getCommandLine() {
    //TODO Maybe other commandLine than this
    if (remotes.size() == 1){
      return "git fetch " + remotes.getFirst().getName();
    }
    else if (remotes.size() > 1){
      String out = null;
      for (int i = 0; i < remotes.size(); i++){
        if (remotes.get(i).getFetchBranches() == null){
          out = out + "git fetch " + remotes.get(i).getName() + System.lineSeparator();
        }
        for (int j = 0; j < remotes.get(i).getFetchBranches().size(); j++){
          out = out + "git fetch " + remotes.get(i).getName() + " " + remotes.get(i).getFetchBranches().get(j).getName() + System.lineSeparator();
        }
      }
      return out;
    }
    else {
      return "";
    }
  }

  /**
   * Method to get the name of the command, that could be displayed in the GUI.
   *
   * @return The name of the command
   */
  public String getName() {
    return "Fetch";
  }

  /**
   * Method to get a description of the Command to describe for the user, what the command does.
   *
   * @return description as a Sting
   */
  public String getDescription() {
    return "Kommando, welches mehrere Zweige aus mehreren Online-Repositories" +
            "hohlt, und für diese einen neuen Zweig im aktuellen lokalen repository anlegt.";
  }

  public void onButtonClicked() {
    FetchDialogView fetch = new FetchDialogView();
    while (fetch.isNeedNew()){
      CredentialProviderHolder.getInstance().changeProvider(true);
      fetch = new FetchDialogView();
    }
    GUIController.getInstance().openDialog(fetch);
  }
}

