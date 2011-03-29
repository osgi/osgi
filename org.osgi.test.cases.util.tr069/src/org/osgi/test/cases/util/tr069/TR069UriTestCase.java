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
 * Checks the validation and transformation of correct TR-069 paths.
 * 
 * @author Evgeni Grigorov, e.grigorov@prosyst.com
 */
public final class TR069UriTestCase extends OSGiTestCase {

	/** Tests the validation of the correct TR-069 paths. */
	public void testTR069PathValidation() {
		// Validate full TR-069 path.
		assertTrue(
				"The TR-069 path must be valid: "
						+ TR069UriTestCaseConstants.TR069_PATH_FULL,
				TR069URI.isValidTR069Path(TR069UriTestCaseConstants.TR069_PATH_FULL));

		// Validate OSGi specific extension
		assertTrue(
				"The TR-069 path must be valid: "
						+ TR069UriTestCaseConstants.TR069_PATH_EXT,
				TR069URI.isValidTR069Path(TR069UriTestCaseConstants.TR069_PATH_EXT));

		// Validate partial path
		assertTrue(
				"The TR-069 path must be valid: "
						+ TR069UriTestCaseConstants.TR069_PATH_PARTIAL,
				TR069URI.isValidTR069Path(TR069UriTestCaseConstants.TR069_PATH_PARTIAL));

		// Validate path with an instance number
		assertTrue(
				"The TR-069 path must be valid: "
						+ TR069UriTestCaseConstants.TR069_PATH_INSTANCE,
				TR069URI.isValidTR069Path(TR069UriTestCaseConstants.TR069_PATH_INSTANCE));

		// check empty string
		assertTrue(
				"The empty TR-069 path must be valid!",
				TR069URI.isValidTR069Path(TR069UriTestCaseConstants.EMPTY_STRING));

		// check the underscore as starting character
		assertTrue(
				"The underscore is valid starting character in the TR-069 path name.",
				TR069URI.isValidTR069Path(TR069UriTestCaseConstants.TR069_PATH_STARTING_UNDERSCORE));
	}

	/** Tests all valid characters. */
	public void testPathNameCharacterRanges() {
		checkValidPathNameCharacters(TR069UriTestCaseConstants.TR069_PATH_BASE_CHAR_RANGES);
		checkValidPathNameCharacters(TR069UriTestCaseConstants.TR069_PATH_IDEOGRAPHIC_RANGES);
		checkValidPathNameCharacters(TR069UriTestCaseConstants.TR069_PATH_DIGIT_RANGES);
	}

	/** Tests the transformation from DMT Uri to TR-069 path. */
	public void testDMTUriToTR069Path() {
		// Check full TR-069 path
		checkDMTUriToTR069Path(TR069UriTestCaseConstants.DMT_URI_FULL,
				TR069UriTestCaseConstants.TR069_PATH_FULL);

		// Check OSGi specific extension
		checkDMTUriToTR069Path(TR069UriTestCaseConstants.DMT_URI_EXT,
				TR069UriTestCaseConstants.TR069_PATH_EXT);

		// Check DMT Uri with an instance number
		checkDMTUriToTR069Path(TR069UriTestCaseConstants.DMT_URI_INSTANCE,
				TR069UriTestCaseConstants.TR069_PATH_INSTANCE);

		// https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1935
		// [PA Utility Classes] the empty string is valid TR-069 path
		// Check DMT root
		// checkDMTUriToTR069Path(TR069UriTestCaseConstants.POINT_STRING,
		// TR069UriTestCaseConstants.EMPTY_STRING);
	}

	/** Tests the transformation fro TR-069 path to DMT Uri. */
	public void testTR069PathToDMTUri() {
		// Check full TR-069 path
		checkTR069PathToDMTUri(TR069UriTestCaseConstants.DMT_URI_FULL,
				TR069UriTestCaseConstants.TR069_PATH_FULL);

		// Check OSGi specific extension
		checkTR069PathToDMTUri(TR069UriTestCaseConstants.DMT_URI_EXT,
				TR069UriTestCaseConstants.TR069_PATH_EXT);

		// https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1934
		// [PA Utility Classes] partial TR-069 paths are not correctly converted
		// // Convert partial TR-069 path to DMT Uri
		// dmtUri = TR069URI
		// .getDmtUri(TR069UriTestCaseConstants.TR069_PATH_PARTIAL);
		// assertEquals("The partial TR-069 path is not correctly converted!",
		// TR069UriTestCaseConstants.DMT_URI_FULL, dmtUri);

		// Check DMT Uri with an instance number
		checkTR069PathToDMTUri(TR069UriTestCaseConstants.DMT_URI_INSTANCE,
				TR069UriTestCaseConstants.TR069_PATH_INSTANCE);

		// https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1935
		// [PA Utility Classes] the empty string is valid TR-069 path
		// // Check DMT root
		// checkTR069PathToDMTUri(TR069UriTestCaseConstants.POINT_STRING,
		// TR069UriTestCaseConstants.EMPTY_STRING);
	}

	private void checkDMTUriToTR069Path(String dmtUri, String tr069Path) {
		// Convert DMT Uri to TR-069 path.
		assertEquals("The DMT Uri is not correctly converted!", tr069Path,
				TR069URI.getTR069Path(dmtUri));

		// Reconvert TR-069 path.
		assertEquals("The TR-069 path is not correctly reconverted!",
				tr069Path, TR069URI.getTR069Path(TR069URI.getDmtUri(tr069Path)));
	}

	private void checkTR069PathToDMTUri(String dmtUri, String tr069Path) {
		// Convert TR-069 path to DMT Uri.
		assertEquals("The TR-069 path is not correctly converted!", dmtUri,
				TR069URI.getDmtUri(tr069Path));

		// Reconvert DMT Uri to TR-069 path.
		assertEquals("The DMT Uri is not correctly reconverted!", dmtUri,
				TR069URI.getDmtUri(TR069URI.getTR069Path(dmtUri)));
	}

	private void checkValidPathNameCharacters(char[] chars) {
		String currentTR069Path;
		for (int i = 0, endIndex = chars.length - 1; i < endIndex; i++) {
			for (int ii = chars[i]; ii <= chars[i + 1]; ii++) {
				currentTR069Path = TR069UriTestCaseConstants.TR069_PATH_FULL
						+ String.valueOf(ii);
				assertTrue(
						"The TR-069 path must be valid: " + currentTR069Path,
						TR069URI.isValidTR069Path(currentTR069Path));
			}
		}
	}

}
