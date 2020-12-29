public class AddGitignore {
    private File file;

    /*
    Übergibt File, die zu gitignore hinzugefügt werden soll, damit execute ohne Parameter aufgerufen werden kann
     */
    public void addFile(File file){
        this.file = file;
    }

    /*
    gleiche Form wie bei ICommand
     */
    public void execute(){
        if (!isVersioned(file)){
            //add to gitignore
        }
        else{
            //do nothing
        }
    }

    /*
    If file has already been versioned, it cannot be added to the gitignore file
     */
    private boolean isVersioned(File file){

    }
}
