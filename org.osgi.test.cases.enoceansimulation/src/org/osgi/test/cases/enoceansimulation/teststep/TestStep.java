/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.test.cases.enoceansimulation.teststep;

/**
 * Test stepper interface used by OSGi test cases for manual test steps. The
 * implementation of this interface must be registered in the OSGi service
 * registry.
 * 
 * @author $Id$
 */
public interface TestStep {

	// /**
	// * Executes the command with the given parameters.
	// *
	// * @param command The command to execute.
	// * @param parameters The command parameters.
	// *
	// * @return The execution result.
	// */
	// public String[] execute(String command, String[] parameters);

	/**
	 * Executes the command with the given parameters, i.e. the Conformance Test
	 * call TestStep in order to request for the execution of a command by the
	 * StepService.
	 * 
	 * The TestStep can receive data from the Reference Implementation (or any
	 * spec implementation). This execute method also enables the Conformance
	 * Test to get these data.
	 * 
	 * @param command The command to be executed by the Reference
	 *        Implementation, or by another implementation.
	 * 
	 *        In EnOcean, this command will most of the time be transformed into
	 *        an EnOcean message, e.g. EspRadioPacket, by the TestStep
	 *        implementation.
	 * 
	 * @param parameters The parameters to be used when executing the command.
	 * 
	 *        In EnOcean, parameters[0] contains the EnOcean message, if any.
	 * 
	 * @return The execution result.
	 */
	public String[] execute(String command, String[] parameters);

}
