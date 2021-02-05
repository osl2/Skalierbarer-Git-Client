package git.exception;

public class GitException extends Exception {
    private String errorMessage;

    public GitException(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public GitException(){

    }

    public String getErrorMessage(){
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage){
        this.errorMessage = errorMessage;
    }


}
