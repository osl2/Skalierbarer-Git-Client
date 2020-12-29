public class Gitignore {

    /**
     * Add the text pattern (could be file path or part of it) of the blob to .gitignore. Blob must be a file, so
     * consider using File, VCSFile or something similar instead
     * @param blop An object that contains file data in JGit. Probably switch to File, VCSFile etc.
     */
    public void addToGitignore(Blop blop){
        if (!file.isVersioned()){
            //add to gitignore
        }
        else{
            //do nothing
        }
    }

    /**
     * Remove the text pattern that matches the file path from .gitignore
     * @param blop An object that contains file data in JGit
     */
    public void removeFromGitignore(Blop blop){
        if (file.isIgnored()){
            //remove
        }
        else{
            //do nothing
        }
    }

}
