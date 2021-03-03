package git;

import org.eclipse.jgit.lib.IndexDiff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a single Change Conflict which may happen during a merge.
 */
public class GitChangeConflict {
  private static final String CONFLICT_MARKER_BEGIN = "<<<<<<< ";
  private static final String CONFLICT_MARKER_SEPARATOR = "=======";
  private static final String CONFLICT_MARKER_END = ">>>>>>> ";
  private final IndexDiff.StageState state;
  private String optionOurs;
  private final GitFile gitFile;
  private String optionTheirs;
  private final int startLine;
  private boolean resolved = false;
  private String result;
  private boolean deleted;
  private int length;

  /**
   * Method to generate a ChangeConflict.
   *
   * @param gitFile File, on wich the changeConflict happens
   * @param state   State of the conflict
   */
  GitChangeConflict(GitFile gitFile, IndexDiff.StageState state) {
    this(gitFile, state, 0);
  }

  /**
   * Method to generate a ChangeConflict.
   *
   * @param gitFile   File, on witch the changeConflict happens
   * @param state     State of the conflict
   * @param startLine line, at witch the conflict starts
   */
  GitChangeConflict(GitFile gitFile, IndexDiff.StageState state, int startLine) {
    this.gitFile = gitFile;
    this.state = state;
    this.startLine = startLine;
    populateOptions(startLine);

  }

  /**
   * Method to get all ChangeConflics for one file.
   *
   * @param gitFile File, for which the conflicts are wanted
   * @param state   State of the conflicts
   * @return List of all conflicts of a file
   */
  public static List<GitChangeConflict> getConflictsForFile(GitFile gitFile, IndexDiff.StageState state) {
    ArrayList<GitChangeConflict> returnList = new ArrayList<>();
    switch (state) {
      case BOTH_ADDED:
        // Fall through
      case DELETED_BY_THEM:
        // Fall through
      case DELETED_BY_US:
        // There is at most one conflict for that file.
        returnList.add(new GitChangeConflict(gitFile, state));
        break;
      case BOTH_MODIFIED:
        // Multiple Conflicts possible. We need to parse in file conflict markers.
        File f = gitFile.getPath();
        try (BufferedReader br = new BufferedReader(new FileReader(f))){
          int i = 0;
          String line;
          while ((line = br.readLine()) != null) {
            if (line.startsWith(CONFLICT_MARKER_BEGIN))
              returnList.add(new GitChangeConflict(gitFile, state, i));
            ++i;
          }
          break;
        } catch (IOException e) {
          throw new AssertionError("File in GitFile did not exist");
        }
      default:
        throw new AssertionError("Unexpected Change type: " + state.toString());
    }

    return returnList;
  }

  /**
   * Method to populate the options.
   *
   * @param startIndex Index, on witch it should be started
   */
  private void populateOptions(int startIndex) {
    try (BufferedReader br = new BufferedReader(new FileReader(gitFile.getPath()))){
      if (state == IndexDiff.StageState.DELETED_BY_THEM) {
        this.optionOurs = br.lines().collect(Collectors.joining(System.lineSeparator()));
        this.optionTheirs = "DELETED";
        this.length = -1;
        return;
      } else if (state == IndexDiff.StageState.DELETED_BY_US) {
        this.optionTheirs = br.lines().collect(Collectors.joining(System.lineSeparator()));
        this.optionOurs = "DELETED";
        this.length = -1;
        return;
      }
      int i = 0;
      while (i < startIndex) {
        br.readLine();
        ++i;
      }
      // Start of Conflict
      if (!br.readLine().startsWith(CONFLICT_MARKER_BEGIN)) {
        // There was no conflict here
        throw new AssertionError("There was no change marker in File "
            + gitFile.getPath().toString() + " at line " + startIndex);
      }
      ++i;

      String line;
      StringBuilder optionOursBuilder = new StringBuilder();
      StringBuilder optionTheirsBuilder = new StringBuilder();
      while (!Objects.equals(line = br.readLine(), CONFLICT_MARKER_SEPARATOR)) {
        optionOursBuilder.append(line);
        optionOursBuilder.append(System.lineSeparator());
        ++i;
      }
      ++i;
      // After separator
      while (!(line = br.readLine()).startsWith(CONFLICT_MARKER_END)) {
        optionTheirsBuilder.append(line);
        optionTheirsBuilder.append(System.lineSeparator());
        ++i;
      }

      if (optionOursBuilder.length() > 0)
        optionOursBuilder.deleteCharAt(optionOursBuilder.lastIndexOf(System.lineSeparator()));
      if (optionTheirsBuilder.length() > 0)
        optionTheirsBuilder.deleteCharAt(optionTheirsBuilder.lastIndexOf(System.lineSeparator()));
      this.optionOurs = optionOursBuilder.toString();
      this.optionTheirs = optionTheirsBuilder.toString();
      this.length = i - startIndex - 1;


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * accepts the first option.
   */
  public void acceptOurs() {
    if (resolved) return;
    if (state == IndexDiff.StageState.DELETED_BY_US) {
      deleted = true;
    }
    if (state != IndexDiff.StageState.BOTH_MODIFIED)
      resolved = true;
    if (result == null) result = "";
    this.result += getOptionOurs() + System.lineSeparator();
  }

  /**
   * accepts the second option
   */
  public void acceptTheirs() {
    if (resolved) return;
    if (state == IndexDiff.StageState.DELETED_BY_THEM) {
      deleted = true;
    }
    if (state != IndexDiff.StageState.BOTH_MODIFIED)
      resolved = true;
    if (result == null) result = "";
    this.result += getOptionTheirs() + System.lineSeparator();

  }

  /**
   * Method to get the Option to keep ours.
   *
   * @return ours
   */
  public String getOptionOurs() {
    return optionOurs;
  }

  /**
   * Method to get the option theirs.
   *
   * @return thries
   */
  public String getOptionTheirs() {
    return optionTheirs;
  }

  public int getConflictStartLine() {
    if (this.state != IndexDiff.StageState.BOTH_MODIFIED)
      return 0;
    return this.startLine;
  }

  /**
   * Get the length of the conflict. May return -1 if the whole file was deleted in one of the conflicting commits.
   *
   * @return length of the conflict in the file
   */
  public int getLength() {
    return length;
  }

  /**
   * may return null if {@link #isResolved()} returns false
   *
   * @return String representation of the current solution
   */
  public String getResult() {
    if (resolved && result == null)
      return "" + System.lineSeparator();

    return result;
  }

  /**
   * If the MergeConflict ist resolved.
   *
   * @return true if it is, false if not
   */
  public boolean isResolved() {
    return resolved;
  }

  /**
   * Method to get, if the ChangeConflict is deleted.
   *
   * @return true if it is, false if not
   */
  public boolean isDeleted() {
    if (!resolved) return false;
    else return deleted;
  }

  /**
   * Method to resolve the ChangeConflict.
   */
  public void resolve() {
    this.resolved = true;
  }
}
