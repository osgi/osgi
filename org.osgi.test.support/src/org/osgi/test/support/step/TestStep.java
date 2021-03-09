/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.support.step;

/**
 * The test step interface is used by the OSGi test cases for the manual test
 * steps execution. The test case is allowed to execute manual steps with the
 * step identifier and human readable message. If the step requires some input,
 * an execution result can be provided.
 * <p>
 * The implementation of this interface must be registered in the OSGi service
 * registry before the test case execution. It's strongly recommended that the
 * {@code TestStep} service will be registered in the start method of the bundle
 * activator, otherwise some test steps can be missed.
 */
public interface TestStep {

	/**
	 * Executes the command with the given parameters. Executes the test step
	 * with the given identifier and human readable message.
	 * <p>
	 * For example, it can be called in this way:
	 * {@code testStep.execute("step.1", "Add a new device to the network");}
	 * 
	 * @param stepId The step identifier.
	 * @param userPrompt The step human readable message.
	 * 
	 * @return The execution result.
	 */
	public String execute(String stepId, String userPrompt);

}
