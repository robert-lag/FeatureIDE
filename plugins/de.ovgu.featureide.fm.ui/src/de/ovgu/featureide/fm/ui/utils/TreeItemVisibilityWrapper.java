package de.ovgu.featureide.fm.ui.utils;

import java.util.ArrayList;
import java.util.Collection;

import de.ovgu.featureide.fm.ui.utils.TreeItemData;

import org.eclipse.swt.widgets.TreeItem;

/**
 * A wrapper around a TreeItem with the ability to hide itself
 *
 * @author Robert Lag
 */
public class TreeItemVisibilityWrapper {

	private TreeItemData backupItemData;
	private TreeItem shownTreeItem;
	private final TreeItemVisibilityWrapper parent;
	private final Collection<TreeItemVisibilityWrapper> children;

	public TreeItemVisibilityWrapper(TreeItemVisibilityWrapper parent, TreeItem treeItem) {
		this.parent = parent;
		if (parent != null) {
			this.parent.children.add(this);
		}
		this.children = new ArrayList<TreeItemVisibilityWrapper>();

		this.shownTreeItem = treeItem;
		createBackupFromTreeItem(treeItem);
	}

	public TreeItem getTreeItem() {
		return shownTreeItem;
	}

	public boolean isVisible() {
		return (shownTreeItem != null) && (!shownTreeItem.isDisposed());
	}

	public void setVisible(boolean visible) {
		if (this.isVisible() == visible) {
			return;
		}

		if (visible) {
			createShownTreeItemFromBackup();
			for (TreeItemVisibilityWrapper child : children) {
				child.setVisible(true);
			}
		} else {
			shownTreeItem.dispose();
		}
	}

	private void createShownTreeItemFromBackup() {
		shownTreeItem = new TreeItem(parent.getTreeItem(), 0);
		updateShownItem();
	}

	public void updateShownItem() {
		shownTreeItem.setData(backupItemData.getData());
		shownTreeItem.setText(backupItemData.getText());
		shownTreeItem.setForeground(backupItemData.getForeground());
		shownTreeItem.setChecked(backupItemData.getChecked());
		shownTreeItem.setGrayed(backupItemData.getGrayed());
		shownTreeItem.setFont(backupItemData.getFont());
		shownTreeItem.setImage(backupItemData.getImage());
	}

	private void createBackupFromTreeItem(TreeItem treeItem) {
		backupItemData = new TreeItemData(treeItem.getParentItem());
		backupItemData.setData(treeItem.getData());
		backupItemData.setText(treeItem.getText());
		backupItemData.setForeground(treeItem.getForeground());
		backupItemData.setChecked(treeItem.getChecked());
		backupItemData.setGrayed(treeItem.getGrayed());
		backupItemData.setFont(treeItem.getFont());
		backupItemData.setImage(treeItem.getImage());
	}

	public void backupChanges() {
		backupItemData.setData(shownTreeItem.getData());
		backupItemData.setText(shownTreeItem.getText());
		backupItemData.setForeground(shownTreeItem.getForeground());
		backupItemData.setChecked(shownTreeItem.getChecked());
		backupItemData.setGrayed(shownTreeItem.getGrayed());
		backupItemData.setFont(shownTreeItem.getFont());
		backupItemData.setImage(shownTreeItem.getImage());
	}

	@Override
	public String toString() {
		String parentText = "<null>";
		if ((parent != null) && (parent.getTreeItem() != null)) {
			parentText = parent.getTreeItem().getText();
		}
		return "TreeItemVisibilityWrapper{" +
				"backupItemData=" + backupItemData +
				", shownTreeItem=" + shownTreeItem +
				", parentText=" + parentText +
				", visible=" + isVisible() +
				'}';
	}
}
