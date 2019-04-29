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
package de.ovgu.featureide.ui.actions.generator.configuration;

import java.util.List;

import de.ovgu.featureide.fm.core.analysis.cnf.CNF;
import de.ovgu.featureide.fm.core.analysis.cnf.ClauseList;
import de.ovgu.featureide.fm.core.analysis.cnf.LiteralSet;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.IConfigurationGenerator;
import de.ovgu.featureide.ui.actions.generator.ConfigurationBuilder;

/**
 * Generates a configuration containing the given feature and a configuration without it.
 *
 * @author Jens Meinicke
 * @author Sebastian Krieter
 */
public class ModuleConfigurationGenerator extends ACNFConfigurationGenerator {

	private final String featureName;

	public ModuleConfigurationGenerator(ConfigurationBuilder builder, FeatureModelFormula formula, String featureName) {
		super(builder, formula);
		this.featureName = featureName;
	}

	@Override
	protected IConfigurationGenerator getGenerator(CNF cnf, int numberOfConfigurations) {
		final int featureVariable = cnf.getVariables().getVariable(featureName);
		final List<List<ClauseList>> expressions = de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.TWiseConfigurationGenerator
				.convertLiterals(new LiteralSet(featureVariable, -featureVariable));
		return new de.ovgu.featureide.fm.core.analysis.cnf.generator.configuration.TWiseConfigurationGenerator(cnf, 2, 1, expressions);
	}

}
