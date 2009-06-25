/* FeatureIDE - An IDE to support feature-oriented software development
 * Copyright (C) 2005-2009  FeatureIDE Team, University of Magdeburg
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */
package featureide.ui.wizards;

import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import featureide.core.CorePlugin;
import featureide.fm.ui.editors.FeatureModelEditor;
import featureide.ui.UIPlugin;

/**
 * A creation wizard for Jak projects that adds the Jak nature after creation.
 * 
 * @author Marcus Leich
 * @author Thomas Th�m
 * @author Tom Brosch
 * @author Janet Feigenspan
 */
public class NewFeatureProjectWizard extends BasicNewProjectResourceWizard {

	public static final String ID = UIPlugin.PLUGIN_ID + ".FeatureProjectWizard";
	
	private NewFeatureProjectPage page;
	
	@Override
	public void addPages() {
		page = new NewFeatureProjectPage();
		addPage(page);
		super.addPages();
	}

	public boolean performFinish() {
		if (!super.performFinish())
			return false;
		if (page.hasCompositionTool()) {
			CorePlugin.setupFeatureProject(getNewProject(), page.getCompositionTool().getId());
			UIPlugin.getDefault().openEditor(FeatureModelEditor.ID, getNewProject().getFile("model.m"));
		}
		return true;
	}

}
