package git;

import org.eclipse.jgit.api.Git;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class GitAuthorTest {
  GitAuthor author;



  @Before
  public void before (){
    author = new GitAuthor("name", "email");
  }

  @Test
  public void equalsTest (){
    GitAuthor author2 = new GitAuthor("name", "email");
    assertTrue (author.equals(author2));
    assertTrue(author2.equals(author));
    assertTrue(author2.equals(author2));
    assertTrue(author.equals(author));
    author2 = new GitAuthor("name1", "email");
    assertFalse(author.equals(author2));
    assertFalse(author2.equals(author));
    int i = new Random().ints().findFirst().getAsInt();
    assertFalse(author.equals(i));
  }

  @Test
  public void hashCodeTest (){
    GitAuthor author2 = new GitAuthor("name", "email");
    assertEquals(author.hashCode(), author2.hashCode());
  }

  @Test
  public void emailTest (){
    assertEquals("email", author.getEmail());
    author.setEmail("newMail");
    assertEquals("newMail", author.getEmail());
  }

  @Test
  public void nameTest (){
    assertEquals("name", author.getName());
    author.setName("newName");
    assertEquals("newName", author.getName());
  }
}
