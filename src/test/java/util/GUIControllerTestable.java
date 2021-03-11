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

import controller.GUIController;
import dialogviews.IDialogView;
import views.HistoryView;
import views.IView;

import java.awt.event.WindowListener;

/**
 * Tracking GUIController implementation, only tracking, otherwise No Operation. Used for testing interactions between
 * commands and Controller.
 */
public class GUIControllerTestable extends GUIController {
    public boolean errorHandlerECalled;
    public boolean errorHandlerMSGCalled;
    public boolean restoreDefaultViewCalled;
    public boolean closeDialogViewCalled;
    public boolean setCommandLineCalled;
    public boolean initializeMainWindowCalled;
    public boolean openDialogCalled;
    public boolean openDialogWithListenerCalled;
    public boolean openViewCalled;
    public boolean updateCalled;
    public boolean openMainWindowCalled;

    public GUIControllerTestable() {
    }

    /**
     * Default handler for any Exception which could not be handled by the program, but does not force
     * us to exit.
     *
     * @param e The unhandled exception
     */
    @Override
    public void errorHandler(Exception e) {
        this.errorHandlerECalled = true;
    }

    /**
     * See {@link #errorHandler(Exception)}. Also used to show an error dialog with a custom text to the user
     *
     * @param msg Text of the error dialog
     */
    @Override
    public void errorHandler(String msg) {
        this.errorHandlerMSGCalled = true;
    }

    /**
     * This method can be used to unload a {@link IView} which was loaded with {@link #openView(IView)}.
     * Currently this method restores an instance of {@link HistoryView}
     */
    @Override
    public void restoreDefaultView() {
        this.restoreDefaultViewCalled = true;
    }

    /**
     * Close the open Dialog.
     */
    @Override
    public void closeDialogView() {
        this.closeDialogViewCalled = true;
    }

    /**
     * Replace the commandLine text in the MainWindow
     *
     * @param commandLine the new text to be shown
     */
    @Override
    public void setCommandLine(String commandLine) {
        this.setCommandLineCalled = true;
    }

    /**
     * Creates the MainWindow.
     * <p>
     * Closes any existing MainWindows and instantiates a new one.
     */
    @Override
    public void initializeMainWindow() {
        this.initializeMainWindowCalled = true;
    }

    /**
     * Open a Dialog
     *
     * @param dialog   the Dialog to be opened
     * @param listener WindowListener to attach to the dialog
     */
    @Override
    public void openDialog(IDialogView dialog, WindowListener listener) {
        this.openDialogWithListenerCalled = true;

    }

    /**
     * See {@link #openDialog(IDialogView, WindowListener)}
     *
     * @param dialog
     */
    @Override
    public void openDialog(IDialogView dialog) {
        this.openDialogCalled = true;
    }

    /**
     * Change the view in the MainWindow
     *
     * @param view the View to be opened
     */
    @Override
    public void openView(IView view) {
        this.openViewCalled = true;
    }

    /**
     * Refreshes the GUI.
     */
    @Override
    public void update() {
        this.updateCalled = true;
    }

    /**
     * Show the MainWindow.
     */
    @Override
    public void openMainWindow() {
        this.openMainWindowCalled = true;
    }

    public void resetTestStatus() {
        this.updateCalled = false;
        this.openMainWindowCalled = false;
        this.openDialogCalled = false;
        this.openViewCalled = false;
        this.initializeMainWindowCalled = false;
        this.setCommandLineCalled = false;
        this.closeDialogViewCalled = false;
        this.restoreDefaultViewCalled = false;
        this.errorHandlerMSGCalled = false;
        this.errorHandlerECalled = false;
        this.openDialogWithListenerCalled = false;
    }
}
