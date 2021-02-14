import controller.GUIController;
import dialogviews.UsernamePasswordDialogView;

public class Main {

  public static void main(String[] args) {
    GUIController c = GUIController.getInstance();
    c.openMainWindow();
    c.openDialog(new UsernamePasswordDialogView());
    // Load Data -> Persistency
    // Todo: Load Plugins -> ???
    // Apply Plugin changes
    // Open GUI
  }

  public static void restart(String commandLine){}
}
