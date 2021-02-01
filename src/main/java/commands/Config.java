package commands;

public class Config implements ICommand {
    private String name = null;
    private String eMail = null;
    private String errorMessage;

    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

}
