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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a single author in Git.
 */
public class GitAuthor {
  private String name;
  private String email;


  /* Is only instantiated inside the git Package */

  /**
   * Method to generate an Author by name and email.
   *
   * @param name  Name of the author
   * @param email email of the author
   */
  @JsonCreator
  GitAuthor(
      @JsonProperty("name") String name,
      @JsonProperty("email") String email) {
    this.name = name;
    this.email = email;
  }

  /**
   * Method to get the name of the author.
   *
   * @return Name of the author
   */
  public String getName() {
    return name;
  }

  /**
   * Method to set the name of an author.
   *
   * @param name name, that the author should get
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Method to get the email of an author.
   *
   * @return Email that the author has
   */
  public String getEmail() {
    return email;
  }

  /**
   * Method to set the email of an author.
   *
   * @param email email that the author should get
   */
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GitAuthor)) return false;
    GitAuthor gitAuthor = (GitAuthor) o;
    return (name.equals(gitAuthor.name) && email.equals(gitAuthor.email));
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, email);
  }
}
