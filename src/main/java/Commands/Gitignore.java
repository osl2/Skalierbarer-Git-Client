package Commands;

import Git.GitBlob;

public class Gitignore {

    /**
     * Add the text pattern (could be file path or part of it) of the blob to .gitignore. Blob must be a file.
     * @param blob An object that contains file data in JGit.
     */
    public void addToGitignore(GitBlob blob){
        if (!blob.isVersioned()){
            //add to gitignore
        }
        else{
            //do nothing
        }
    }

    /**
     * Remove the text pattern that matches the file path from .gitignore
     * @param blob An object that contains file data in JGit
     */
    public void removeFromGitignore(GitBlob blob){
        if (blob.isIgnored()){
            //remove
        }
        else{
            //do nothing
        }
    }

}
