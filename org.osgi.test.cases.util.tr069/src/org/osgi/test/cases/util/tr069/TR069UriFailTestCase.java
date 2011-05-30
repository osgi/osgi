/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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

package org.osgi.test.cases.util.tr069;

import org.osgi.test.cases.util.tr069.helper.TR069UriTestCaseConstants;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.tr069.TR069URI;

/**
 * Checks the behavior of path validation and transformation for invalid
 * parameters.
 * 
 * @author Evgeni Grigorov, e.grigorov@prosyst.com
 */
public final class TR069UriFailTestCase extends OSGiTestCase {

	/** Tests validation with invalid parameters. */
	public void testIncorrectTR069PathValidation() {
		// check null value
		assertFalse("The null value is not valid TR-069 path!",
				TR069URI.isValidTR069Path(null));

		// check a few white spaces
		assertFalse("The white spaces are not valid TR-069 path!",
				TR069URI.isValidTR069Path("        "));

		// TODO: uncomment after the fix of: 1954
		// // check foo parameter
		// assertFalse("The TR-069 path is valid: "
		// + TR069UriTestCaseConstants.FOO,
		// TR069URI.isValidTR069Path(TR069UriTestCaseConstants.FOO));

		// TODO: uncomment after the fix of: 1954
		// // check foo instance number
		// assertFalse(
		// "The TR-069 path is not valid: "
		// + TR069UriTestCaseConstants.TR069_PATH_EXT_INSTANCE_FOO,
		// TR069URI.isValidTR069Path(TR069UriTestCaseConstants.TR069_PATH_EXT_INSTANCE_FOO));

		// check a few points placed in the middle
		assertFalse(
				"The TR-069 path is not valid: "
						+ TR069UriTestCaseConstants.TR069_PATH_MIDDLE_POINTS,
				TR069URI.isValidTR069Path(TR069UriTestCaseConstants.TR069_PATH_MIDDLE_POINTS));

		// check a few end points
		assertFalse(
				"The TR-069 path is not valid: "
						+ TR069UriTestCaseConstants.TR069_PATH_END_POINTS,
				TR069URI.isValidTR069Path(TR069UriTestCaseConstants.TR069_PATH_END_POINTS));

		// check the digit as a first symbol - it is not allowed
		assertFalse(
				"The digit must not be allowed as a first character in the path name!",
				TR069URI.isValidTR069Path(TR069UriTestCaseConstants.TR069_PATH_STARTING_DIGIT));

		// check the hyphen as a first symbol - it is not allowed
		assertFalse(
				"The hyphen must not be allowed as a first character in the path name!",
				TR069URI.isValidTR069Path(TR069UriTestCaseConstants.TR069_PATH_STARTING_HYPHEN));
	}

	public void testIncorrectAbsTR069PathValidation() {
		// Check the relative path.
		assertFalse(
				"The TR-069 path should not be absolute: ",
				TR069URI.isAbsoluteTR069Path(TR069UriTestCaseConstants.TR069_PATH_STARTING_POINT));

		// Check the point
		assertFalse(
				"The point should not be absolute!",
				TR069URI.isAbsoluteTR069Path(TR069UriTestCaseConstants.POINT_STRING));
	}

	/** Tests the invalid starting characters in the TR-069 path name. */
	public void testInvalidStartingCharacters() {
		checkInvalidStartingCharacters(TR069UriTestCaseConstants.TR069_PATH_DIGIT_RANGES);
	}

	private void checkInvalidStartingCharacters(char[] chars) {
		String currentInvalidPath;
		for (int i = 0, endIndex = chars.length - 1; i < endIndex; i++) {
			for (int ii = chars[i]; ii <= chars[i + 1]; ii++) {
				currentInvalidPath = String.valueOf(ii)
						+ TR069UriTestCaseConstants.TR069_PATH_FULL;
				assertFalse("The TR-069 path must be invalid: "
						+ currentInvalidPath,
						TR069URI.isValidTR069Path(currentInvalidPath));
			}
		}
	}

}
