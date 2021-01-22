package views;

import git.GitCommit;
import git.GitFile;

import javax.swing.*;

public class DiffView implements IDiffView {

    /**
     * Opens the difference between the given file and and the previous version of the file.
     *
     * @param fileName the name of the given file.
     */
    public void openDiff(String fileName) {
    }


    public JPanel openDiffView() {
        return null;
    }

    /**
     * Opens the difference between the given file and and the previous version of the file.
     *
     * @param activeCommit
     * @param file
     */
    public void setDiff(GitCommit activeCommit, GitFile file) {

    }
}
