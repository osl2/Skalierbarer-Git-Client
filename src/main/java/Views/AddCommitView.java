package Views;

import Git.GitFile;

import javax.swing.*;
import java.util.List;

public class AddCommitView implements IView {
    public JPanel getView() {
        return null;
    }

    /**
     *
     * @return a list of GitBlobs that should be added to the staging-area when execute() is called
     */
    public List<GitFile> getBlobs(){
        return null;
    }
}
