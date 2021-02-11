package commands;

import settings.Settings;

public class Config implements ICommand {
    private String name;
    private String eMail;
    private String errorMessage = "";

    public Config() {
        if(Settings.getInstance().getUser() != null) {
            name = Settings.getInstance().getUser().getName();
            eMail = Settings.getInstance().getUser().getEmail();
        }
    }
    @Override
    public boolean execute() {
        if(name == null || eMail == null) {
            errorMessage = "Es muss ein Name und eine eMail-Adresse angegeben werden";
            return false;
        }
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
