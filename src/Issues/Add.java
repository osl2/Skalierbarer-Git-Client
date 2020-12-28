public class Add implements ICommand {

    /**
     * OPTIONAL
     * performs git add if current level is greater equal than minimum required level
     */
    public void execute(ILevel level){
        if (level.getLevelNumber() >= this.getMinimumLevel()){
            //perform git add
        }
        else{
            //do nothing
        }
    }

    /**
     * OPTIONAL
     * @return The lowest level at which the command can be invoked
     */
    public ILevel getMinimumLevel(){
        //return LevelOne;
    }
}
