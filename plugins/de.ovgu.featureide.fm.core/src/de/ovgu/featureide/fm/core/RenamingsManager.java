/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2019  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.fm.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.prop4j.Literal;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.event.DefaultEventManager;
import de.ovgu.featureide.fm.core.base.event.FeatureIDEEvent;
import de.ovgu.featureide.fm.core.base.event.FeatureIDEEvent.EventType;
import de.ovgu.featureide.fm.core.base.event.IEventListener;
import de.ovgu.featureide.fm.core.base.event.IEventManager;
import de.ovgu.featureide.fm.core.functional.Functional;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.manager.IFeatureModelManager;

/**
 * Handles feature renamings.
 *
 * @author Jens Meinicke
 * @author Marcus Pinnecke (Feature Interface)
 */
public class RenamingsManager implements IEventManager, Cloneable {

	/**
	 * a list containing all renamings since the last save
	 */
	private final List<Renaming> renamings = new LinkedList<>();
	private final IFeatureModel model;

	private final DefaultEventManager eventManager = new DefaultEventManager();

	public RenamingsManager(IFeatureModel model) {
		this.model = model;
	}

	protected RenamingsManager(RenamingsManager renamingsManager) {
		model = renamingsManager.model;
		renamings.addAll(renamingsManager.renamings);
	}

	/**
	 * @param renamingsManager
	 * @param oldModel
	 */
	protected RenamingsManager(RenamingsManager renamingsManager, IFeatureModel newModel) {
		model = newModel;
		renamings.addAll(renamingsManager.renamings);
	}

	public boolean renameFeature(final String oldName, final String newName) {
		final Map<String, IFeature> featureTable = model.getFeatureTable();
		if (!featureTable.containsKey(oldName) || featureTable.containsKey(newName)) {
			return false;
		}
		final List<IConstraint> constraints = model.getAllConstraints();
		final IFeature feature = model.getFeature(oldName);
		model.deleteFeatureFromTable(feature);
		feature.setName(newName);
		model.addFeature(feature);
		renamings.add(new Renaming(oldName, newName));
		for (final IConstraint c : constraints) {
			renameVariables(c.getNode(), oldName, newName);
		}

		// update the feature order list

		final List<String> featureOrderList = Functional.toList(model.getFeatureOrderList());
		for (int i = 0; i < featureOrderList.size(); i++) {
			if (featureOrderList.get(i).equals(oldName)) {
				model.setFeatureOrderListItem(i, newName);
				break;
			}
		}
		return true;
	}

	public boolean isRenamed() {
		return (!renamings.isEmpty());
	}

	public void notifyAboutRenamings() {
		final IFeatureModelManager instance = FeatureModelManager.getInstance(model);
		for (final Renaming renaming : renamings) {
			instance.fireEvent(new FeatureIDEEvent(model, EventType.FEATURE_NAME_PERSISTENTLY_CHANGED, renaming.oldName, renaming.newName));
		}
		renamings.clear();
	}

	public List<Renaming> getRenamings() {
		return new ArrayList<>(renamings);
	}

	private void renameVariables(Node node, String oldName, String newName) {
		if (node instanceof Literal) {
			if (oldName.equals(((Literal) node).var)) {
				((Literal) node).var = newName;
			}
			return;
		}

		for (final Node child : node.getChildren()) {
			renameVariables(child, oldName, newName);
		}
	}

	/**
	 * Returns the current name of a feature given its name at the last save.
	 *
	 * @param name name when last saved
	 * @return current name of this feature
	 */
	public String getNewName(String name) {
		for (final Renaming renaming : renamings) {
			if (renaming.oldName.equals(name)) {
				return renaming.newName;
			}
		}
		return name;
	}

	/**
	 * Returns the name of a feature at the time of the last save given its current name.
	 *
	 * @param name current name of a feature
	 * @return name when last saved
	 */
	public String getOldName(String name) {
		for (final Renaming renaming : renamings) {
			if (renaming.newName.equals(name)) {
				return renaming.oldName;
			}
		}
		return name;
	}

	public Set<String> getOldFeatureNames() {
		final HashSet<String> names = new HashSet<>(model.getFeatureTable().keySet());
		for (final Renaming renaming : renamings) {
			names.remove(renaming.newName);
			names.add(renaming.oldName);
		}
		return names;
	}

	public void clear() {
		renamings.clear();
	}

	@Override
	public void addListener(IEventListener listener) {
		eventManager.addListener(listener);
	}

	@Override
	public List<IEventListener> getListeners() {
		return eventManager.getListeners();
	}

	@Override
	public void fireEvent(FeatureIDEEvent event) {
		eventManager.fireEvent(event);
	}

	@Override
	public void removeListener(IEventListener listener) {
		eventManager.removeListener(listener);
	}

	@Override
	public RenamingsManager clone() {
		return new RenamingsManager(this);
	}

	public RenamingsManager clone(IFeatureModel newModel) {
		return new RenamingsManager(this, newModel);
	}

}
