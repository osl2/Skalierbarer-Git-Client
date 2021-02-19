package settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Observable for the Observer Pattern. Gets notified when Settings or stored Data has changed.
 * Firing the DataChangeEvent is concern of the caller.
 */
public abstract class DataObservable {
    /**
     * List of Registered observers.
     * <p>
     * SHOULD NOT BE CHANGED EXTERNALLY.
     */
    protected List<DataObserver> dataObserverList = new ArrayList<>();

    /**
     * Register a new Listener to be called when Changes occur.
     *
     * @param dataObserver the Observer to register
     */
    public void addDataChangedListener(DataObserver dataObserver) {
        if (!dataObserverList.contains(dataObserver))
            dataObserverList.add(dataObserver);
    }

    /**
     * Notifies all Observers of Change.
     * THIS FUNCTION SHOULD BE CALLED ONLY EXTERNALLY AFTER A GROUP OF CHANGES HAS HAPPENED.
     */
    public void fireDataChangedEvent() {
        for (DataObserver o : dataObserverList) {
            o.dataChangedListener(this);
        }
    }

}
