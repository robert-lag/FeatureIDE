package de.ovgu.featureide.fm.ui.utils;

import java.util.ArrayList;
import java.util.Collection;

import de.ovgu.featureide.fm.core.configuration.SelectableFeature;
import de.ovgu.featureide.fm.ui.utils.TreeItemData;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * A wrapper around a TreeItem with the ability to hide itself
 *
 * @author Robert Lag
 */
public class TreeItemVisibilityWrapper {

	private boolean shouldBeVisible;
	private TreeItemData backupItemData;
	private TreeItem shownTreeItem;
	private final Tree parentTree;
	private final TreeItemVisibilityWrapper parent;
	private final Collection<TreeItemVisibilityWrapper> children;

	public TreeItemVisibilityWrapper(Tree parentTree, TreeItem treeItem) {
		this.parent = null;
		this.parentTree = parentTree;
		this.children = new ArrayList<TreeItemVisibilityWrapper>();
		this.shownTreeItem = treeItem;
		this.shouldBeVisible = true;
		createBackupFromTreeItem(treeItem);
	}

	public TreeItemVisibilityWrapper(TreeItemVisibilityWrapper parent, TreeItem treeItem) {
		this.parentTree = null;
		this.parent = parent;
		if (parent != null) {
			this.parent.children.add(this);
		}
		this.children = new ArrayList<TreeItemVisibilityWrapper>();
		this.shownTreeItem = treeItem;
		this.shouldBeVisible = true;
		createBackupFromTreeItem(treeItem);
	}

	public TreeItem getTreeItem() {
		return shownTreeItem;
	}

	/**
	 * Returns if this item is mandatory. If it is, then
	 * it is guaranteed to be always visible and checked.
	 * @return True, if this item is mandatory, otherwise False.
	 */
	public boolean isMandatory() {
		final SelectableFeature selectableFeature = (SelectableFeature) backupItemData.getData();
		return selectableFeature.getFeature().getStructure().isMandatory();
	}

	/**
	 * Returns if this item is visible. This also returns true,
	 * if this item is said to be visible but it's parent item isn't.
	 * @return True, if this item is visible, otherwise False.
	 */
	public boolean isVisible() {
		return shouldBeVisible;
	}

	/**
	 * Sets the visibility of this item. If it's parent isn't visible,
	 * it won't be displayed on the screen but it will remember this call
	 * for when its parent is set visible.
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		if (isVisible() == visible) {
			return;
		}
		shouldBeVisible = visible;

		if (visible) {
			// If the parent isn't visible, this item cannot be visible either
			if (((parent != null) && parent.isVisible()) || (parentTree != null)) {
				createShownTreeItemFromBackup();
				for (TreeItemVisibilityWrapper child : children) {
					if (child.shouldBeVisible) {
						child.createShownTreeItemFromBackup();
					}
				}
				shownTreeItem.setExpanded(true);
			}
		} else {
			shownTreeItem.dispose();
		}
	}

	public void refresh() {
		// By hiding and then re-showing the item it and its children
		// are newly created => they are refreshed
		// Non-visible items don't need to be refreshed
		if (isVisible()) {
			shownTreeItem.dispose();
			createShownTreeItemFromBackup();

			for (TreeItemVisibilityWrapper child : children) {
				child.refresh();
			}
		}
	}

	private void createShownTreeItemFromBackup() {
		if (parent != null) {
			shownTreeItem = new TreeItem(parent.getTreeItem(), 0);
		} else {
			shownTreeItem = new TreeItem(parentTree, 0);
		}
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
		if ((parent != null) && (parent.getTreeItem() != null) && !parent.getTreeItem().isDisposed()) {
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
