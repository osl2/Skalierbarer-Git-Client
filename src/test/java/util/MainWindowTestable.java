package util;

import views.MainWindow;

public class MainWindowTestable extends MainWindow {

    public boolean openCalled;

    /**
     * Open the Window. This invokes {@link #update()}
     */
    public void open() {
        openCalled = true;
    }
}
