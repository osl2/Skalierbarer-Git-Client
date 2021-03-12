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
package views;

import git.GitCommit;
import git.GitFile;

import javax.swing.*;

/**
 * Specifies how a diff formatting view has to be defined.
 */
public interface IDiffView {

  /**
   * Opens the view of the difference between a selected file and the
   * previous version of the file.
   *
   * @return the TextPanel containing the Diff
   */
  JTextPane openDiffView();

  /**
   * Opens the difference between the given commit and the previous one.
   * Only the git diff of the given File will be displayed.
   *
   * @param activeCommit is the commit to compare to its previous version.
   * @param file is a the File of the given commit.
   */
  void setDiff(GitCommit activeCommit, GitFile file);

  /**
   * Opens the difference between the given File and the working directory.
   * @param file a File which changed since the latest commit.
   */
  void setDiff(GitFile file);
}
