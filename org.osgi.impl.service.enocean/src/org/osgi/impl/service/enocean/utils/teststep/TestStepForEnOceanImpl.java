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

package org.osgi.impl.service.enocean.utils.teststep;

import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.test.support.step.TestStep;

/**
 * An implementation of the test step based on RI simulator. This implementation
 * is to be registered in the OSGi service registry.
 */
public class TestStepForEnOceanImpl implements TestStep {

    static public final String PLUG_DONGLE = "Plug the EnOcean USB dongle";
    static public final String MSG_EXAMPLE_1A = "MessageExample1_A";
    static public final String MSG_EXAMPLE_1B = "MessageExample1_B";
    static public final String MSG_EXAMPLE_2 = "MessageExample2_";
    static public final String MDSET_WITH_MD = "EnOceanMessageDescriptionSet_with_an_EnOceanMessageDescription";
    static public final String CDSET_WITH_CD = "EnOceanChannelDescriptionSet_with_an_EnOceanChannelDescription_CID";
    static public final String GET_EVENT = "Get_the_event_that_the_base_driver_should_have_received";

    static private final String TAG = "TestStepForEnOceanImpl";

    private byte[] currentCommand;
    private byte[] currentData;

    /**
     * It is possible to change this constructor in order to add some parameters
     * to specify simulators.
     */
    public TestStepForEnOceanImpl() {

    }

    /**
     * Get current command and replace it by null.
     * 
     * @return the current command.
     */
    public byte[] getCurrentCommandAndReplaceItByNull() {
	Logger.d(TAG, 
		"getCurrentCommandAndReplaceItByNull() returns currentCommand: " + currentCommand);
	byte[] result = currentCommand;
	currentCommand = null;
	return result;
    }

    /**
     * Push data in test step.
     * 
     * @param data
     */
    public void pushDataInTestStep(byte[] data) {
	Logger.d(TAG, "pushDataInTestStep: " + Utils.bytesToHexString(data));
	currentData = data;
    }

    @Override
	public String execute(String stepId, String userPrompt) {
	Logger.d(TAG, "execute the stepId: " + stepId + ", userPrompt: "
		+ userPrompt);
	if (stepId == null) {
	    Logger.e(TAG, "The given stepId is null, but it can NOT be.");
	    return null;
	}
	if (stepId.equals(MSG_EXAMPLE_1A)) {
	    // Teach in telegram, see in test.cases.enocean
	    // MessageExample1.generateTeachInMsg(Fixtures.HOST_ID, Fixtures.MANUFACTURER)
	    byte[] teachInTelegram = { 85, 0, 10, 7, 1, -21, -91, 8, 14, -22,
		    -128, 18, 52, 86, 120, -128, 3, -1, -1, -1, -1, -1, 0, -78 };
	    currentCommand = teachInTelegram;
	    return null;
	}
	if (stepId.equals(MSG_EXAMPLE_1B)) {
	    byte[] teachInTelegram = { 85, 0, 10, 7, 1, -21, -91, 0, 0, 127, 8,
		    18, 52, 86, 120, -128, 3, -1, -1, -1, -1, -1, 0, 48 };
	    currentCommand = teachInTelegram;
	    return null;
	}
	if (stepId.startsWith(MSG_EXAMPLE_2)) {
	    currentCommand = Utils.hex2Bytes(stepId.replaceFirst(MSG_EXAMPLE_2, ""));
	    return null;
	}
	if (stepId.equals(MDSET_WITH_MD)) {
	    currentCommand = stepId.getBytes();
	    return null;
	}
	if (stepId.equals(CDSET_WITH_CD)) {
	    currentCommand = stepId.getBytes();
	    return null;
	}
	if (stepId.equals(GET_EVENT)) {
	    Logger.d(TAG, "execute(...) returns currentData: " 
		    + Utils.bytesToHexString(currentData));
	    String result = null;
	    if (currentData != null) {
		result = Utils.bytesToHexString(currentData);
		currentData = null;
	    }
	    return result;
	}
	if (stepId.equals(PLUG_DONGLE)) {
	    Logger.d(TAG, "execute(...) returns currentData: "
		    + Utils.bytesToHexString(currentData));
	    // This message should be displayed on the user screen
	    // when testing EnOcean implementation for real.
	    return null;
	}
	Logger.e(TAG, "The given command is UNKNOWN.");
	return null;
    }

}
