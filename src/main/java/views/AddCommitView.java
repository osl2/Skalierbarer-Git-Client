package views;

import git.GitFile;
import java.util.List;
import javax.swing.JPanel;

public class AddCommitView extends JPanel implements IView {
  public JPanel getView() {
    return null;
  }

  /**
   * Method to get a list of the modified files.
   *
   * @return a list of GitBlobs that should be added to the staging-area when execute() is called
   */
  public List<GitFile> getBlobs() {
    return null;
  }
}
