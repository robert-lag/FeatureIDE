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
package de.ovgu.featureide.fm.ui.editors.featuremodel.operations;

import static de.ovgu.featureide.fm.core.localization.StringTable.CREATE_CONSTRAINT;

import org.prop4j.Node;
import org.prop4j.VisibleIf;

import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.event.FeatureIDEEvent;
import de.ovgu.featureide.fm.core.base.event.FeatureIDEEvent.EventType;
import de.ovgu.featureide.fm.core.base.impl.FMFactoryManager;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.manager.IFeatureModelManager;

/**
 * Operation with functionality to create a new constraint. Enables undo/redo functionality.
 *
 * @author Fabian Benduhn
 * @author Marcus Pinnecke
 * @author Marlen Bernier
 * @author Dawid Szczepanski
 */
public class CreateConstraintOperation extends AbstractFeatureModelOperation {

	private final Node node;
	private final String description;
	private final boolean isVisibilityConstraint;

	private int constraintCount = -1;

	/**
	 * @param node the node representing the constraint to be added
	 * @param featureModel model that will be used to add the constraint
	 * @param description description
	 */
	public CreateConstraintOperation(Node node, IFeatureModelManager featureModelManager, String description) {
		super(featureModelManager, CREATE_CONSTRAINT);
		this.node = node;
		this.description = description;
		this.isVisibilityConstraint = node instanceof VisibleIf;
	}

	@Override
	protected FeatureIDEEvent operation(IFeatureModel featureModel) {
		final IConstraint constraint = FMFactoryManager.getInstance().getFactory(featureModel).createConstraint(featureModel, node);
		constraint.setDescription(description);

		if (isVisibilityConstraint) {
			constraintCount = featureModel.getVisibilityConstraintCount();
			featureModel.addVisibilityConstraint(constraint, constraintCount);
		} else {
			constraintCount = featureModel.getConstraintCount();
			featureModel.addConstraint(constraint, constraintCount);
		}
		return new FeatureIDEEvent(featureModel, EventType.CONSTRAINT_ADD, null, constraint);
	}

	@Override
	protected FeatureIDEEvent inverseOperation(IFeatureModel featureModel) {
		final IConstraint constraint;
		if (isVisibilityConstraint) {
			constraint = featureModel.getVisibilityConstraints().get(constraintCount);
			featureModel.removeVisibilityConstraint(constraintCount);
		} else {
			constraint = featureModel.getConstraints().get(constraintCount);
			featureModel.removeConstraint(constraintCount);
		}
		return new FeatureIDEEvent(featureModel, EventType.CONSTRAINT_DELETE, constraint, null);
	}

	@Override
	protected int getChangeIndicator() {
		return FeatureModelManager.CHANGE_DEPENDENCIES;
	}

}
