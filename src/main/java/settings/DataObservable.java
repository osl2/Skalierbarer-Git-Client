/*-
 * ========================LICENSE_START=================================
 * Git-Client
 * ======================================================================
 * Copyright (C) 2020 - 2021 The Git-Client Project Authors
 * ======================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
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
