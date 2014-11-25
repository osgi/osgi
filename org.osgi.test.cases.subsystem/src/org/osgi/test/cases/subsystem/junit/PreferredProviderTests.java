/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.subsystem.junit;

import org.osgi.framework.Bundle;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.service.subsystem.SubsystemException;
import org.osgi.test.cases.subsystem.util.SubsystemBuilder;

public class PreferredProviderTests extends SubsystemTest {
	/*
	 * This test was inspired by member bug 2517 and rfc 201, section 3.6.
	 * 
	 * Root Subsystem
	 * 
	 * 		Bundle A - Export-Package: foo,bar;uses:=foo
	 * 
	 * 		Application A
	 * 
	 * 			Bundle B - Export-Package: foo
	 * 
	 * 			Bundle C - Import-Package: foo,bar
	 * 
	 * In Subsystems 1.0, a uses constraint violation would always result 
	 * from the above scenario, even if Bundle A was listed as a 
	 * Preferred-Provider. Content resources were strictly favored (i.e. if a 
	 * matching capability was found in the Content Repository, the search had
	 * to stop, so the resolver was not given Bundle A as an option), so Bundle 
	 * C would always get wired to Bundle B for package foo. Since the only 
	 * provider of package bar is Bundle A, a uses constraint violation 
	 * resulted. In 1.1, implementations must now provide matching capabilities,
	 * in the appropriate order, from all defined repositories, with repository
	 * services being optional.
	 */
	public void testPreferredProviderSolvesUsesConstraintViolation() throws Exception {
		String symbolicNameA = "preferred.provider.a";
		String symbolicNameB = "preferred.provider.b";
		String symbolicNameC = "preferred.provider.c";
		Subsystem root = getRootSubsystem();
		Bundle bundle = doBundleInstall(symbolicNameA, root.getBundleContext(), null, symbolicNameA + ".jar", false);
		doBundleOperation(symbolicNameA, bundle, Operation.START, false);
		Subsystem subsystem = null;
		try {
			subsystem = root.install(
					getName(),
					new SubsystemBuilder(getContext())
							.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, getName())
							.header(SubsystemConstants.SUBSYSTEM_CONTENT, symbolicNameB + ',' + symbolicNameC)
							.bundle(symbolicNameB + ".jar")
							.bundle(symbolicNameC + ".jar")
							.header(SubsystemConstants.PREFERRED_PROVIDER, symbolicNameA)
							.build());
			subsystem.start();
		}
		catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have installed and started because the preferred provider repository offered a solution to the uses constraint violation");
		}
		finally {
			if (subsystem != null) {
				doSubsystemOperation(getName(), subsystem, Operation.UNINSTALL, false);
			}
		}
	}
}
