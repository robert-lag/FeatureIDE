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
package de.ovgu.featureide.fm.ui.editors.elements;

import java.util.List;

import de.ovgu.featureide.fm.core.PluginID;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureStructure;
import de.ovgu.featureide.fm.core.io.APersistentFormat;
import de.ovgu.featureide.fm.ui.editors.IGraphicalFeatureModel;

/**
 * Converts the Feature Model Diagram in a tex-format using tikz.
 *
 * @author Simon Wenk
 * @author Yang Liu
 */
public class TikzFormat extends APersistentFormat<IGraphicalFeatureModel> {

	static boolean[] legend = { false, false, false, false, false, false, false };

	public static class TikZHead extends APersistentFormat<IGraphicalFeatureModel> {

		@Override
		public String write(IGraphicalFeatureModel object) {
			final StringBuilder str = new StringBuilder();

			printHead(str);

			return str.toString();
		}

		@Override
		public boolean supportsRead() {
			return false;

		}

		@Override
		public boolean supportsWrite() {
			return true;

		}

		@Override
		public String getSuffix() {
			return ".tex";
		}

		@Override
		public String getName() {
			return "LaTeX-Document with TikZ";
		}

		@Override
		public String getId() {
			return ID;
		}
	}

	public static class TikZBody extends APersistentFormat<IGraphicalFeatureModel> {

		private final String FileName;

		public TikZBody(String FileName) {
			this.FileName = FileName;
		}

		@Override
		public String write(IGraphicalFeatureModel object) {
			final StringBuilder str = new StringBuilder();

			printBody(str, FileName);

			return str.toString();
		}

		@Override
		public boolean supportsRead() {
			return false;

		}

		@Override
		public boolean supportsWrite() {
			return true;

		}

		@Override
		public String getSuffix() {
			return ".tex";
		}

		@Override
		public String getName() {
			return "LaTeX-Document with TikZ";
		}

		@Override
		public String getId() {
			return ID;
		}

	}

	public static class TikZMain extends APersistentFormat<IGraphicalFeatureModel> {

		@Override
		public String write(IGraphicalFeatureModel object) {
			StringBuilder str = new StringBuilder();

			str.append("\\begin{forest}\n	featureDiagram\n	");
			printTree(getRoot(object), object, str);
			str = postProcessing(str);
			str.append("	\n");
			printLegend(str);
			str.append("\n\\end{forest}");

			return str.toString();
		}

		@Override
		public boolean supportsRead() {
			return false;

		}

		@Override
		public boolean supportsWrite() {
			return true;

		}

		@Override
		public String getSuffix() {
			return ".tex";
		}

		@Override
		public String getName() {
			return "LaTeX-Document with TikZ";
		}

		@Override
		public String getId() {
			return ID;
		}

	}

	public static final String ID = PluginID.PLUGIN_ID + ".format.fm." + TikzFormat.class.getSimpleName();

	private static StringBuilder postProcessing(StringBuilder str) {
		final int strLength = str.length();
		StringBuilder newString = new StringBuilder();
		newString = newString.append(str);
		for (int i = 0, j = 0; i < strLength; ++i) {
			if (str.charAt(i) == '_') {
				newString.insert(i + j, '\\');
				++j;
			}
		}
		return newString;

	}

	private static void insertNodeHead(String node, IGraphicalFeatureModel object, StringBuilder str) {
		str.append("[" + node);
		if (object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().isAbstract() == true) {
			str.append(",abstract");
			legend[0] = true;
		}
		if (object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().isConcrete() == true) {
			str.append(",concrete");
			legend[1] = true;
		}
		if ((object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().isRoot() == false)
			&& (object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().getParent().isAnd() == true)) {
			if (object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().isMandatory() == true) {
				str.append(",mandatory");
				legend[2] = true;
			} else {
				str.append(",optional");
				legend[3] = true;
			}
		}
		if (object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().isRoot() == false) {
			if ((object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().getParent().isOr() == true)
				&& (object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().getParent().getFirstChild()
						.equals(object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure()) == true)) {
				str.append(",or");
				legend[4] = true;
			}
		}
		if (object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().isRoot() == false) {
			if ((object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().getParent().isAlternative() == true)
				&& (object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().getParent().getFirstChild()
						.equals(object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure()) == true)) {
				str.append(",alternative");
				legend[5] = true;
			}
		}
	}

