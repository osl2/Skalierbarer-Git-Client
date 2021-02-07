package git.exception;

public class GitException extends Exception {

    public GitException(String errorMessage){
        super(errorMessage);
    }

    public GitException(){

    }


  public GitException(String msg) {
    super(msg);
  }
}
