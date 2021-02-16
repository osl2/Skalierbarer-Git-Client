package views.addCommitView;



import javax.swing.*;
import java.awt.*;

/**
 * This class defines the renderer for the list of files with uncommitted changes that is located in the middle
 * panel of AddCommitView.
 * @see AddCommitView
 */
public class FileListRenderer implements ListCellRenderer<FileListItem> {
    /**
     * Method that returns a Component that defines the appearance of the list entries
     * @param list The corresponding list the renderer is set for
     * @param value The value of the list entry, held by the list model
     * @param index The index of the list entry in the list
     * @param isSelected Whether the entry has been selected.
     * @param cellHasFocus Whether the specific cell has focus
     * @return Component - in this case, a JCheckbox element
     */
    @Override
    public Component getListCellRendererComponent(JList<? extends FileListItem> list, FileListItem value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setEnabled(list.isEnabled());
        checkBox.setSelected(value.isSelected());
        checkBox.setFont(list.getFont());
        checkBox.setBackground(list.getBackground());
        checkBox.setForeground(list.getForeground());
        //TODO: consider another text
        checkBox.setText(value.getGitFile().getPath().getName());
        //TODO: focus does not appear. How to make focus visible?
        checkBox.setFocusPainted(cellHasFocus);
        return checkBox;
    }


}
