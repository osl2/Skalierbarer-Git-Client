package git;

import org.eclipse.jgit.api.Git;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GitAuthorTest {
  GitAuthor author;

  @Before
  void before (){
    author = new GitAuthor("name", "email");
  }

  @Test
  void equalsTest (){
    GitAuthor author2 = new GitAuthor("name", "email");
    assertTrue (author.equals(author2));
  }

  @Test
  void hashCodeTest (){
    GitAuthor author2 = new GitAuthor("name", "email");
    assertEquals(author.hashCode(), author2.hashCode());
  }

  @Test
  void emailTest (){
    assertEquals("email", author.getEmail());
    author.setEmail("newMail");
    assertEquals("newMail", author.getEmail());
  }

  @Test
  void nameTest (){
    assertEquals("name", author.getName());
    author.setName("newName");
    assertEquals("newName", author.getName());
  }
}
