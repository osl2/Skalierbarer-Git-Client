package git;

import java.util.List;

public class GitRemote {
  private String url;
  private String gitUser;
  private String name;
  private List<GitBranch> branches;


  /* Is only instantiated inside the git Package */
  protected GitRemote() {
  }

}
