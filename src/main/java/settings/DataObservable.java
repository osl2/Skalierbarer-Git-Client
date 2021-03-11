package settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Observable for the Observer Pattern. Gets notified when Settings or stored Data has changed.
 * Firing the DataChangeEvent is concern of the caller.
 */
public abstract class DataObservable {
    /**
     * List of registered observers.
     */
    protected List<DataObserver> dataObserverList = new ArrayList<>();

    /**
     * Register a new listener to be called when changes occur.
     *
     * @param dataObserver the Observer to register
     */
    public void addDataChangedListener(DataObserver dataObserver) {
        if (!dataObserverList.contains(dataObserver))
            dataObserverList.add(dataObserver);
    }

    /**
     * Notifies all Observers of a Data Change.
     */
    protected void fireDataChangedEvent() {
        for (DataObserver o : dataObserverList) {
            o.dataChangedListener(this);
        }
    }

}
