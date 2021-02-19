package commands;

import git.GitFacade;

public class Config implements ICommand {
    private String name = "";
    private String eMail = "";

    public Config() {
    }

    @Override
    public boolean execute() {
        if (name == null || eMail == null) {
            return false;
        }
        GitFacade facade = new GitFacade();
        boolean retVal = (name.compareTo("") == 0) || facade.setConfigValue("user.name", name);
        retVal &= (eMail.compareTo("") == 0) || facade.setConfigValue("user.email", eMail);
        return retVal;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

}
