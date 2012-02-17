/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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
package org.osgi.test.cases.subsystem.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import junit.framework.Assert;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.resource.ResourceConstants;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.test.cases.subsystem.junit.SubsystemTest;
import org.osgi.test.support.OSGiTestCase;

public class SubsystemInfo {

	private final File esaFile;
	private final TestResource subsystemResource;

	public SubsystemInfo(File ssaFile, boolean hasSN, String v, String t, boolean acceptDependencies, String contentHeader, Map<String, URL> contents, Map<String, String> extraHeaders) {
		this.esaFile = ssaFile;
		String sn = null;
		if (hasSN) {
			sn = SubsystemTest.getSymbolicName(ssaFile.getName());
		}
		Map<String, String> sm = new HashMap<String, String>();
		if (extraHeaders != null)
			sm.putAll(extraHeaders);
		Map<String, Object> subsystemAttrs = new HashMap<String, Object>();
		if (sn != null) {
			sm.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, sn);
			subsystemAttrs.put(ResourceConstants.IDENTITY_NAMESPACE, sn);
		}
		if (v != null) {
			sm.put(SubsystemConstants.SUBSYSTEM_VERSION, v);
			subsystemAttrs.put(Constants.VERSION_ATTRIBUTE, Version.parseVersion(v));
		}

		if (t != null) {
			subsystemAttrs.put(ResourceConstants.IDENTITY_TYPE_ATTRIBUTE, t);
			if (acceptDependencies)
				t += SubsystemConstants.PROVISION_POLICY_DIRECTIVE + ":=" + SubsystemConstants.PROVISION_POLICY_ACCEPT_DEPENDENCIES;
			sm.put(SubsystemConstants.SUBSYSTEM_TYPE, t);
		} else {
			// need to default to application
			subsystemAttrs.put(ResourceConstants.IDENTITY_TYPE_ATTRIBUTE, SubsystemConstants.SUBSYSTEM_TYPE_APPLICATION);
		}

		if (contentHeader != null) {
			sm.put(SubsystemConstants.SUBSYSTEM_CONTENT, contentHeader);
		}
		createSubsystem(sm.size() == 0 ? null : sm, null, contents, this.esaFile);
		try {
			this.subsystemResource = new TestResource(subsystemAttrs, null, this.esaFile.toURI().toURL());
		} catch (MalformedURLException e) {
			OSGiTestCase.fail("Could not find subsystem: " + ssaFile, e);
			throw new RuntimeException(e);
		}
	}

	public TestResource getSubsystemResource() {
		return subsystemResource;
	}

	public File getSubsystemArchive() {
		return esaFile;
	}

	/**
	 * Creates a subsystem with the given subsystem manifest, deployment manifest, and content.
	 * The subsystem is created using the given target file.
	 * @param sm the subsystem manifest
	 * @param dm the deployment manifest
	 * @param content the contents
	 * @param target the target to write the subsystem archive.
	 */

	private static void createSubsystem(Map<String, String> sm, Map<String, String> dm, Map<String, URL> contents, File target) {
		target.getParentFile().mkdirs();
		Set<String> directories = new HashSet<String>();
		Assert.assertTrue("Parent folder does not exist.", target.getParentFile().exists());
		try {
			ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(target));
			putManifest(SubsystemTest.SUBSYSTEM_MANIFEST, sm, zip, directories);
			putManifest(SubsystemTest.DEPLOYMENT_MANIFEST, dm, zip, directories);
			if (contents != null) {
				for (Map.Entry<String, URL> entry : contents.entrySet()) {
					putNextEntry(zip, entry.getKey(), entry.getValue().openStream(), directories);
				}
			}
			// make sure we have at least one entry
			Map<String, String> testInfo = new HashMap<String, String>();
			testInfo.put("subsystem.file.name", target.getName());
			putManifest("OSGI-INF/test", testInfo, zip, directories);
			zip.close();
		} catch (IOException e) {
			OSGiTestCase.fail("Failed to create subsystem archive: " + target.getName(), e);
		}
	}

	private static void putManifest(String manifestName, Map<String, String> manifest, ZipOutputStream zip, Set<String> directories) throws IOException {
		if (manifest == null)
			return;
		ByteArrayOutputStream manifestContent = new ByteArrayOutputStream();
		PrintStream manifestPrinter = new PrintStream(manifestContent);
		for (Map.Entry<String, String> entry : manifest.entrySet()) {
			manifestPrinter.println(entry.getKey() + ": " + entry.getValue());
		}
		manifestPrinter.close();
		ByteArrayInputStream manifestInput = new ByteArrayInputStream(manifestContent.toByteArray());
		putNextEntry(zip, manifestName, manifestInput, directories);
	}

	private static void putNextEntry(ZipOutputStream zip, String entryName, InputStream in, Set<String> directories) throws IOException {
		ZipEntry entry = new ZipEntry(entryName);
		// It is questionable if we should test with or without directories entries
		// this bit of code ensures directory entries exist before the content entries
		if (!entry.isDirectory()) {
			int idxLastSlash = entryName.lastIndexOf('/');
			if (idxLastSlash != -1) {
				ZipEntry dirEntry = new ZipEntry(entryName.substring(0, idxLastSlash + 1));
				if (!directories.contains(dirEntry.getName())) {
					zip.putNextEntry(dirEntry);
					zip.closeEntry();
					directories.add(dirEntry.getName());
				}
			}
		} else {
			if (directories.contains(entry.getName())) {
				return;
			} else {
				directories.add(entry.getName());
			}
		}
		zip.putNextEntry(new ZipEntry(entryName));
		int len;
		byte[] buf = new byte[1024];
		while ((len = in.read(buf)) > 0) {
            zip.write(buf, 0, len);
        }
		zip.closeEntry();
		in.close();
	}
 
}