	private static void insertNodeTail(StringBuilder str) {
		str.append("]");
	}

	private static void printTree(String node, IGraphicalFeatureModel object, StringBuilder str) {
		// PRE-OREDER TRAVERSEL
		final int numberOfChildren = object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().getChildrenCount();
		if ((numberOfChildren == 0)) {
			insertNodeHead(node, object, str);
			insertNodeTail(str);
		} else if (object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).isCollapsed()) {
			legend[6] = true;
			insertNodeHead(node, object, str);
			str.append("[,collapsed,edge label={node[hiddenNodes]{" + countNodes(node, object, -1) + "}}]");
			insertNodeTail(str);
		} else {
			insertNodeHead(node, object, str);
			final List<IFeatureStructure> nodesChildren =
				object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().getChildren();
			final Iterable<IFeatureStructure> myChildren = nodesChildren;
			for (final IFeatureStructure child : myChildren) {
				printTree(child.getFeature().getName(), object, str);
			}
			insertNodeTail(str);
		}
	}

	private static int countNodes(String node, IGraphicalFeatureModel object, int counter) {
		final int numberOfChildren = object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().getChildrenCount();
		if (numberOfChildren == 0) {
			return ++counter;
		} else {
			final List<IFeatureStructure> nodesChildren =
				object.getGraphicalFeature(object.getFeatureModel().getFeature(node)).getObject().getStructure().getChildren();
			final Iterable<IFeatureStructure> myChildren = nodesChildren;
			for (final IFeatureStructure child : myChildren) {
				counter = countNodes(child.getFeature().getName(), object, counter);

			}
			return ++counter;
		}
	}

	private static void printHead(StringBuilder str) {
		str.append("\\documentclass[border=5pt]{standalone}\n" + "%---required packages & variable definitions------------------------------------\n"
			+ "\\usepackage{forest}\n" + "\\usepackage{xcolor}\n" + "\\usetikzlibrary{angles}\n" + "\\definecolor{drawColor}{RGB}{128 128 128}\n"
			+ "\\newcommand{\\circleSize}{0.25em}\n" + "\\newcommand{\\angleSize}{0.8em}\n"
			+ "%-------------------------------------------------------------------------------\n"
			+ "%---Define the style of the tree------------------------------------------------\n" + "\\forestset{\n" + "	/tikz/mandatory/.style={\n"
			+ "		circle,fill=drawColor,\n" + "		draw=drawColor,\n" + "		inner sep=\\circleSize\n" + "	},\n" + "	/tikz/optional/.style={\n"
			+ "		circle,\n" + "		fill=white,\n" + "		draw=drawColor,\n" + "		inner sep=\\circleSize\n" + "	},\n"
			+ "	featureDiagram/.style={\n" + "		for tree={\n" + "			parent anchor = south,\n" + "			child anchor = north,\n"
			+ "			draw = drawColor,\n" + "			edge = {draw=drawColor},\n" + "		}\n" + "	},\n" + "	/tikz/abstract/.style={\n"
			+ "		fill = blue!85!cyan!5,\n" + "		draw = drawColor\n" + "	},\n" + "	/tikz/concrete/.style={\n" + "		fill = blue!85!cyan!20,\n"
			+ "		draw = drawColor\n" + "	},\n" + "	mandatory/.style={\n" + "		edge label={node [mandatory] {} }\n" + "	},\n"
			+ "	optional/.style={\n" + "		edge label={node [optional] {} }\n" + "	},\n" + "	or/.style={\n" + "		tikz+={\n"
			+ "			\\path (.parent) coordinate (A) -- (!u.children) coordinate (B) -- (!ul.parent) coordinate (C) pic[fill=drawColor, angle radius=\\angleSize]{angle};\n"
			+ "		}	\n" + "	},\n" + "	/tikz/or/.style={\n" + "	},\n" + "	alternative/.style={\n" + "		tikz+={\n"
			+ "			\\path (.parent) coordinate (A) -- (!u.children) coordinate (B) -- (!ul.parent) coordinate (C) pic[draw=drawColor, angle radius=\\angleSize]{angle};\n"
			+ "		}	\n" + "	},\n" + "	/tikz/alternative/.style={\n" + "	},\n" + "	/tikz/placeholder/.style={\n" + "	},\n"
			+ "	collapsed/.style={\n" + "		rounded corners,\n" + "		no edge,\n" + "		for tree={\n" + "			fill opacity=0,\n"
			+ "			draw opacity=0,\n" + "			l = 0em,\n" + "		}\n" + "	},\n" + "	/tikz/hiddenNodes/.style={\n" + "		midway,\n"
			+ "		rounded corners,\n" + "		draw=drawColor,\n" + "		fill=white,\n" + "		minimum size = 1.2em,\n"
			+ "		minimum width = 0.8em,\n" + "		scale=0.9\n" + "	},\n" + "}\n"
			+ "%-------------------------------------------------------------------------------\n");
	}

	private static void printBody(StringBuilder str, String FileName) {
		str.append("\\input{head.tex}\n"); // Include head
		str.append("\\begin{document}\n	");
		str.append("\\sffamily\n");
		str.append("	\\input{" + FileName + "}\n"); // Include main
		str.append("\\end{document}");
	}

	private static String getRoot(IGraphicalFeatureModel object) {
		final Iterable<IFeature> myList = object.getFeatureModel().getFeatures();
		String myRoot = null;

		for (final IFeature feature : myList) {
			if (object.getGraphicalFeature(object.getFeatureModel().getFeature(feature.getName())).getObject().getStructure().isRoot()) {
				myRoot = feature.getName();
				break;
			}
		}

		return myRoot;
	}

	private static void printLegend(StringBuilder str) {
		boolean check = false;
		final StringBuilder myString = new StringBuilder();
		if (legend[0]) {
			check = true;
			myString.append("		\\node [abstract,label=right:Abstract] {}; \\\\\n");
		}
		if (legend[1]) {
			check = true;
			myString.append("		\\node [concrete,label=right:Concrete] {}; \\\\\n");
		}
		if (legend[2]) {
			check = true;
			myString.append("		\\node [mandatory,label=right:Mandatory] {}; \\\\\n");
		}
		if (legend[3]) {
			check = true;
			myString.append("		\\node [optional,label=right:Optional] {}; \\\\\n");
		}
		if (legend[4]) {
			check = true;
			myString.append("		\\filldraw[drawColor] (0.45,0.15) ++ (225:0.3) arc[start angle=315,end angle=225,radius=0.2]; \n"
				+ "		\\node [or,label=right:Or] {}; \\\\\n");
		}
		if (legend[5]) {
			check = true;
			myString.append("		\\draw[drawColor] (0.45,0.15) ++ (225:0.3) arc[start angle=315,end angle=225,radius=0.2] -- cycle; \n"
				+ "		\\node [alternative,label=right:Alternative] {}; \\\\\n");
		}
		if (legend[6]) {
			check = true;
			myString.append("		\\node [hiddenNodes,label=center:1,label=right:Collapsed Nodes] {}; \\\\\n");
		}
		if (check) {
			str.append("	\\matrix [anchor=north west] at (current bounding box.north east) {\n" + "		\\node [placeholder] {}; \\\\\n" + "	};\n"
				+ "	\\matrix [draw=drawColor,anchor=north west] at (current bounding box.north east) {\n"
				+ "		\\node [label=center:\\underline{Legend:}] {}; \\\\\n");
			str.append(myString);
			str.append("	};");
		}
	}

	@Override
	public String write(IGraphicalFeatureModel object) {
		final StringBuilder str = new StringBuilder();
		printHead(str);
		str.append("\\begin{document}\n" + "	%---The Feature Diagram-----------------------------------------------------\n" + "	\\begin{forest}\n"
			+ "		featureDiagram\n");

		StringBuilder myTree = new StringBuilder();
		str.append("		");
		printTree(getRoot(object), object, myTree);
		myTree = postProcessing(myTree);
		str.append(myTree);
		str.append("\n");
		printLegend(str);
		str.append("	\\end{forest}\n" + "	%---------------------------------------------------------------------------\n" + "\\end{document}");
		return str.toString();
	}

	@Override
	public boolean supportsRead() {
		return false;

	}

	@Override
	public boolean supportsWrite() {
		return true;

	}

	@Override
	public String getSuffix() {
		return ".tex";
	}

	@Override
	public String getName() {
		return "LaTeX-Document with TikZ";
	}

	@Override
	public String getId() {
		return ID;
	}

}
