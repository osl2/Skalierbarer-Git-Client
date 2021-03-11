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
package util;

import javax.swing.*;
import java.awt.*;

public class JOptionPaneTestable extends JOptionPane {
    private static boolean showConfirmDialogCalled;

    /*
    This method always returns 0 (confirm)
     */
    public static int showConfirmDialog(Component parentComponent,
                                        Object message, String title, int optionType, int messageType)
            throws HeadlessException {

        showConfirmDialogCalled = true;
        return JOptionPane.YES_OPTION;
    }

    public void resetTestStatus() {
        showConfirmDialogCalled = false;
    }

    public boolean isShowConfirmDialogCalled() {
        return showConfirmDialogCalled;
    }
}
