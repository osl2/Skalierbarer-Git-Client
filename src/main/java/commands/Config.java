package commands;

public class Config implements ICommand {
    private String name = null;
    private String eMail = null;
    private String errorMessage;

    @Override
    public boolean execute() {
        return false;
    }


    public void setName(String name) {
        this.name = name;
    }
    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

}
