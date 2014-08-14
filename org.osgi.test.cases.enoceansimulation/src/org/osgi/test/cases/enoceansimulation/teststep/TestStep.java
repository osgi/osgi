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
	 * Executes the given EspRadioPacket, i.e. the Conformance Test can call
	 * TestStep in order to request for the execution of a command/message by
	 * the StepService.
	 * 
	 * @param command The command/message (here, in EnOcean, an EspRadioPacket)
	 *        to execute.
	 * 
	 * @return The execution result.
	 */
	public void execute(byte[] command);
	
	/**
	 * The TestStep can receive data/command/message from the StepService. This
	 * method enables the Conformance Test to get these data. Once data has been
	 * retrieved, it is removed from the TestStep.
	 * 
	 * @return all the data.
	 */
	public byte[] getCurrentData();

}
