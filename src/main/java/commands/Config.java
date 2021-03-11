package commands;

import git.GitFacade;

/**
 * This class is used the change the e-Mail and the name that will
 * be displayed when the user creates a new Commit.
 */
public class Config implements ICommand {
    private String name = "";
    private String eMail = "";

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

    /**
     * Set the new name of the author.
     * @param name the name of the author.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the new e-Mail of the author.
     * @param eMail the e-Mail.
     */
    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

}
