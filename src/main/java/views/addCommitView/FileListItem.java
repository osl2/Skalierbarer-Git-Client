package views.addCommitView;

import git.GitFile;

/**
 * This class represents a list item that holds a GitFile instance. This class is needed to build the list
 * of files with uncommitted changes that is located in the middle panel of AddCommitView.
 * @see AddCommitView
 */
public class FileListItem {
    private GitFile gitFile;
    private boolean isSelected;
    private int i;

    /**
     * Creates a list item that holds a GitFile instance. When the file is already in the staging area,
     * the list item is initialized to be selected.
     * @param gitFile
     */
    public FileListItem(GitFile gitFile){
        this.gitFile = gitFile;
        this.i = i;
        this.isSelected = gitFile.isStaged();
    }

    /**
     *
     * @return The selection state of the item
     */
    public boolean isSelected(){
        return isSelected;
    }

    /**
     * Setter for the selection state. When the user clicks on a list entry, its selection state will change
     * @param isSelected True, if the item should be selected (i.e., the nested GitFile should be marked for git add)
     */
    public void setSelected(boolean isSelected){
        this.isSelected = isSelected;
    }

    /**
     * Method to retrieve the GitFile instance from the list item
     * @return GitFile - the instance that is encapsulated in the list item
     */
    public GitFile getGitFile(){
        return gitFile;
    }

}
