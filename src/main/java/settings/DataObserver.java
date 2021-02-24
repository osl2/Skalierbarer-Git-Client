package settings;

/**
 * Observer for Data Changes
 */
public abstract class DataObserver {

    /**
     * Gets called when a change happened.
     *
     * @param observable the Observerable that has been changed.
     */
    protected abstract void dataChangedListener(DataObservable observable);
}
