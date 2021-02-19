package commands;

import controller.GUIController;
import dialogviews.FetchDialogView;
import git.CredentialProviderHolder;
import git.GitBranch;
import git.GitFacade;
import git.GitRemote;
import git.exception.GitException;

import java.util.LinkedList;

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
    boolean suc = false;
      suc = tryFetch();
    return suc;
  }


  /**
   * Returns a list containing all remote names.
   *
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
      String out = "";
      for (int i = 0; i < remotes.size(); i++){
        if (remotes.get(i).getFetchBranches() == null){
          out = out + "git fetch " + remotes.get(i).getName() + System.lineSeparator();
        }
        else {
          for (int j = 0; j < remotes.get(i).getFetchBranches().size(); j++) {
            out = out + "git fetch " + remotes.get(i).getName() + " " + remotes.get(i).getFetchBranches().get(j).getName() + System.lineSeparator();
          }
        }
      }
      return out;
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
    FetchDialogView dialogView = new FetchDialogView();
    if (dialogView.isOpen()){
      GUIController.getInstance().openDialog(dialogView);
    }

  }
  private boolean tryFetch(){
    return retryFetch();
  }
  private boolean retryFetch(){
    GitFacade gitFacade = new GitFacade();
    try {
      gitFacade.fetchRemotes(remotes);
      return true;
    } catch (GitException e) {
      CredentialProviderHolder.getInstance().changeProvider(true, "");
      if (CredentialProviderHolder.getInstance().isActive()){
        return tryFetch();
      }
      else{
        CredentialProviderHolder.getInstance().setActive(true);
        return false;
      }
    }
  }
}

