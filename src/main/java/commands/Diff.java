package commands;

import git.GitCommit;
import git.GitFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

public class Diff implements ICommand {
  private GitCommit activeCommit;
  private GitFile activeFile;
  private String errorMessage;
  private String activeDiff;
  private ArrayList<String> lines = new ArrayList<String>();
  private int[] fileStartCount;
  private boolean validDiff = false;

  /**
   * Executes the "git diff" command. Can only be used after @setDiffCommit was called once.
   *
   * @return true, if the command has been executed successfully
   */
  public boolean execute() {
    if(activeCommit == null || activeFile == null) {
      errorMessage = "Es muss ein GitCommit und ein GitFile übergeben werden um den Diff Befehl auszuführen.";
      return false;
    }
    if(validDiff) {
      return true;
    }
    try {
      activeDiff = activeCommit.getDiff();
    } catch (IOException e) {
      e.printStackTrace();
    }
    fileStartCount = getAllFileStarts();
    validDiff = true;
    return true;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Sets the commit ID and the file name to compare with the previous one.
   *
   * @param activeCommit the selected commit.
   * @param file the file to compare to his previous version.
   */
  public void setDiffCommit(GitCommit activeCommit, GitFile file) {
    if(!validDiff == true || this.activeCommit.getHash().compareTo(activeCommit.getHash()) != 0) {
      this.activeCommit = activeCommit;
      validDiff = false;
    }
    this.activeFile = file;
  }

  /**
   * Can only be called after setDiffCommit was called.
   * @return the git diff of the given commit and the given file.
   */
  public String[] diffGit() {
    if(!validDiff) {
      return null;
    }
    int startLine = 0;
    int finishLine = 0;
    String activeFilePath = activeFile.getPath().getPath();
    String separator = Pattern.quote(System.getProperty("file.separator"));
    String[] relativePath = activeFilePath.split(separator);
    String[] firstDirectory = lines.get(0).split("/");
    int index = 0;
    // Find the start of the relative path used in git.
    for(int i = 0; i < relativePath.length; i++) {
      if(relativePath[i].compareTo(firstDirectory[1]) == 0) {
        index = i;
        break;
      }
    }
    // Build the relative path used in git an store it in activeFilePath.
    activeFilePath = "diff --git a";
    for(int i = index; i < relativePath.length; i++) {
      activeFilePath += "/" + relativePath[i];
    }
    int compareLength = activeFilePath.length();
    // Find the diff entry of the given file.
    for(int i = 0; i < fileStartCount.length; i++) {
      if(lines.get(fileStartCount[i]).length() < compareLength) {
        continue;
      }
      if(lines.get(fileStartCount[i]).substring(0, compareLength).compareTo(activeFilePath) == 0) {
        startLine = fileStartCount[i];
        if(i + 1 == fileStartCount.length) {
          finishLine = lines.size();
        } else {
          finishLine = fileStartCount[i + 1];
        }
      }
    }
    String[] output = new String[finishLine-startLine-5];
    for(int i = startLine + 5; i < finishLine; i++) {
      output[i - 5 - startLine] = lines.get(i);
    }
    return output;
  }

  private int[] getAllFileStarts() {
    ArrayList<Integer> startLine = new ArrayList<Integer>();
    activeDiff.lines().forEach(lines::add);
    for(int i = 0; i < lines.size(); i++) {
      if(lines.get(i).length() < 10) {
        continue;
      } else if(lines.get(i).substring(0, 10).compareTo("diff --git") == 0) {
        startLine.add(i);
      }
    }
    Collections.sort(startLine);
    int[] output = new int[startLine.size()];
    for(int i = 0; i < startLine.size(); i++) {
      output[i] = startLine.get(i);
    }
    return output;
  }
}
