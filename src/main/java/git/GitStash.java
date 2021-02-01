package git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import settings.Settings;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GitStash {

  private final List<GitFile> changes;
  private final String id;
  private final Date date;

  GitStash(List<GitFile> changes, String id, Date date) {
    this.changes = changes;
    this.id = id;
    this.date = date;
  }

  GitStash(RevCommit revCommit) {
    RevCommit[] parents = revCommit.getParents();
    RevCommit parent = parents[0];
    this.id = revCommit.getName();
    int stashTime = revCommit.getCommitTime();
    Instant instant = Instant.ofEpochSecond(stashTime);
    this.date = Date.from(instant);

    Repository repository;
    Git git;
    File path = Settings.getInstance().getActiveRepositoryPath();
    List<GitFile> changedFiles = new ArrayList<>();
    try {
      FileRepositoryBuilder builder = new FileRepositoryBuilder();
      builder.setMustExist(true);
      if (path.getAbsolutePath().matches(".*/.git$")) {
        builder.setGitDir(path);
      } else {
        builder.setWorkTree(path);
      }
      //builder.setGitDir(path);
      repository = builder.build();
      git = Git.open(path);

      ObjectId commit1 = revCommit.getId();
      ObjectId commit2 = parent.getId();

      ObjectReader reader = repository.newObjectReader();

      CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
      oldTreeIter.reset(reader, commit1);
      CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
      newTreeIter.reset(reader, commit2);

      List<DiffEntry> diffs = git.diff().setOldTree(oldTreeIter).setNewTree(newTreeIter).call();

      for (DiffEntry diff : diffs){
        File changed = new File(diff.getPath(DiffEntry.Side.OLD));
        int size = (int) changed.getTotalSpace();
        changedFiles.add(new GitFile(size, changed));
      }

    } catch (IOException | GitAPIException e) {
      e.printStackTrace();
      List<GitFile> changes = new ArrayList<>();
    }
    changes = changedFiles;
  }

  public List<GitFile> getChanges() {
    return changes;
  }

  public String getId() {
    return id;
  }

  /**
   * Restore stashed data to working directory
   *
   * @return true iff successful
   */
  public boolean apply() {
    return false;
  }


}
