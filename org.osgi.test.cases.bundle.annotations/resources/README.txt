This test case requires that a bundle be created using the source code in 
OSGI-INF/impl-src. This bundle must have the Declarative Services 
annotations processed into component description XML which is referenced
by the bundle's Service-Component manifest header.

The -runbundles of the test case bnd file must be modified to list the created 
bundle and the org.osgi.test.cases.component.annotations.bundle.symbolic.name
property must be modified in -runproperties to the Bundle Symbolic Name
of the created bundle. 

The test case will use the value of the property to locate the bundle
and then examine the generated component description XML to validate
that the Declarative Services annotations in the source code were properly
mapped to the component description XML.