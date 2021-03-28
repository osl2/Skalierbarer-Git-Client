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

/**
 * This interface represents an executable Command.
 * commands are to be prepared using their custom methods
 * Changes are to be applied to the underlying repository
 * exclusively via the execute() method.
 */
public interface ICommand {

  /**
   *Method to execute the command.
   *
   * @return true if the command has been executed successfully \
   *    *     false otherwise
   */
  boolean execute();


}
