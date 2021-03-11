package git.exception;

/**
 * Exception which is returned if any operations fail due to problems with git or jgit
 */
public class GitException extends Exception {

    /**
     * Throws a new exception
     * @param errorMessage the message to show in the log
     */
    public GitException(String errorMessage){
        super(errorMessage);
    }

    public GitException(){

    }
}
