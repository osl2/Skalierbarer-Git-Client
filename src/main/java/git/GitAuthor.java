package git;

public class GitAuthor {
  private String name;
  private String email;

  public GitAuthor(String name, String email) {
    this.name = name;
    this.email = email;
  }

  /* Is only instantiated inside the git Package */
  protected GitAuthor() {

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
