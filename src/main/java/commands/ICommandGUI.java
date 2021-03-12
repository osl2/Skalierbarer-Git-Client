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
package commands;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import settings.ICommandDeserializer;
import settings.ICommandSerializer;

/**
 * Represents the Command in a Command in the Main window.
 */

@JsonDeserialize(using = ICommandDeserializer.class)
@JsonSerialize(using = ICommandSerializer.class)
public interface ICommandGUI {


  /**
   * Method to get the Commandline input that would be necessary to execute the command.
   *
   * @return Returns a String representation of the corresponding git command to display on
   * the command line
   */
  String getCommandLine();

  /**
   * Method to get the name of the command, that could be displayed in the GUI.
   *
   * @return The name of the command
   */
  String getName();

  /**
   * Method to get a description of the Command to describe for the user, what the command does.
   *
   * @return description as a String
   */
  String getDescription();

  /**
   * OnClick handler for the GUI button representation.
   */
  void onButtonClicked();
  // Otherwise we can't change the view in the gui.

}
