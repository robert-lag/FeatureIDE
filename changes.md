# Feature IDE with Visibility Constraints

Following is a summary of the improvements I made to the Feature IDE, from a
user and a technical perspective:

## New features

### Visibility constraints

When using the Feature IDE to work with UVL models you can easily be overwhelmed
in the configurator as there could be thousands of features you could enable or
disable.

To alleviate this problem the UVL language was extended, so you can specify
which features should be visible at which point in time. This addition was
called 'visibility constraints'. Their definition looks similar to regular
constraints but they serve a different purpose. While regular constraints define
what combinations of features are allowed for the model, visibility constraints
only concern themselves with under what configuration feature choices should be
visible.

The new constraints are defined in the `*.uvl` file under the
`visibility-constraints` section. A constraint definition looks something
like this:

~~~
<feature-name> vif <logical-expression>
~~~

On the left side of the `vif` (= 'visible if') operator a feature name is
written and on the right side a logical expression just like regular
constraints.

For example:

~~~
A vif B & C
~~~

This visibility constraint would mean that the feature A is only shown if the
features B and C are both enabled in the configurator.

Apart from the `visibility-constraints` section, visibility constraints can also
be defined as attributes of a specific feature. Attributes in UVL are defined
in curly braces on the right of the feature name.

Here is an example of a visibility constraint as an attribute of the feature F:

~~~
F {visibility-constraint F vif A}
~~~

Here is a full example of a `*.uvl` file with visibility constraints:

~~~
features
    A
        mandatory
            B
                or
                    C
                    D
                    E
            F
                alternative
                    G
                    H {visibility-constraint H vif E}

constraints
    A & B

visibility-constraints
    F vif C | D
~~~

### Feature Diagram

In the feature diagram in the FeatureIDE you now have the option to show or hide
the visibility constraints using the context action `Show Visibility
Constraints`.

It was also made possible to add, edit and remove visibility constraints with
the respective context actions in this diagram.

## Implementation

### UVL parser

The UVL parser is written in ANTLR4, a tool that's used to easily describe the
grammatics of a particular language (in our case UVL) and generate equivalent
code for parsing it in a programming language (e.g. java).

It also contains some wrapper classes around the generated code from ANTLR4,
which makes it easier for the FeatureIDE to interact with it.

#### ANTLR4 code

To make it possible that the parser can detect the visibility constraints, I
adjusted the file `/uvl/UVLBase.g4` which contains the grammatical definition
of the UVL language. Those changes included the definition of a few
non-terminals (e.g. `visibilityConstraints`, `visibilityConstraintLine`, etc.)
and their correct integration with existing ones, so that the parser can detect
the new `visibility-constraints` section in UVL files.

#### Wrapper code

The wrapper code is located in `java/scr/main/de/vill/`.

There, a new constraint was added under
`java/scr/main/de/vill/model/constraint/VisibleIfConstraint.java`. This
constraint represents the `vif` operation.

To use this new operator and the additions to the ANTLR4 code, the
files `java/scr/main/de/vill/main/UVLListener.java` and
`java/scr/main/de/vill/model/FeatureModel.java` had to be adjusted. The latter
is responsible for representing the current feature model and the former is used
to fill this feature model with data from the `*.uvl` file.

### FeatureIDE

#### General

The source code of the FeatureIDE is split between many different projects. The
most important ones for my changes were `de.ovgu.featureide.fm.core` and
`de.ovgu.featureide.fm.ui`.

The implementation of the visibility constraints is very similar from the normal
constraints as both are represented by logical statements, so much of the code
concerning these 2 looks very similar. The difference between those 2 though is
how they are handled by the program. Whereas the normal constraints are used to
see if the model is satisfiable, the visibility constraints are used to set the
the visibility of features in the configuration editor.

#### Parsing of VisibleIfConstraints

In the FeatureIDE the constraints are parsed into nodes
(`org.prop4j.Node`), that can be used to evaluate boolean expressions. This
is done in the class `UVLFeatureModelFormat`.

To make the parsing possible with VisibilityConstraints a new Subclass of `Node`
was created called `VisibleIf`. A specialty of `VisibleIf` nodes is that they
can only be used as a outermost operator in a boolean expression and it's left
operand is always a `Literal` (as per the specification of visibility
constraints). Every expression whose outermost node has the `VisibleIf` is
recognized as a visibility constraint.

#### Storing visibility constraints

The visibility constraints had to be stored accessed and stored in many
different classes, which led to major changes to e.g. `MultiFeatureModel.java`,
`FeatureModel.java` and `GraphicalFeatureModel.java`.

#### Hiding features in the configuration editor

Now that the constraints were parsed and passed around different classes, the
constraints still needed to be evaluated and depending on the result specific
features hidden in the configuration editor.

This was done in the class `ConfigurationTreeEditorPage` in the function
`updateVisibilityOfAllItems()`.

To be able to actually hide the features the class `TreeItem` had to be wrapped
by a new class I called `TreeItemVisibilityWrapper`. This class adds the
functionality of hiding and re-showing a tree item.

