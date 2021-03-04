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
  public Component getChildByName(Component parent, String child) {
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
