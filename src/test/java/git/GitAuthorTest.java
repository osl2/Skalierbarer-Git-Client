package git;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


class GitAuthorTest {
    GitAuthor author;


    @BeforeEach
    public void before() {
        author = new GitAuthor("name", "email");
    }

    @SuppressWarnings({"SimplifiableAssertion", "EqualsBetweenInconvertibleTypes", "EqualsWithItself"})
    @Test
    void equalsTest() {
        GitAuthor author2 = new GitAuthor("name", "email");
        assertTrue(author.equals(author2));
        assertTrue(author2.equals(author));
        assertTrue(author2.equals(author2));
        assertTrue(author.equals(author));
        author2 = new GitAuthor("name1", "email");
        assertFalse(author.equals(author2));
        assertFalse(author2.equals(author));
        @SuppressWarnings("OptionalGetWithoutIsPresent") int i = new Random().ints().findFirst().getAsInt();
        assertFalse(author.equals(i));
    }

    @Test
    void hashCodeTest() {
        GitAuthor author2 = new GitAuthor("name", "email");
        assertEquals(author.hashCode(), author2.hashCode());
    }

    @Test
    void emailTest() {
        assertEquals("email", author.getEmail());
        author.setEmail("newMail");
        assertEquals("newMail", author.getEmail());
    }

    @Test
    void nameTest() {
        assertEquals("name", author.getName());
        author.setName("newName");
        assertEquals("newName", author.getName());
    }
}
