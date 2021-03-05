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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Test Step Proxy is a wrapper over {@code TestStep} OSGi service. The wrapper
 * forwards the executions to the service, when it's available. If the service
 * is missing, "standard" output and input streams are used.
 */
public class TestStepProxy implements TestStep {

	private final ServiceTracker<TestStep, TestStep>	testStepTracker;

	/**
	 * Constructs a new proxy instance and starts to track the {@code TestStep}
	 * service.
	 * 
	 * @param bc The bundle context is used to track {@code TestStep} service.
	 */
	public TestStepProxy(BundleContext bc) {
		this.testStepTracker = new ServiceTracker<TestStep, TestStep>(bc, TestStep.class, null);
		this.testStepTracker.open();
	}

	/**
	 * The method forwards the execution to
	 * {@code TestStep#execute(String, String)} method when the service is
	 * available. If the service is missing, "standard" output and input streams
	 * are used.
	 * 
	 * @param stepId The step identifier.
	 * @param userPrompt The step human readable message.
	 * @return The execution result.
	 * 
	 * @see org.osgi.test.support.step.TestStep#execute(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public String execute(String stepId, String userPrompt) {
		TestStep testStep = this.testStepTracker.getService();
		if (null != testStep) {
			return testStep.execute(stepId, userPrompt);
		}
		return executeOnConsole(stepId, userPrompt);
	}

	/**
	 * Releases the proxy allocated resources.
	 */
	public void close() {
		this.testStepTracker.close();
	}

	private static String executeOnConsole(String stepId, String userPrompt) {
		System.out.println('[' + stepId + "] " + userPrompt + " (press enter to confirm...):");
		BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
		String answer = null;
		try {
			answer = consoleIn.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (null != answer) {
			answer = answer.trim();
		}
		return answer;
	}

}
