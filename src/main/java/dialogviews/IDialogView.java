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
package dialogviews;

import javax.swing.*;
import java.awt.*;

/**
 * Represents a Dialog Window
 * the Information is used by the {@link controller.GUIController}
 * to create a new modal Dialog Window.
 */
public interface IDialogView {

    /**
     * DialogWindow Title
     *
     * @return Window Title as String
     */
    String getTitle();

    /**
     * The Size of the newly created Dialog
     *
     * @return 2D Dimension
     */
    Dimension getDimension();

    /**
     * The content Panel containing all contents of the Dialog
     *
     * @return the contentPanel of the new Dialog
     */
    JPanel getPanel();

    /**
     * Called to notify the Dialog of changes to the application data.
     * A well written Dialog should reload all data shown to the user.
     */
    void update();

}
