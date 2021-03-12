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
package dialogviews;

import javax.swing.*;
import java.awt.*;

public class FindComponents {

  /**
   * This method can be used to find a child component specified by its getName() method.
   *
   * @param parent the parent Component.
   * @param child  the name of the Component you want to find.
   * @return a Component with the specified name as the output of its getName() method.
   */
  public static Component getChildByName(Component parent, String child) {
    if (child.equals(parent.getName())) {
      return parent;
    }
    if (parent instanceof Container) {
      Component[] allChildren = (parent instanceof JMenu) ? ((JMenu) parent).getMenuComponents() : ((Container) parent).getComponents();

      for (Component allChild : allChildren) {
        Component children = getChildByName(allChild, child);
        if (children != null) {
          return children;
        }
      }
    }
    return null;
  }
}
