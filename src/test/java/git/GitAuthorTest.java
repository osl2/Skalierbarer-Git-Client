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
package git;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class GitAuthorTest {
  GitAuthor author;


  @BeforeEach
  void before() {
    author = new GitAuthor("name", "email");
  }

  @Test
  void equalsTest() {
    GitAuthor author2 = new GitAuthor("name", "email");
    assertEquals(author2, author);
    assertEquals(author, author2);
    assertEquals(author2, author2);
    assertEquals(author, author);
    author2 = new GitAuthor("name1", "email");
    assertNotEquals(author2, author);
    assertNotEquals(author, author2);
    int i = 4; // The most random integer.
    // Do no swap sides. assertNotEquals calls leftSide.equals(rightSide) internally!
    //noinspection AssertBetweenInconvertibleTypes
    assertNotEquals(author, i);
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
