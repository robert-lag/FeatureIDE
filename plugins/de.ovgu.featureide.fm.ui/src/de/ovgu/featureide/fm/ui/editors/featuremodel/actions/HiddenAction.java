/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2017  FeatureIDE team, University of Magdeburg, Germany
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
package de.ovgu.featureide.fm.ui.editors.featuremodel.actions;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.manager.IFeatureModelManager;
import de.ovgu.featureide.fm.ui.editors.featuremodel.operations.FeatureModelOperationWrapper;
import de.ovgu.featureide.fm.ui.editors.featuremodel.operations.SetFeatureToHiddenOperation;

/**
 * Action to mark a feature as hidden.
 *
 * @author Marcus Pinnecke (Feature Interface)
 * @author Chico Sundermann
 * @author Paul Westphal
 */
public class HiddenAction extends MultipleSelectionAction {

	public static final String ID = "de.ovgu.featureide.hidden";

	public HiddenAction(Object viewer, IFeatureModelManager featureModelManager) {
		super("Hidden", viewer, ID, featureModelManager);
	}

	@Override
	public void run() {
		changeHiddenStatus(isEveryFeatureHidden());
		setChecked(isEveryFeatureHidden());
	}

	private boolean isEveryFeatureHidden() {
		final IFeatureModel featureModel = featureModelManager.editObject();
		for (final String name : featureArray) {
			final IFeature tempFeature = featureModel.getFeature(name);
			if (!(tempFeature.getStructure().isHidden())) {
				return false;
			}
		}
		return true;
	}

	private void changeHiddenStatus(boolean allHidden) {
		FeatureModelOperationWrapper.run(new SetFeatureToHiddenOperation(featureModelManager, allHidden, getSelectedFeatures()));
	}

	@Override
	protected void updateProperties() {
		setEnabled(true);
		// A selection of features is considered hidden if every feature is hidden.
		setChecked(isEveryFeatureHidden());
	}

}
