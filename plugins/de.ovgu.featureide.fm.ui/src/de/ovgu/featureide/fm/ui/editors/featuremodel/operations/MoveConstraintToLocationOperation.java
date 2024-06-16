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

import static de.ovgu.featureide.fm.core.localization.StringTable.MOVE_CONSTRAINT;

import org.eclipse.draw2d.geometry.Point;

import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.event.FeatureIDEEvent;
import de.ovgu.featureide.fm.core.base.event.FeatureIDEEvent.EventType;
import de.ovgu.featureide.fm.ui.editors.IGraphicalFeatureModel;

import java.lang.*;

/**
 * Operation to move constraints to the new mouse position when manual layout is activated
 *
 * @author Joshua Sprey
 */
public class MoveConstraintToLocationOperation extends AbstractGraphicalFeatureModelOperation {

	private final Point newPos;
	private Point oldPos;
	private final int constraintIndex;

	public MoveConstraintToLocationOperation(IGraphicalFeatureModel graphicalFeatureModel, Point newPos, IConstraint constraint) {
		super(graphicalFeatureModel, MOVE_CONSTRAINT);

		if (featureModelManager.getSnapshot().getConstraints().contains(constraint)) {
			constraintIndex = featureModelManager.getSnapshot().getConstraintIndex(constraint);
		} else {
			// Add the number of constraints to the index as the visibility constraints are offset by them
			constraintIndex = featureModelManager.getSnapshot().getVisibilityConstraintIndex(constraint) +
					featureModelManager.getSnapshot().getConstraints().size();
		}
		this.newPos = newPos;
	}

	@Override
	protected FeatureIDEEvent operation(IFeatureModel featureModel) {
		final IConstraint constraint = featureModel.getAllConstraints().get(constraintIndex);
		if (featureModel.getConstraints().contains(constraint)) {
			oldPos = graphicalFeatureModel.getGraphicalConstraint(constraint).getLocation();
			graphicalFeatureModel.getGraphicalConstraint(constraint).setLocation(newPos);
		} else if (featureModel.getVisibilityConstraints().contains(constraint)) {
			oldPos = graphicalFeatureModel.getGraphicalVisibilityConstraint(constraint).getLocation();
			graphicalFeatureModel.getGraphicalVisibilityConstraint(constraint).setLocation(newPos);
		}
		return new FeatureIDEEvent(constraint, EventType.CONSTRAINT_MOVE_LOCATION, newPos, oldPos);
	}

	@Override
	protected FeatureIDEEvent inverseOperation(IFeatureModel featureModel) {
		final IConstraint constraint = featureModel.getAllConstraints().get(constraintIndex);
		if (featureModel.getConstraints().contains(constraint)) {
			graphicalFeatureModel.getGraphicalConstraint(constraint).setLocation(oldPos);
		} else if (featureModel.getVisibilityConstraints().contains(constraint)) {
			graphicalFeatureModel.getGraphicalVisibilityConstraint(constraint).setLocation(oldPos);
		}
		return new FeatureIDEEvent(constraint, EventType.CONSTRAINT_MOVE_LOCATION, oldPos, newPos);
	}

}
