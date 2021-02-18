import controller.GUIController;
import views.HistoryView;

public class Main {

    public static void main(String[] args) {
        GUIController c = GUIController.getInstance();
        c.openMainWindow();
        c.openView(new HistoryView());
        // Load Data -> Persistency
        // Todo: Load Plugins -> ???
        // Apply Plugin changes
        // Open GUI
    }

    public static void restart(String commandLine) {
    }
}
