package settings;

import java.util.ArrayList;
import java.util.List;

public abstract class DataObservable {
    protected List<DataObserver> dataObserverList = new ArrayList<>();

    public void addDataChangedListener(DataObserver dataObserver) {
        if (!dataObserverList.contains(dataObserver))
            dataObserverList.add(dataObserver);
    }

    public void fireDataChangedEvent() {
        for (DataObserver o : dataObserverList) {
            o.dataChangedListener(this);
        }
    }

}
