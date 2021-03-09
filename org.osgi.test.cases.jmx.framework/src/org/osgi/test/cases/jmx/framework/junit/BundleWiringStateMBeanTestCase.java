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
package org.osgi.test.cases.jmx.framework.junit;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleRevisions;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.jmx.framework.FrameworkMBean;
import org.osgi.jmx.framework.ServiceStateMBean;
import org.osgi.jmx.framework.wiring.BundleWiringStateMBean;

public class BundleWiringStateMBeanTestCase extends MBeanGeneralTestCase {
    private Bundle testBundle1;
    private Bundle testBundle2;
    private Bundle testBundle3;
    private BundleWiringStateMBean brsMBean;
    private BundleContext bundleContext;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        waitForRegistering(createObjectName(FrameworkMBean.OBJECTNAME));
        FrameworkMBean frameworkMBean = getMBeanFromServer(FrameworkMBean.OBJECTNAME,
                                            FrameworkMBean.class);
        frameworkMBean.refreshBundlesAndWait(null);

        testBundle3 = install("tb3.jar");
        testBundle3.start();

        testBundle2 = install("tb2.jar");
        testBundle2.start();

        testBundle1 = install("tb1.jar");
        testBundle1.start();

        waitForRegistering(createObjectName(BundleWiringStateMBean.OBJECTNAME));
        brsMBean = getMBeanFromServer(BundleWiringStateMBean.OBJECTNAME,
                BundleWiringStateMBean.class);
        bundleContext = super.getContext();
    }

    public void testObjectNameStructure() throws Exception {
        ObjectName queryName = new ObjectName(BundleWiringStateMBean.OBJECTNAME + ",*");
        Set<ObjectName> names = getMBeanServer().queryNames(queryName, null);
        assertEquals(1, names.size());

        ObjectName name = names.iterator().next();
        Hashtable<String, String> props = name.getKeyPropertyList();

        String type = props.get("type");
        assertEquals("wiringState", type);
        String version = props.get("version");
		assertEquals("1.1", version);
        String framework = props.get("framework");
        assertEquals(getContext().getBundle(0).getSymbolicName(), framework);
        String uuid = props.get("uuid");
        assertEquals(getContext().getProperty(Constants.FRAMEWORK_UUID), uuid);

        assertTrue(name.getKeyPropertyListString().startsWith(
                "type=" + type + ",version=" + version + ",framework=" + framework + ",uuid=" + uuid));
    }

    public void testGetCurrentRevisionDeclaredRequirements() throws Exception {
        BundleRevision revision = testBundle3.adapt(BundleRevision.class);
        List<BundleRequirement> declaredReqs = revision.getDeclaredRequirements(BundleRevision.PACKAGE_NAMESPACE);

        CompositeData[] reqs = brsMBean.getCurrentRevisionDeclaredRequirements(testBundle3.getBundleId(), BundleRevision.PACKAGE_NAMESPACE);
        Set<String> namespaces = new HashSet<String>();
        for (CompositeData req : reqs) {
            assertCompositeDataKeys(req, "BUNDLE_REQUIREMENT", new String[] {
               BundleWiringStateMBean.ATTRIBUTES, BundleWiringStateMBean.DIRECTIVES, BundleWiringStateMBean.NAMESPACE});
            namespaces.add((String) req.get(BundleWiringStateMBean.NAMESPACE));
        }
        assertEquals(Collections.singleton(BundleRevision.PACKAGE_NAMESPACE), namespaces);

        Set<CapReqData> expected = requirementsToSet(declaredReqs);
        Set<CapReqData> actual = jmxCapReqToSet(reqs);
        assertEquals(expected, actual);
    }

    public void testGetCurrentRevisionDeclaredRequirementsNull() throws Exception {
        BundleRevision revision = testBundle3.adapt(BundleRevision.class);
        List<BundleRequirement> declaredReqs = revision.getDeclaredRequirements(null);

        CompositeData[] reqs = brsMBean.getCurrentRevisionDeclaredRequirements(testBundle3.getBundleId(), null);
        Set<String> namespaces = new HashSet<String>();
        for (CompositeData req : reqs) {
            assertCompositeDataKeys(req, "BUNDLE_REQUIREMENT", new String[] {
                    BundleWiringStateMBean.ATTRIBUTES, BundleWiringStateMBean.DIRECTIVES, BundleWiringStateMBean.NAMESPACE});
            namespaces.add((String) req.get(BundleWiringStateMBean.NAMESPACE));
        }
        assertTrue("tb3 has at least requirements in 2 namespaces: package and bundle", namespaces.size() >= 2);
        assertTrue(namespaces.contains(BundleRevision.PACKAGE_NAMESPACE));
        assertTrue(namespaces.contains(BundleRevision.BUNDLE_NAMESPACE));

        Set<CapReqData> expected = requirementsToSet(declaredReqs);
        Set<CapReqData> actual = jmxCapReqToSet(reqs);
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    public void testGetRevisionsDeclaredRequirements() throws Exception {
        // Update testbundle2, this forces a second bundle revision in the system
        testBundle2.update(getContext().getBundle().getEntry("tb2_updated.jar").openStream());
        BundleRevisions br = testBundle2.adapt(BundleRevisions.class);
        assertEquals("Precondition", 2, br.getRevisions().size());

        TabularData revReqs = brsMBean.getRevisionsDeclaredRequirements(testBundle2.getBundleId(), BundleRevision.PACKAGE_NAMESPACE);
        assertEquals("Two revisions expected", 2, revReqs.size());

        int importFrameworkCount = 0;
        boolean foundImport420 = false;
        boolean foundImport999 = false;
        for (List<?> key : (Set<List<?>>) revReqs.keySet()) {
            CompositeData data = revReqs.get(key.toArray());
            assertEquals(key.get(0), data.get(BundleWiringStateMBean.BUNDLE_REVISION_ID));

            for (CompositeData req : (CompositeData []) data.get(BundleWiringStateMBean.REQUIREMENTS)) {
                assertEquals("This bundle only has Import-Package type requirements",
                    BundleRevision.PACKAGE_NAMESPACE, req.get(BundleWiringStateMBean.NAMESPACE));
                TabularData dirs = (TabularData) req.get(BundleWiringStateMBean.DIRECTIVES);

                assertEquals("filter", dirs.get(new Object [] {"filter"}).get("Key"));
                String filter = (String) dirs.get(new Object [] {"filter"}).get("Value");

                if (filter.matches(".*" + BundleRevision.PACKAGE_NAMESPACE + "[ ]*=[ ]*org[.]osgi[.]framework.*")) {
                    importFrameworkCount++;
				}
				else
					if (filter
							.matches(".*"
									+ BundleRevision.PACKAGE_NAMESPACE
									+ "[ ]*=[ ]*org[.]osgi[.]test[.]cases[.]jmx[.]framework[.]tb2[.]api.*")) {
                    if (filter.matches(".*[(]version[ ]*>=[ ]*999[.]0[.]0[)].*")) {
                        if (filter.matches(".*[!][ ]*[(]version[ ]*>=[ ]*1000[.]0[.]0[)].*")) {
                            foundImport999 = true;
                        }
                    }
                    if (filter.matches(".*[(]version[ ]*>=[ ]*4[.]2[.]0[)].*")) {
                        if (filter.matches(".*[!][ ]*[(]version[ ]*>=[ ]*5[.]0[.]0[)].*")) {
                            foundImport420 = true;
                        }
                    }

                }
            }
        }
        assertEquals("Both revisions should import the framework", 2, importFrameworkCount);
        assertTrue(foundImport420);
        assertTrue(foundImport999);
    }

    @SuppressWarnings("unchecked")
    public void testGetRevisionsDeclaredRequirementsNull() throws Exception {
        // Update testbundle2, this forces a second bundle revision in the system
        testBundle2.update(getContext().getBundle().getEntry("tb2_updated.jar").openStream());
        BundleRevisions br = testBundle2.adapt(BundleRevisions.class);
        assertEquals("Precondition", 2, br.getRevisions().size());

        TabularData revReqs = brsMBean.getRevisionsDeclaredRequirements(testBundle2.getBundleId(), null);
        assertEquals("Two revisions expected", 2, revReqs.size());

        int importFrameworkCount = 0;
        boolean foundImport420 = false;
        boolean foundImport999 = false;
        for (List<?> key : (Set<List<?>>) revReqs.keySet()) {
            CompositeData data = revReqs.get(key.toArray());
            assertEquals(key.get(0), data.get(BundleWiringStateMBean.BUNDLE_REVISION_ID));

            for (CompositeData req : (CompositeData []) data.get(BundleWiringStateMBean.REQUIREMENTS)) {
                assertEquals("This bundle only has Import-Package type requirements",
                    BundleRevision.PACKAGE_NAMESPACE, req.get(BundleWiringStateMBean.NAMESPACE));
                TabularData dirs = (TabularData) req.get(BundleWiringStateMBean.DIRECTIVES);

                assertEquals("filter", dirs.get(new Object [] {"filter"}).get("Key"));
                String filter = (String) dirs.get(new Object [] {"filter"}).get("Value");

                if (filter.matches(".*" + BundleRevision.PACKAGE_NAMESPACE + "[ ]*=[ ]*org[.]osgi[.]framework.*")) {
                    importFrameworkCount++;
				}
				else
					if (filter
							.matches(".*"
									+ BundleRevision.PACKAGE_NAMESPACE
									+ "[ ]*=[ ]*org[.]osgi[.]test[.]cases[.]jmx[.]framework[.]tb2[.]api.*")) {
                    if (filter.matches(".*[(]version[ ]*>=[ ]*999[.]0[.]0[)].*")) {
                        if (filter.matches(".*[!][ ]*[(]version[ ]*>=[ ]*1000[.]0[.]0[)].*")) {
                            foundImport999 = true;
                        }
                    }
                    if (filter.matches(".*[(]version[ ]*>=[ ]*4[.]2[.]0[)].*")) {
                        if (filter.matches(".*[!][ ]*[(]version[ ]*>=[ ]*5[.]0[.]0[)].*")) {
                            foundImport420 = true;
                        }
                    }

                }
            }
        }
        assertEquals("Both revisions should import the framework", 2, importFrameworkCount);
        assertTrue(foundImport420);
        assertTrue(foundImport999);
    }

    public void testGetCurrentRevisionDeclaredCapabilities() throws Exception {
        BundleRevision revision = testBundle3.adapt(BundleRevision.class);
        List<BundleCapability> declaredCaps = revision.getDeclaredCapabilities(BundleRevision.BUNDLE_NAMESPACE);

        CompositeData[] caps = brsMBean.getCurrentRevisionDeclaredCapabilities(testBundle3.getBundleId(), BundleRevision.BUNDLE_NAMESPACE);
        Set<String> namespaces = new HashSet<String>();
        for (CompositeData cap : caps) {
            assertCompositeDataKeys(cap, "BUNDLE_CAPABILITY", new String[] {
                    BundleWiringStateMBean.ATTRIBUTES, BundleWiringStateMBean.DIRECTIVES, BundleWiringStateMBean.NAMESPACE});
            namespaces.add((String) cap.get(BundleWiringStateMBean.NAMESPACE));
        }
        assertEquals(Collections.singleton(BundleRevision.BUNDLE_NAMESPACE), namespaces);

        Set<CapReqData> expected = capabilitiesToSet(declaredCaps);
        Set<CapReqData> actual = jmxCapReqToSet(caps);
        assertEquals(expected, actual);
    }

    public void testGetCurrentRevisionDeclaredCapabilitiesNull() throws Exception {
        BundleRevision revision = testBundle3.adapt(BundleRevision.class);
        List<BundleCapability> declaredCaps = revision.getDeclaredCapabilities(null);

        CompositeData[] caps = brsMBean.getCurrentRevisionDeclaredCapabilities(testBundle3.getBundleId(), null);
        Set<String> namespaces = new HashSet<String>();
        for (CompositeData cap : caps) {
            assertCompositeDataKeys(cap, "BUNDLE_CAPABILITY", new String[] {
                    BundleWiringStateMBean.ATTRIBUTES, BundleWiringStateMBean.DIRECTIVES, BundleWiringStateMBean.NAMESPACE});
            namespaces.add((String) cap.get(BundleWiringStateMBean.NAMESPACE));
        }
        assertTrue("tb3 has at least capabilities in 3 namespaces: bundle, fragment host and identity", namespaces.size() >= 3);
        assertTrue(namespaces.contains(BundleRevision.BUNDLE_NAMESPACE));
        assertTrue(namespaces.contains(BundleRevision.HOST_NAMESPACE));
        assertTrue(namespaces.contains("osgi.identity")); // TODO pick up from the appropriate constant

        Set<CapReqData> expected = capabilitiesToSet(declaredCaps);
        Set<CapReqData> actual = jmxCapReqToSet(caps);
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    public void testGetRevisionsDeclaredCapabilities() throws Exception {
        // Update testbundle2, this forces a second bundle revision in the system
        testBundle2.update(getContext().getBundle().getEntry("tb2_updated.jar").openStream());
        BundleRevisions br = testBundle2.adapt(BundleRevisions.class);
        assertEquals("Precondition", 2, br.getRevisions().size());

        TabularData revCaps = brsMBean.getRevisionsDeclaredCapabilities(testBundle2.getBundleId(), BundleRevision.PACKAGE_NAMESPACE);
        assertEquals("Two revisions expected", 2, revCaps.size());

        boolean foundExport420 = false;
        boolean foundExport999 = false;
        for (List<?> key : (Set<List<?>>) revCaps.keySet()) {
            CompositeData data = revCaps.get(key.toArray());
            assertEquals(key.get(0), data.get(BundleWiringStateMBean.BUNDLE_REVISION_ID));

            CompositeData[] caps = (CompositeData []) data.get(BundleWiringStateMBean.CAPABILITIES);
            assertEquals(1, caps.length);

            assertEquals("This bundle only has Import-Package type capabilities",
                BundleRevision.PACKAGE_NAMESPACE, caps[0].get(BundleWiringStateMBean.NAMESPACE));
            TabularData attrs = (TabularData) caps[0].get(BundleWiringStateMBean.ATTRIBUTES);

            assertEquals("org.osgi.test.cases.jmx.framework.tb2.api", attrs.get(new Object [] {BundleRevision.PACKAGE_NAMESPACE}).get("Value"));
            assertEquals("String", attrs.get(new Object [] {BundleRevision.PACKAGE_NAMESPACE}).get("Type"));
            assertEquals(BundleRevision.PACKAGE_NAMESPACE, attrs.get(new Object [] {BundleRevision.PACKAGE_NAMESPACE}).get("Key"));

            Version version = new Version((String) attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Value"));
            if (testBundle2.getVersion().equals(version)) {
                foundExport420 = true;
            } else if (new Version("999").equals(version)) {
                foundExport999 = true;
            }
            assertEquals("Version", attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Type"));
            assertEquals(Constants.VERSION_ATTRIBUTE, attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Key"));
        }
        assertTrue(foundExport420);
        assertTrue(foundExport999);
    }

    @SuppressWarnings("unchecked")
    public void testGetRevisionsDeclaredCapabilitiesNull() throws Exception {
        // Update testbundle2, this forces a second bundle revision in the system
        testBundle2.update(getContext().getBundle().getEntry("tb2_updated.jar").openStream());
        BundleRevisions br = testBundle2.adapt(BundleRevisions.class);
        assertEquals("Precondition", 2, br.getRevisions().size());

        TabularData revCaps = brsMBean.getRevisionsDeclaredCapabilities(testBundle2.getBundleId(), null);
        assertEquals("Two revisions expected", 2, revCaps.size());

        int bundleNamespaceCount = 0;
        int hostNamespaceCount = 0;
        int identityCount = 0;
        boolean foundExport420 = false;
        boolean foundExport999 = false;
        for (List<?> key : (Set<List<?>>) revCaps.keySet()) {
            CompositeData data = revCaps.get(key.toArray());
            assertEquals(key.get(0), data.get(BundleWiringStateMBean.BUNDLE_REVISION_ID));

            CompositeData[] caps = (CompositeData []) data.get(BundleWiringStateMBean.CAPABILITIES);
            for (CompositeData cap : caps) {
                String namespace = (String) cap.get(BundleWiringStateMBean.NAMESPACE);
                TabularData attrs = (TabularData) cap.get(BundleWiringStateMBean.ATTRIBUTES);

                if (BundleRevision.PACKAGE_NAMESPACE.equals(namespace)) {
                    assertEquals("org.osgi.test.cases.jmx.framework.tb2.api", attrs.get(new Object [] {BundleRevision.PACKAGE_NAMESPACE}).get("Value"));
                    assertEquals("String", attrs.get(new Object [] {BundleRevision.PACKAGE_NAMESPACE}).get("Type"));
                    assertEquals(BundleRevision.PACKAGE_NAMESPACE, attrs.get(new Object [] {BundleRevision.PACKAGE_NAMESPACE}).get("Key"));

                    Version version = new Version((String) attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Value"));
                    if (testBundle2.getVersion().equals(version)) {
                        foundExport420 = true;
                    } else if (new Version("999").equals(version)) {
                        foundExport999 = true;
                    }
                    assertEquals("Version", attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Type"));
                    assertEquals(Constants.VERSION_ATTRIBUTE, attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Key"));
                } else if (BundleRevision.BUNDLE_NAMESPACE.equals(namespace)) {
                    bundleNamespaceCount++;

                    assertTrue("Symbolic name should either be 'org.osgi.test.cases.jmx.framework.tb2' or 'org.osgi.test.cases.jmx.framework.tb2_updated'",
                        ((String) attrs.get(new Object [] {BundleRevision.BUNDLE_NAMESPACE}).get("Value")).startsWith("org.osgi.test.cases.jmx.framework.tb2"));
                    assertEquals("String", attrs.get(new Object [] {BundleRevision.BUNDLE_NAMESPACE}).get("Type"));
                    assertEquals(BundleRevision.BUNDLE_NAMESPACE, attrs.get(new Object [] {BundleRevision.BUNDLE_NAMESPACE}).get("Key"));

                    assertEquals(testBundle2.getVersion(), new Version((String) attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Value")));
                    assertEquals("Version", attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Type"));
                    assertEquals(Constants.BUNDLE_VERSION_ATTRIBUTE, attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Key"));
                } else if (BundleRevision.HOST_NAMESPACE.equals(namespace)) {
                    hostNamespaceCount++;

                    assertTrue("Symbolic name should either be 'org.osgi.test.cases.jmx.framework.tb2' or 'org.osgi.test.cases.jmx.framework.tb2_updated'",
                        ((String) attrs.get(new Object [] {BundleRevision.HOST_NAMESPACE}).get("Value")).startsWith("org.osgi.test.cases.jmx.framework.tb2"));
                    assertEquals("String", attrs.get(new Object [] {BundleRevision.HOST_NAMESPACE}).get("Type"));
                    assertEquals(BundleRevision.HOST_NAMESPACE, attrs.get(new Object [] {BundleRevision.HOST_NAMESPACE}).get("Key"));

                    assertEquals(testBundle2.getVersion(), new Version((String) attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Value")));
                    assertEquals("Version", attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Type"));
                    assertEquals(Constants.BUNDLE_VERSION_ATTRIBUTE, attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Key"));
                } else if ("osgi.identity".equals(namespace)) {
                    identityCount++;

                    assertTrue("Symbolic name should either be 'org.osgi.test.cases.jmx.framework.tb2' or 'org.osgi.test.cases.jmx.framework.tb2_updated'",
                        ((String) attrs.get(new Object [] {"osgi.identity"}).get("Value")).startsWith("org.osgi.test.cases.jmx.framework.tb2"));
                    assertEquals("String", attrs.get(new Object [] {"osgi.identity"}).get("Type"));
                    assertEquals("osgi.identity", attrs.get(new Object [] {"osgi.identity"}).get("Key"));

                    assertEquals(testBundle2.getVersion(), new Version((String) attrs.get(new Object [] {"version"}).get("Value")));
                    assertEquals("Version", attrs.get(new Object [] {"version"}).get("Type"));
                    assertEquals("version", attrs.get(new Object [] {"version"}).get("Key"));

                    assertEquals("osgi.bundle", attrs.get(new Object [] {"type"}).get("Value"));
                    assertEquals("String", attrs.get(new Object [] {"type"}).get("Type"));
                    assertEquals("type", attrs.get(new Object [] {"type"}).get("Key"));
                }
            }
        }
        assertTrue(foundExport420);
        assertTrue(foundExport999);
        assertEquals(2, bundleNamespaceCount);
        assertEquals(2, hostNamespaceCount);
        assertEquals(2, identityCount);
    }

    public void testGetCurrentWiring() throws Exception {
        BundleWiring wiring = testBundle1.adapt(BundleWiring.class);
        List<BundleCapability> capabilities = wiring.getCapabilities(BundleRevision.PACKAGE_NAMESPACE);
        List<BundleRequirement> requirements = wiring.getRequirements(BundleRevision.PACKAGE_NAMESPACE);
        List<BundleWire> providedWires = wiring.getProvidedWires(BundleRevision.PACKAGE_NAMESPACE);
        List<BundleWire> requiredWires = wiring.getRequiredWires(BundleRevision.PACKAGE_NAMESPACE);

        CompositeData wiringData = brsMBean.getCurrentWiring(testBundle1.getBundleId(), BundleRevision.PACKAGE_NAMESPACE);
        assertCompositeDataKeys(wiringData, "BUNDLE_WIRING", BundleWiringStateMBean.BUNDLE_WIRING_TYPE.keySet());

        assertEquals(testBundle1.getBundleId(), wiringData.get(BundleWiringStateMBean.BUNDLE_ID));
        CompositeData[] capabilityData = (CompositeData[]) wiringData.get(BundleWiringStateMBean.CAPABILITIES);
        Set<String> namespaces = new HashSet<String>();
        for (CompositeData cap : capabilityData) {
            namespaces.add((String) cap.get(BundleWiringStateMBean.NAMESPACE));
        }
        assertEquals(capabilitiesToSet(capabilities), jmxCapReqToSet(capabilityData));

        CompositeData[] requirementData = (CompositeData[]) wiringData.get(BundleWiringStateMBean.REQUIREMENTS);
        Set<String> namespaces2 = new HashSet<String>();
        for (CompositeData req : requirementData) {
            namespaces2.add((String) req.get(BundleWiringStateMBean.NAMESPACE));
        }
        assertEquals(Collections.singleton(BundleRevision.PACKAGE_NAMESPACE), namespaces2);
        assertEquals(requirementsToSet(requirements), jmxCapReqToSet(requirementData));

        CompositeData [] providedWiresData = (CompositeData[]) wiringData.get(BundleWiringStateMBean.PROVIDED_WIRES);
        assertEquals(wiresToSet(providedWires), wireDataToSet(providedWiresData));

        CompositeData [] requiredWiresData = (CompositeData[]) wiringData.get(BundleWiringStateMBean.REQUIRED_WIRES);
        assertEquals(wiresToSet(requiredWires), wireDataToSet(requiredWiresData));
    }

    public void testGetCurrentWiringNull() throws Exception {
        BundleWiring wiring = testBundle1.adapt(BundleWiring.class);
        List<BundleCapability> capabilities = wiring.getCapabilities(null);
        List<BundleRequirement> requirements = wiring.getRequirements(null);
        List<BundleWire> providedWires = wiring.getProvidedWires(null);
        List<BundleWire> requiredWires = wiring.getRequiredWires(null);

        CompositeData wiringData = brsMBean.getCurrentWiring(testBundle1.getBundleId(), null);
        assertCompositeDataKeys(wiringData, "BUNDLE_WIRING", BundleWiringStateMBean.BUNDLE_WIRING_TYPE.keySet());

        assertEquals(testBundle1.getBundleId(), wiringData.get(BundleWiringStateMBean.BUNDLE_ID));
        CompositeData[] capabilityData = (CompositeData[]) wiringData.get(BundleWiringStateMBean.CAPABILITIES);
        Set<String> namespaces = new HashSet<String>();
        for (CompositeData cap : capabilityData) {
            namespaces.add((String) cap.get(BundleWiringStateMBean.NAMESPACE));
        }
        assertTrue("tb1 has at least capabilities in 3 namespaces: bundle, fragment host and identity", namespaces.size() >= 3);
        assertTrue(namespaces.contains(BundleRevision.BUNDLE_NAMESPACE));
        assertTrue(namespaces.contains(BundleRevision.HOST_NAMESPACE));
        assertTrue(namespaces.contains("osgi.identity")); // TODO pick up from the appropriate constant
        assertEquals(capabilitiesToSet(capabilities), jmxCapReqToSet(capabilityData));

        CompositeData[] requirementData = (CompositeData[]) wiringData.get(BundleWiringStateMBean.REQUIREMENTS);
        Set<String> namespaces2 = new HashSet<String>();
        for (CompositeData req : requirementData) {
            namespaces2.add((String) req.get(BundleWiringStateMBean.NAMESPACE));
        }
        assertEquals(Collections.singleton(BundleRevision.PACKAGE_NAMESPACE), namespaces2);
        assertEquals(requirementsToSet(requirements), jmxCapReqToSet(requirementData));

        CompositeData [] providedWiresData = (CompositeData[]) wiringData.get(BundleWiringStateMBean.PROVIDED_WIRES);
        assertEquals(wiresToSet(providedWires), wireDataToSet(providedWiresData));

        CompositeData [] requiredWiresData = (CompositeData[]) wiringData.get(BundleWiringStateMBean.REQUIRED_WIRES);
        assertEquals(wiresToSet(requiredWires), wireDataToSet(requiredWiresData));
    }

    @SuppressWarnings("unchecked")
    public void testGetCurrentWiringClosure() throws Exception {
        // Update testbundle2, this forces a second bundle revision in the system
        testBundle2.update(getContext().getBundle().getEntry("tb2_updated.jar").openStream());
        BundleRevisions br = testBundle2.adapt(BundleRevisions.class);
        assertEquals("Precondition", 2, br.getRevisions().size());

        Bundle testBundle4 = null;
        try {
            URL entry = getContext().getBundle().getEntry("tb4.jar");
            testBundle4 = getContext().installBundle("tb4.jar", entry.openStream());
            testBundle4.start();

            TabularData closure = brsMBean.getCurrentWiringClosure(testBundle4.getBundleId(), BundleRevision.PACKAGE_NAMESPACE);
            assertTabularDataStructure(closure, "BUNDLES_WIRING", new String [] {
                    BundleWiringStateMBean.BUNDLE_ID, BundleWiringStateMBean.BUNDLE_REVISION_ID
                }, closure.getTabularType().getRowType().keySet());

            for (List<?> key : (Set<List<?>>) closure.keySet()) {
                if (key.get(0).equals(testBundle4.getBundleId())) {
                    CompositeData b4Wiring = closure.get(key.toArray());
                    assertEquals("Test Bundle4 does not provide any wires", 0, ((CompositeData[])b4Wiring.get(BundleWiringStateMBean.PROVIDED_WIRES)).length);

                    String ipFrameworkPattern = ".*" + BundleRevision.PACKAGE_NAMESPACE + "[ ]*=[ ]*org[.]osgi[.]framework.*";
					String ipTb2Pattern = ".*"
							+ BundleRevision.PACKAGE_NAMESPACE
							+ "[ ]*=[ ]*org[.]osgi[.]test[.]cases[.]jmx[.]framework[.]tb2[.]api.*";
                    boolean foundImportFramework = false;
                    boolean foundImportTb2 = false;
                    for (CompositeData req : (CompositeData []) b4Wiring.get(BundleWiringStateMBean.REQUIREMENTS)) {
                        TabularData dirs = (TabularData) req.get(BundleWiringStateMBean.DIRECTIVES);

                        if (BundleRevision.PACKAGE_NAMESPACE.equals(req.get(BundleWiringStateMBean.NAMESPACE))) {
                            assertEquals("filter", dirs.get(new Object [] {"filter"}).get("Key"));
                            String filter = (String) dirs.get(new Object [] {"filter"}).get("Value");
                            if (filter.matches(ipFrameworkPattern)) {
                                foundImportFramework = true;
                            } else if (filter.matches(ipTb2Pattern)) {
                                foundImportTb2 = true;

                                assertEquals("resolution", dirs.get(new Object [] {"resolution"}).get("Key"));
                                assertEquals("mandatory", dirs.get(new Object [] {"resolution"}).get("Value"));
                            }
                        }
                    }
                    assertTrue(foundImportFramework);
                    assertTrue(foundImportTb2);

                    boolean foundImportFrameworkWire = false;
                    boolean foundImportTb2Wire = false;
                    for (CompositeData wire : (CompositeData []) b4Wiring.get(BundleWiringStateMBean.REQUIRED_WIRES)) {
                        CompositeData requirement = (CompositeData) wire.get(BundleWiringStateMBean.BUNDLE_REQUIREMENT);
                        TabularData reqDirs = (TabularData) requirement.get(BundleWiringStateMBean.DIRECTIVES);
                        CompositeData capability = (CompositeData) wire.get(BundleWiringStateMBean.BUNDLE_CAPABILITY);
                        TabularData capAttrs = (TabularData) capability.get(BundleWiringStateMBean.ATTRIBUTES);

                        assertEquals(capability.get(BundleWiringStateMBean.NAMESPACE), requirement.get(BundleWiringStateMBean.NAMESPACE));

                        if (BundleRevision.PACKAGE_NAMESPACE.equals(capability.get(BundleWiringStateMBean.NAMESPACE))) {
                            String pkg = (String) capAttrs.get(new Object [] {BundleRevision.PACKAGE_NAMESPACE}).get("Value");
                            if ("org.osgi.framework".equals(pkg)) {
                                foundImportFrameworkWire = true;

                                List<String> symbolicNames = Arrays.asList(((String) capAttrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Value")).split(","));
                                assertTrue(symbolicNames.contains(bundleContext.getBundle(0).getSymbolicName()));
                                assertTrue(symbolicNames.contains(Constants.SYSTEM_BUNDLE_SYMBOLICNAME));
                                assertEquals("Array of String", capAttrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Type"));
                                assertEquals(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, capAttrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Key"));

                                assertEquals(bundleContext.getBundle(0).getVersion(), new Version(capAttrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Value").toString()));
                                assertEquals("Version", capAttrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Type"));
                                assertEquals(Constants.BUNDLE_VERSION_ATTRIBUTE, capAttrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Key"));

                                assertEquals("filter", reqDirs.get(new Object [] {"filter"}).get("Key"));
                                assertTrue(((String) reqDirs.get(new Object [] {"filter"}).get("Value")).matches(ipFrameworkPattern));

                                assertEquals(testBundle4.getBundleId(), wire.get(BundleWiringStateMBean.REQUIRER_BUNDLE_ID));
                                assertEquals(0l, wire.get(BundleWiringStateMBean.PROVIDER_BUNDLE_ID));
                            } else if ("org.osgi.test.cases.jmx.framework.tb2.api".equals(pkg)) {
                                foundImportTb2Wire = true;
                                assertEquals(testBundle2.getSymbolicName(), capAttrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Value"));
                                assertEquals("String", capAttrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Type"));
                                assertEquals(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, capAttrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Key"));

                                assertEquals(testBundle2.getVersion(), new Version(capAttrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Value").toString()));
                                assertEquals("Version", capAttrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Type"));
                                assertEquals(Constants.BUNDLE_VERSION_ATTRIBUTE, capAttrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Key"));

                                assertEquals("filter", reqDirs.get(new Object [] {"filter"}).get("Key"));
                                assertTrue(((String) reqDirs.get(new Object [] {"filter"}).get("Value")).matches(ipTb2Pattern));

                                assertEquals("resolution", reqDirs.get(new Object [] {"resolution"}).get("Key"));
                                assertEquals("mandatory", reqDirs.get(new Object [] {"resolution"}).get("Value"));

                                assertEquals(testBundle4.getBundleId(), wire.get(BundleWiringStateMBean.REQUIRER_BUNDLE_ID));
                                assertEquals(testBundle2.getBundleId(), wire.get(BundleWiringStateMBean.PROVIDER_BUNDLE_ID));
                            }
                        }
                    }
                    assertTrue(foundImportFrameworkWire);
                    assertTrue(foundImportTb2Wire);
                }
            }
        } finally {
            if (testBundle4 != null)
                testBundle4.uninstall();
        }
    }

    @SuppressWarnings("unchecked")
    public void testGetCurrentWiringClosureNull() throws Exception {
        // Update testbundle2, this forces a second bundle revision in the system
        testBundle2.update(getContext().getBundle().getEntry("tb2_updated.jar").openStream());
        BundleRevisions br = testBundle2.adapt(BundleRevisions.class);
        assertEquals("Precondition", 2, br.getRevisions().size());

        Bundle testBundle4 = null;
        try {
            URL entry = getContext().getBundle().getEntry("tb4.jar");
            testBundle4 = getContext().installBundle("tb4.jar", entry.openStream());
            testBundle4.start();

            TabularData closure = brsMBean.getCurrentWiringClosure(testBundle4.getBundleId(), null);
            assertTabularDataStructure(closure, "BUNDLES_WIRING", new String [] {
                    BundleWiringStateMBean.BUNDLE_ID, BundleWiringStateMBean.BUNDLE_REVISION_ID
                }, closure.getTabularType().getRowType().keySet());

            // Check that for every entry in the return value, all the other entries either have a different bundle ID
            // (they relate to a different bundle) or, if they have the same ID they must have a different bundleRevision
            Set<Long> differentRevisions = new HashSet<Long>();
            int updatedTB2RevisionID = -1;
            int tb4RevisionID = -1;
            for (List<?> key : (Set<List<?>>) closure.keySet()) {
                for (List<?> key2 : (Set<List<?>>) closure.keySet()) {
                    // Capture the TestBundle4 revision ID for later use
                    if (key.get(0).equals(testBundle4.getBundleId()))
                        tb4RevisionID = (Integer) key.get(1);

                    if (key2 == key)
                        // same object, ignore
                        continue;

                    if (!key2.get(0).equals(key.get(0)))
                        // different bundles, that's ok
                        continue;

                    // bundle IDs are the same, now check that the revision IDs are different
                    if (!key2.get(1).equals(key.get(1))) {
                        differentRevisions.add((Long) key.get(0));

                        CompositeData data = closure.get(key.toArray());
                        CompositeData data2 = closure.get(key2.toArray());
                        assertEquals(data.get(BundleWiringStateMBean.BUNDLE_ID), data2.get(BundleWiringStateMBean.BUNDLE_ID));
                        assertFalse(data.get(BundleWiringStateMBean.BUNDLE_REVISION_ID).equals(data2.get(BundleWiringStateMBean.BUNDLE_REVISION_ID)));

                        // capture the revision ID for the updated bundle, we use this later in the test
                        for (CompositeData cd : (CompositeData []) data.get(BundleWiringStateMBean.CAPABILITIES)) {
                            if (BundleRevision.PACKAGE_NAMESPACE.equals(cd.get(BundleWiringStateMBean.NAMESPACE))) {
                                TabularData attrs = (TabularData) cd.get(BundleWiringStateMBean.ATTRIBUTES);
                                if (new Version("999").equals(new Version((String) attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Value")))) {
                                    updatedTB2RevisionID = (Integer) data.get(BundleWiringStateMBean.BUNDLE_REVISION_ID);
                                }
                            }
                        }
                    }
                }
            }
            assertEquals("Should have found a bundle with 2 revisions", 1, differentRevisions.size());
            assertEquals((Long) testBundle2.getBundleId(), differentRevisions.iterator().next());
            assertTrue("Should have found the revision ID of updated testBundle2", updatedTB2RevisionID >= 0);
            assertTrue("Should have found the revision ID of testBundle4", tb4RevisionID >= 0);

            for (List<?> key : (Set<List<?>>) closure.keySet()) {
                if (key.get(0).equals(testBundle4.getBundleId())) {
                    CompositeData b4Wiring = closure.get(key.toArray());
                    assertEquals("Test Bundle4 does not provide any wires", 0, ((CompositeData[])b4Wiring.get(BundleWiringStateMBean.PROVIDED_WIRES)).length);

                    boolean foundIdentity = false;
                    boolean foundBundle = false;
                    boolean foundHost = false;
                    for(CompositeData cap : (CompositeData []) b4Wiring.get(BundleWiringStateMBean.CAPABILITIES)) {
                        TabularData attrs = (TabularData) cap.get(BundleWiringStateMBean.ATTRIBUTES);
                        if ("osgi.identity".equals(cap.get(BundleWiringStateMBean.NAMESPACE))) {
                            foundIdentity = true;

                            assertEquals(testBundle4.getSymbolicName(), attrs.get(new Object [] {"osgi.identity"}).get("Value"));
                            assertEquals("osgi.identity", attrs.get(new Object [] {"osgi.identity"}).get("Key"));
                            assertEquals("String", attrs.get(new Object [] {"osgi.identity"}).get("Type"));

                            assertEquals("osgi.bundle", attrs.get(new Object [] {"type"}).get("Value"));
                            assertEquals("type", attrs.get(new Object [] {"type"}).get("Key"));
                            assertEquals("String", attrs.get(new Object [] {"type"}).get("Type"));

                            assertEquals(testBundle4.getVersion(), new Version(attrs.get(new Object [] {"version"}).get("Value").toString()));
                            assertEquals("version", attrs.get(new Object [] {"version"}).get("Key"));
                            assertEquals("Version", attrs.get(new Object [] {"version"}).get("Type").toString());
                        } else if (BundleRevision.BUNDLE_NAMESPACE.equals(cap.get(BundleWiringStateMBean.NAMESPACE))) {
                            foundBundle = true;

                            assertEquals(testBundle4.getSymbolicName(), attrs.get(new Object [] {BundleRevision.BUNDLE_NAMESPACE}).get("Value"));
                            assertEquals(BundleRevision.BUNDLE_NAMESPACE, attrs.get(new Object [] {BundleRevision.BUNDLE_NAMESPACE}).get("Key"));
                            assertEquals("String", attrs.get(new Object [] {BundleRevision.BUNDLE_NAMESPACE}).get("Type"));

                            assertEquals(testBundle4.getVersion(), new Version(attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Value").toString()));
                            assertEquals(Constants.BUNDLE_VERSION_ATTRIBUTE, attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Key"));
                            assertEquals("Version", attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Type"));

                            TabularData dirs = (TabularData) cap.get(BundleWiringStateMBean.DIRECTIVES);
                            assertEquals("true", dirs.get(new Object [] {Constants.SINGLETON_DIRECTIVE}).get("Value"));
                        } else if (BundleRevision.HOST_NAMESPACE.equals(cap.get(BundleWiringStateMBean.NAMESPACE))) {
                            foundHost = true;

                            assertEquals(testBundle4.getSymbolicName(), attrs.get(new Object [] {BundleRevision.HOST_NAMESPACE}).get("Value"));
                            assertEquals(BundleRevision.HOST_NAMESPACE, attrs.get(new Object [] {BundleRevision.HOST_NAMESPACE}).get("Key"));
                            assertEquals("String", attrs.get(new Object [] {BundleRevision.HOST_NAMESPACE}).get("Type"));

                            assertEquals(testBundle4.getVersion(), new Version(attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Value").toString()));
                            assertEquals(Constants.BUNDLE_VERSION_ATTRIBUTE, attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Key"));
                            assertEquals("Version", attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Type"));
                        }
                    }
                    assertTrue(foundIdentity);
                    assertTrue(foundBundle);
                    assertTrue(foundHost);

                    String ipFrameworkPattern = ".*" + BundleRevision.PACKAGE_NAMESPACE + "[ ]*=[ ]*org[.]osgi[.]framework.*";
					String ipTb2Pattern = ".*"
							+ BundleRevision.PACKAGE_NAMESPACE
							+ "[ ]*=[ ]*org[.]osgi[.]test[.]cases[.]jmx[.]framework[.]tb2[.]api.*";
                    String rbTb1Pattern = ".*" + BundleRevision.BUNDLE_NAMESPACE+"[ ]*=[ ]*"+testBundle1.getSymbolicName() + ".*";
                    boolean foundImportFramework = false;
                    boolean foundImportTb2 = false;
                    boolean foundRequireBundle = false;
                    for (CompositeData req : (CompositeData []) b4Wiring.get(BundleWiringStateMBean.REQUIREMENTS)) {
                        TabularData dirs = (TabularData) req.get(BundleWiringStateMBean.DIRECTIVES);

                        if (BundleRevision.PACKAGE_NAMESPACE.equals(req.get(BundleWiringStateMBean.NAMESPACE))) {
                            assertEquals("filter", dirs.get(new Object [] {"filter"}).get("Key"));
                            String filter = (String) dirs.get(new Object [] {"filter"}).get("Value");
                            if (filter.matches(ipFrameworkPattern)) {
                                foundImportFramework = true;
                            } else if (filter.matches(ipTb2Pattern)) {
                                foundImportTb2 = true;

                                assertEquals("resolution", dirs.get(new Object [] {"resolution"}).get("Key"));
                                assertEquals("mandatory", dirs.get(new Object [] {"resolution"}).get("Value"));
                            }
                        } else if (BundleRevision.BUNDLE_NAMESPACE.equals(req.get(BundleWiringStateMBean.NAMESPACE))) {
                            assertEquals("filter", dirs.get(new Object [] {"filter"}).get("Key"));
                            String filter = (String) dirs.get(new Object [] {"filter"}).get("Value");
                            assertTrue("There should be a bundle requirement to tb1", filter.matches(rbTb1Pattern));
                            foundRequireBundle = true;
                        }
                    }
                    assertTrue(foundImportFramework);
                    assertTrue(foundImportTb2);
                    assertTrue(foundRequireBundle);

                    boolean foundRequireBundleWire = false;
                    boolean foundImportFrameworkWire = false;
                    boolean foundImportTb2Wire = false;
                    for (CompositeData wire : (CompositeData []) b4Wiring.get(BundleWiringStateMBean.REQUIRED_WIRES)) {
                        CompositeData requirement = (CompositeData) wire.get(BundleWiringStateMBean.BUNDLE_REQUIREMENT);
                        TabularData reqDirs = (TabularData) requirement.get(BundleWiringStateMBean.DIRECTIVES);
                        CompositeData capability = (CompositeData) wire.get(BundleWiringStateMBean.BUNDLE_CAPABILITY);
                        TabularData capAttrs = (TabularData) capability.get(BundleWiringStateMBean.ATTRIBUTES);

                        assertEquals(capability.get(BundleWiringStateMBean.NAMESPACE), requirement.get(BundleWiringStateMBean.NAMESPACE));

                        if (BundleRevision.BUNDLE_NAMESPACE.equals(capability.get(BundleWiringStateMBean.NAMESPACE))) {
                            foundRequireBundleWire = true;

                            assertEquals(testBundle1.getSymbolicName(), capAttrs.get(new Object [] {BundleRevision.BUNDLE_NAMESPACE}).get("Value"));
                            assertEquals(BundleRevision.BUNDLE_NAMESPACE, capAttrs.get(new Object [] {BundleRevision.BUNDLE_NAMESPACE}).get("Key"));
                            assertEquals("String", capAttrs.get(new Object [] {BundleRevision.BUNDLE_NAMESPACE}).get("Type"));

                            assertEquals("filter", reqDirs.get(new Object [] {"filter"}).get("Key"));
                            assertTrue(((String) reqDirs.get(new Object [] {"filter"}).get("Value")).matches(rbTb1Pattern));

                            assertEquals(testBundle4.getBundleId(), wire.get(BundleWiringStateMBean.REQUIRER_BUNDLE_ID));
                            assertEquals(testBundle1.getBundleId(), wire.get(BundleWiringStateMBean.PROVIDER_BUNDLE_ID));
                        } else if (BundleRevision.PACKAGE_NAMESPACE.equals(capability.get(BundleWiringStateMBean.NAMESPACE))) {
                            String pkg = (String) capAttrs.get(new Object [] {BundleRevision.PACKAGE_NAMESPACE}).get("Value");
                            if ("org.osgi.framework".equals(pkg)) {
                                foundImportFrameworkWire = true;

                                List<String> symbolicNames = Arrays.asList(((String) capAttrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Value")).split(","));
                                assertTrue(symbolicNames.contains(bundleContext.getBundle(0).getSymbolicName()));
                                assertTrue(symbolicNames.contains(Constants.SYSTEM_BUNDLE_SYMBOLICNAME));
                                assertEquals("Array of String", capAttrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Type"));
                                assertEquals(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, capAttrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Key"));

                                assertEquals(bundleContext.getBundle(0).getVersion(), new Version(capAttrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Value").toString()));
                                assertEquals("Version", capAttrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Type"));
                                assertEquals(Constants.BUNDLE_VERSION_ATTRIBUTE, capAttrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Key"));

                                assertEquals("filter", reqDirs.get(new Object [] {"filter"}).get("Key"));
                                assertTrue(((String) reqDirs.get(new Object [] {"filter"}).get("Value")).matches(ipFrameworkPattern));

                                assertEquals(testBundle4.getBundleId(), wire.get(BundleWiringStateMBean.REQUIRER_BUNDLE_ID));
                                assertEquals(0l, wire.get(BundleWiringStateMBean.PROVIDER_BUNDLE_ID));
                            } else if ("org.osgi.test.cases.jmx.framework.tb2.api".equals(pkg)) {
                                foundImportTb2Wire = true;
                                assertEquals(testBundle2.getSymbolicName(), capAttrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Value"));
                                assertEquals("String", capAttrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Type"));
                                assertEquals(Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE, capAttrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Key"));

                                assertEquals(testBundle2.getVersion(), new Version(capAttrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Value").toString()));
                                assertEquals("Version", capAttrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Type"));
                                assertEquals(Constants.BUNDLE_VERSION_ATTRIBUTE, capAttrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Key"));

                                assertEquals("filter", reqDirs.get(new Object [] {"filter"}).get("Key"));
                                assertTrue(((String) reqDirs.get(new Object [] {"filter"}).get("Value")).matches(ipTb2Pattern));

                                assertEquals("resolution", reqDirs.get(new Object [] {"resolution"}).get("Key"));
                                assertEquals("mandatory", reqDirs.get(new Object [] {"resolution"}).get("Value"));

                                assertEquals(testBundle4.getBundleId(), wire.get(BundleWiringStateMBean.REQUIRER_BUNDLE_ID));
                                assertEquals(testBundle2.getBundleId(), wire.get(BundleWiringStateMBean.PROVIDER_BUNDLE_ID));
                                assertEquals(tb4RevisionID, wire.get(BundleWiringStateMBean.REQUIRER_BUNDLE_REVISION_ID));
                                assertEquals(updatedTB2RevisionID, wire.get(BundleWiringStateMBean.PROVIDER_BUNDLE_REVISION_ID));
                            }
                        }
                    }
                    assertTrue(foundRequireBundleWire);
                    assertTrue(foundImportFrameworkWire);
                    assertTrue(foundImportTb2Wire);

                }
            }
        } finally {
            if (testBundle4 != null)
                testBundle4.uninstall();
        }
    }

    @SuppressWarnings("unchecked")
    public void testGetRevisionsWiring() throws Exception {
        // Update testbundle2, this forces a second bundle revision in the system
        testBundle2.update(getContext().getBundle().getEntry("tb2_updated.jar").openStream());
        BundleRevisions br = testBundle2.adapt(BundleRevisions.class);
        assertEquals("Precondition", 2, br.getRevisions().size());

        TabularData revWirings = brsMBean.getRevisionsWiring(testBundle2.getBundleId(), BundleRevision.PACKAGE_NAMESPACE);
        assertEquals("Two revisions expected", 2, revWirings.size());
        for (CompositeData wiringData : (Collection<CompositeData>) revWirings.values()) {
            assertCompositeDataKeys(wiringData, "BUNDLE_WIRING", BundleWiringStateMBean.BUNDLE_WIRING_TYPE.keySet());

            assertEquals(testBundle2.getBundleId(), wiringData.get(BundleWiringStateMBean.BUNDLE_ID));
            CompositeData[] capabilityData = (CompositeData []) wiringData.get(BundleWiringStateMBean.CAPABILITIES);
            Set<String> namespaces = new HashSet<String>();
            for (CompositeData cap : capabilityData) {
                namespaces.add((String) cap.get(BundleWiringStateMBean.NAMESPACE));
            }
            assertEquals(Collections.singleton(BundleRevision.PACKAGE_NAMESPACE), namespaces);

            Set<CapReqData> capSet = jmxCapReqToSet(capabilityData);
            BundleRevision rev = findMatchingBundleRevision(br, capSet);

            BundleWiring wiring = rev.getWiring();
            assertEquals(capabilitiesToSet(wiring.getCapabilities(BundleRevision.PACKAGE_NAMESPACE)), capSet);

            CompositeData[] requirementData = (CompositeData []) wiringData.get(BundleWiringStateMBean.REQUIREMENTS);
            Set<String> namespaces2 = new HashSet<String>();
            for (CompositeData req : requirementData) {
                namespaces2.add((String) req.get(BundleWiringStateMBean.NAMESPACE));
            }
            assertEquals(Collections.singleton(BundleRevision.PACKAGE_NAMESPACE), namespaces2);
            assertEquals(requirementsToSet(wiring.getRequirements(BundleRevision.PACKAGE_NAMESPACE)), jmxCapReqToSet(requirementData));

            CompositeData [] providedWiresData = (CompositeData[]) wiringData.get(BundleWiringStateMBean.PROVIDED_WIRES);
            assertEquals(wiresToSet(wiring.getProvidedWires(BundleRevision.PACKAGE_NAMESPACE)), wireDataToSet(providedWiresData));

            CompositeData [] requiredWiresData = (CompositeData[]) wiringData.get(BundleWiringStateMBean.REQUIRED_WIRES);
            assertEquals(wiresToSet(wiring.getRequiredWires(BundleRevision.PACKAGE_NAMESPACE)), wireDataToSet(requiredWiresData));
        }
    }

    @SuppressWarnings("unchecked")
    public void testGetRevisionsWiringNull() throws Exception {
        // Update testbundle2, this forces a second bundle revision in the system
        testBundle2.update(getContext().getBundle().getEntry("tb2_updated.jar").openStream());
        BundleRevisions br = testBundle2.adapt(BundleRevisions.class);
        assertEquals("Precondition", 2, br.getRevisions().size());

        TabularData revWirings = brsMBean.getRevisionsWiring(testBundle2.getBundleId(), null);
        assertEquals("Two revisions expected", 2, revWirings.size());
        for (CompositeData wiringData : (Collection<CompositeData>) revWirings.values()) {
            assertCompositeDataKeys(wiringData, "BUNDLE_WIRING", BundleWiringStateMBean.BUNDLE_WIRING_TYPE.keySet());

            assertEquals(testBundle2.getBundleId(), wiringData.get(BundleWiringStateMBean.BUNDLE_ID));
            CompositeData[] capabilityData = (CompositeData []) wiringData.get(BundleWiringStateMBean.CAPABILITIES);
            Set<String> namespaces = new HashSet<String>();
            for (CompositeData cap : capabilityData) {
                namespaces.add((String) cap.get(BundleWiringStateMBean.NAMESPACE));
            }
            assertTrue("tb2 has at least capabilities in 4 namespaces: bundle, fragment host, identity and package",
                namespaces.size() >= 4);
            assertTrue(namespaces.contains(BundleRevision.BUNDLE_NAMESPACE));
            assertTrue(namespaces.contains(BundleRevision.HOST_NAMESPACE));
            assertTrue(namespaces.contains("osgi.identity")); // TODO pick up from the appropriate constant
            assertTrue(namespaces.contains(BundleRevision.PACKAGE_NAMESPACE));

            Set<CapReqData> capSet = jmxCapReqToSet(capabilityData);
            BundleRevision rev = findMatchingBundleRevision(br, capSet);

            BundleWiring wiring = rev.getWiring();
            assertEquals(capabilitiesToSet(wiring.getCapabilities(null)), capSet);

            CompositeData[] requirementData = (CompositeData []) wiringData.get(BundleWiringStateMBean.REQUIREMENTS);
            Set<String> namespaces2 = new HashSet<String>();
            for (CompositeData req : requirementData) {
                namespaces2.add((String) req.get(BundleWiringStateMBean.NAMESPACE));
            }
            assertEquals(Collections.singleton(BundleRevision.PACKAGE_NAMESPACE), namespaces2);
            assertEquals(requirementsToSet(wiring.getRequirements(null)), jmxCapReqToSet(requirementData));

            CompositeData [] providedWiresData = (CompositeData[]) wiringData.get(BundleWiringStateMBean.PROVIDED_WIRES);
            assertEquals(wiresToSet(wiring.getProvidedWires(null)), wireDataToSet(providedWiresData));

            CompositeData [] requiredWiresData = (CompositeData[]) wiringData.get(BundleWiringStateMBean.REQUIRED_WIRES);
            assertEquals(wiresToSet(wiring.getRequiredWires(null)), wireDataToSet(requiredWiresData));
        }
    }

    private BundleRevision findMatchingBundleRevision(BundleRevisions br, Set<CapReqData> capSet) {
        CapReqData crData = null;
        for (CapReqData data : capSet) {
            if (BundleRevision.PACKAGE_NAMESPACE.equals(data.namespace)) {
                crData = data;
                break;
            }
        }

        boolean is999Revision;
        if (new Version("999").equals(crData.attrs.get(Constants.VERSION_ATTRIBUTE))) {
            is999Revision = true;
        } else {
            is999Revision = false;
        }

        for (BundleRevision brev : br.getRevisions()) {
            List<BundleCapability> caps = brev.getDeclaredCapabilities(BundleRevision.PACKAGE_NAMESPACE);
            assertEquals("Exports only 1 package", 1, caps.size());
            if (new Version("999").equals(caps.iterator().next().getAttributes().get(Constants.VERSION_ATTRIBUTE))) {
                if (is999Revision) {
                    return brev;
                }
            } else {
                if (!is999Revision) {
                    return brev;
                }
            }
        }

        // not found
        return null;
    }

    @SuppressWarnings("unchecked")
    public void testGetRevisionsWiringClosure() throws Exception {
        // Update testbundle2, this forces a second bundle revision in the system
        testBundle2.update(getContext().getBundle().getEntry("tb2_updated.jar").openStream());
        BundleRevisions br = testBundle2.adapt(BundleRevisions.class);
        assertEquals("Precondition", 2, br.getRevisions().size());

        Bundle testBundle4 = null;
        try {
            URL entry = getContext().getBundle().getEntry("tb4.jar");
            testBundle4 = getContext().installBundle("tb4.jar", entry.openStream());
            testBundle4.start();

            TabularData closure = brsMBean.getRevisionsWiringClosure(testBundle2.getBundleId(), BundleRevision.PACKAGE_NAMESPACE);
            assertTabularDataStructure(closure, "BUNDLES_WIRING", new String [] {
                    BundleWiringStateMBean.BUNDLE_ID, BundleWiringStateMBean.BUNDLE_REVISION_ID
                }, closure.getTabularType().getRowType().keySet());

            // Obtain the revision IDs for the two revisions of testBundle2
            Set<Long> foundBundleIDs = new HashSet<Long>();
            Integer initialTB2RevisionID = null;
            Integer updatedTB2RevisionID = null;
            for (List<?> key : (Set<List<?>>) closure.keySet()) {
                foundBundleIDs.add((Long) key.get(0));

                for (List<?> key2 : (Set<List<?>>) closure.keySet()) {
                    if (key2 == key)
                        // same object, ignore
                        continue;

                    if (!key2.get(0).equals(key.get(0)))
                        // different bundles, that's ok
                        continue;

                    // bundle IDs are the same, now check that the revision IDs are different
                    if (!key2.get(1).equals(key.get(1))) {
                        CompositeData data = closure.get(key.toArray());
                        CompositeData data2 = closure.get(key2.toArray());
                        assertEquals(data.get(BundleWiringStateMBean.BUNDLE_ID), data2.get(BundleWiringStateMBean.BUNDLE_ID));
                        assertFalse(data.get(BundleWiringStateMBean.BUNDLE_REVISION_ID).equals(data2.get(BundleWiringStateMBean.BUNDLE_REVISION_ID)));

                        // capture the revision ID for the updated bundle, we use this later in the test
                        for (CompositeData cd : (CompositeData []) data.get(BundleWiringStateMBean.CAPABILITIES)) {
                            if (BundleRevision.PACKAGE_NAMESPACE.equals(cd.get(BundleWiringStateMBean.NAMESPACE))) {
                                TabularData attrs = (TabularData) cd.get(BundleWiringStateMBean.ATTRIBUTES);
                                if (new Version("999").equals(new Version((String) attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Value")))) {
                                    updatedTB2RevisionID = (Integer) data.get(BundleWiringStateMBean.BUNDLE_REVISION_ID);
                                } else {
                                    initialTB2RevisionID = (Integer) data.get(BundleWiringStateMBean.BUNDLE_REVISION_ID);
                                }
                            }
                        }
                    }
                }
            }
            assertTrue(initialTB2RevisionID != updatedTB2RevisionID);
            assertNotNull(initialTB2RevisionID);
            assertNotNull(updatedTB2RevisionID);

            Set<Long> expectedBundleIDs = new HashSet<Long>();
            for (Bundle b : bundleContext.getBundles()) {
                expectedBundleIDs.add(b.getBundleId());
            }
            assertEquals("The closure is expected to contain entries for every bundle in the system, as everything is (transitively) wired to everything else",
                expectedBundleIDs, foundBundleIDs);

            boolean foundInitial = false;
            boolean foundUpdated = false;
            for (List<?> key : (Set<List<?>>) closure.keySet()) {
                if (key.get(0).equals(testBundle2.getBundleId())) {
                    CompositeData wiring = closure.get(key.toArray());
                    assertEquals(testBundle2.getBundleId(), wiring.get(BundleWiringStateMBean.BUNDLE_ID));

                    // Test Provided wires
                    CompositeData [] providedWires = (CompositeData []) wiring.get(BundleWiringStateMBean.PROVIDED_WIRES);
                    assertEquals(1, providedWires.length);
                    assertEquals(testBundle2.getBundleId(), providedWires[0].get(BundleWiringStateMBean.PROVIDER_BUNDLE_ID));

                    boolean isUpdated = isUpdatedRevision(providedWires[0]);
                    if (isUpdated) {
                        foundUpdated = true;

                        assertEquals(updatedTB2RevisionID, wiring.get(BundleWiringStateMBean.BUNDLE_REVISION_ID));
                        assertEquals(updatedTB2RevisionID, providedWires[0].get(BundleWiringStateMBean.PROVIDER_BUNDLE_REVISION_ID));
                        assertEquals(testBundle4.getBundleId(), providedWires[0].get(BundleWiringStateMBean.REQUIRER_BUNDLE_ID));
                        int providerRevision = (Integer) providedWires[0].get(BundleWiringStateMBean.REQUIRER_BUNDLE_REVISION_ID);

                        CompositeData providerData = closure.get(new Object [] {testBundle4.getBundleId(), providerRevision});
                        assertEquals(testBundle4.getBundleId(), providerData.get(BundleWiringStateMBean.BUNDLE_ID));
                        assertEquals(providerRevision, providerData.get(BundleWiringStateMBean.BUNDLE_REVISION_ID));

                        CompositeData provCap = (CompositeData) providedWires[0].get(BundleWiringStateMBean.BUNDLE_CAPABILITY);
                        assertEquals(BundleRevision.PACKAGE_NAMESPACE, provCap.get(BundleWiringStateMBean.NAMESPACE));
                        TabularData attrs = (TabularData) provCap.get(BundleWiringStateMBean.ATTRIBUTES);
                        assertEquals("org.osgi.test.cases.jmx.framework.tb2.api", attrs.get(new Object [] {BundleRevision.PACKAGE_NAMESPACE}).get("Value"));
                        assertEquals(new Version("999"), new Version((String) attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Value")));
                        assertEquals("org.osgi.test.cases.jmx.framework.tb2_updated", attrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Value"));
                        assertEquals(testBundle2.getVersion(), new Version((String) attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Value")));

                        CompositeData provReq = (CompositeData) providedWires[0].get(BundleWiringStateMBean.BUNDLE_REQUIREMENT);
                        assertEquals(BundleRevision.PACKAGE_NAMESPACE, provReq.get(BundleWiringStateMBean.NAMESPACE));
                        TabularData dirs = (TabularData) provReq.get(BundleWiringStateMBean.DIRECTIVES);
                        String filter = (String) dirs.get(new Object [] {"filter"}).get("Value");
						assertTrue(filter
								.matches(".*"
										+ BundleRevision.PACKAGE_NAMESPACE
										+ "[ ]*=[ ]*org[.]osgi[.]test[.]cases[.]jmx[.]framework[.]tb2[.]api.*"));
                        assertTrue(filter.matches(".*[(]version[ ]*>=[ ]*999[.]0[.]0[)].*"));
                        assertTrue(filter.matches(".*[!][ ]*[(]version[ ]*>=[ ]*1000[.]0[.]0[)].*"));
                    } else {
                        foundInitial = true;

                        assertEquals(initialTB2RevisionID, wiring.get(BundleWiringStateMBean.BUNDLE_REVISION_ID));
                        assertEquals(initialTB2RevisionID, providedWires[0].get(BundleWiringStateMBean.PROVIDER_BUNDLE_REVISION_ID));
                        assertEquals(testBundle1.getBundleId(), providedWires[0].get(BundleWiringStateMBean.REQUIRER_BUNDLE_ID));
                        int providerRevision = (Integer) providedWires[0].get(BundleWiringStateMBean.REQUIRER_BUNDLE_REVISION_ID);

                        CompositeData providerData = closure.get(new Object [] {testBundle1.getBundleId(), providerRevision});
                        assertEquals(testBundle1.getBundleId(), providerData.get(BundleWiringStateMBean.BUNDLE_ID));
                        assertEquals(providerRevision, providerData.get(BundleWiringStateMBean.BUNDLE_REVISION_ID));

                        CompositeData provCap = (CompositeData) providedWires[0].get(BundleWiringStateMBean.BUNDLE_CAPABILITY);
                        assertEquals(BundleRevision.PACKAGE_NAMESPACE, provCap.get(BundleWiringStateMBean.NAMESPACE));
                        TabularData attrs = (TabularData) provCap.get(BundleWiringStateMBean.ATTRIBUTES);
                        assertEquals("org.osgi.test.cases.jmx.framework.tb2.api", attrs.get(new Object [] {BundleRevision.PACKAGE_NAMESPACE}).get("Value"));
                        assertEquals("In this case the version of the package is the same as the version of the bundle",
                            testBundle2.getVersion(), new Version((String) attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Value")));
                        assertEquals("org.osgi.test.cases.jmx.framework.tb2", attrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Value"));
                        assertEquals(testBundle2.getVersion(), new Version((String) attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Value")));

                        CompositeData provReq = (CompositeData) providedWires[0].get(BundleWiringStateMBean.BUNDLE_REQUIREMENT);
                        assertEquals(BundleRevision.PACKAGE_NAMESPACE, provReq.get(BundleWiringStateMBean.NAMESPACE));
                        TabularData dirs = (TabularData) provReq.get(BundleWiringStateMBean.DIRECTIVES);
                        String filter = (String) dirs.get(new Object [] {"filter"}).get("Value");
						assertTrue(filter
								.matches(".*"
										+ BundleRevision.PACKAGE_NAMESPACE
										+ "[ ]*=[ ]*org[.]osgi[.]test[.]cases[.]jmx[.]framework[.]tb2[.]api.*"));
                        assertTrue(filter.matches(".*[(]version[ ]*>=[ ]*4[.]2[.]0[.]\\d+[)].*"));
                    }

                    //Test Required wires
                    CompositeData [] requiredWires = (CompositeData []) wiring.get(BundleWiringStateMBean.REQUIRED_WIRES);
                    for (CompositeData requiredWire : requiredWires) {
                        assertEquals(testBundle2.getBundleId(), requiredWire.get(BundleWiringStateMBean.REQUIRER_BUNDLE_ID));
                        if (isUpdated) {
                            assertEquals(updatedTB2RevisionID, requiredWire.get(BundleWiringStateMBean.REQUIRER_BUNDLE_REVISION_ID));
                        } else {
                            assertEquals(initialTB2RevisionID, requiredWire.get(BundleWiringStateMBean.REQUIRER_BUNDLE_REVISION_ID));
                        }

                        CompositeData req = (CompositeData) requiredWire.get(BundleWiringStateMBean.BUNDLE_REQUIREMENT);
                        assertEquals(BundleRevision.PACKAGE_NAMESPACE, req.get(BundleWiringStateMBean.NAMESPACE));
                    }

                    CompositeData [] capabilities = (CompositeData []) wiring.get(BundleWiringStateMBean.CAPABILITIES);
                    Set<String> actualCapabilities = new HashSet<String>();
                    for (CompositeData cap : capabilities) {
                        actualCapabilities.add((String) cap.get(BundleWiringStateMBean.NAMESPACE));
                    }
                    assertEquals(Collections.singleton(BundleRevision.PACKAGE_NAMESPACE), actualCapabilities);

                    CompositeData [] requirements = (CompositeData []) wiring.get(BundleWiringStateMBean.REQUIREMENTS);
                    for (CompositeData req : requirements) {
                        assertEquals(BundleRevision.PACKAGE_NAMESPACE, req.get(BundleWiringStateMBean.NAMESPACE));
                    }
                }
            }
            assertTrue(foundInitial);
            assertTrue(foundUpdated);
        } finally {
            if (testBundle4 != null)
                testBundle4.uninstall();
        }
    }

    @SuppressWarnings("unchecked")
    public void testGetRevisionsWiringClosureNull() throws Exception {
        // Update testbundle2, this forces a second bundle revision in the system
        testBundle2.update(getContext().getBundle().getEntry("tb2_updated.jar").openStream());
        BundleRevisions br = testBundle2.adapt(BundleRevisions.class);
        assertEquals("Precondition", 2, br.getRevisions().size());

        Bundle testBundle4 = null;
        try {
            URL entry = getContext().getBundle().getEntry("tb4.jar");
            testBundle4 = getContext().installBundle("tb4.jar", entry.openStream());
            testBundle4.start();

            TabularData closure = brsMBean.getRevisionsWiringClosure(testBundle2.getBundleId(), null);
            assertTabularDataStructure(closure, "BUNDLES_WIRING", new String [] {
                    BundleWiringStateMBean.BUNDLE_ID, BundleWiringStateMBean.BUNDLE_REVISION_ID
                }, closure.getTabularType().getRowType().keySet());

            // Obtain the revision IDs for the two revisions of testBundle2
            Set<Long> foundBundleIDs = new HashSet<Long>();
            Integer initialTB2RevisionID = null;
            Integer updatedTB2RevisionID = null;
            for (List<?> key : (Set<List<?>>) closure.keySet()) {
                foundBundleIDs.add((Long) key.get(0));

                for (List<?> key2 : (Set<List<?>>) closure.keySet()) {
                    if (key2 == key)
                        // same object, ignore
                        continue;

                    if (!key2.get(0).equals(key.get(0)))
                        // different bundles, that's ok
                        continue;

                    // bundle IDs are the same, now check that the revision IDs are different
                    if (!key2.get(1).equals(key.get(1))) {
                        CompositeData data = closure.get(key.toArray());
                        CompositeData data2 = closure.get(key2.toArray());
                        assertEquals(data.get(BundleWiringStateMBean.BUNDLE_ID), data2.get(BundleWiringStateMBean.BUNDLE_ID));
                        assertFalse(data.get(BundleWiringStateMBean.BUNDLE_REVISION_ID).equals(data2.get(BundleWiringStateMBean.BUNDLE_REVISION_ID)));

                        // capture the revision ID for the updated bundle, we use this later in the test
                        for (CompositeData cd : (CompositeData []) data.get(BundleWiringStateMBean.CAPABILITIES)) {
                            if (BundleRevision.PACKAGE_NAMESPACE.equals(cd.get(BundleWiringStateMBean.NAMESPACE))) {
                                TabularData attrs = (TabularData) cd.get(BundleWiringStateMBean.ATTRIBUTES);
                                if (new Version("999").equals(new Version((String) attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Value")))) {
                                    updatedTB2RevisionID = (Integer) data.get(BundleWiringStateMBean.BUNDLE_REVISION_ID);
                                } else {
                                    initialTB2RevisionID = (Integer) data.get(BundleWiringStateMBean.BUNDLE_REVISION_ID);
                                }
                            }
                        }
                    }
                }
            }
            assertTrue(initialTB2RevisionID != updatedTB2RevisionID);
            assertNotNull(initialTB2RevisionID);
            assertNotNull(updatedTB2RevisionID);

            Set<Long> expectedBundleIDs = new HashSet<Long>();
            for (Bundle b : bundleContext.getBundles()) {
                expectedBundleIDs.add(b.getBundleId());
            }
            assertEquals("The closure is expected to contain entries for every bundle in the system, as everything is (transitively) wired to everything else",
                expectedBundleIDs, foundBundleIDs);

            boolean foundInitial = false;
            boolean foundUpdated = false;
            for (List<?> key : (Set<List<?>>) closure.keySet()) {
                if (key.get(0).equals(testBundle2.getBundleId())) {
                    CompositeData wiring = closure.get(key.toArray());
                    assertEquals(testBundle2.getBundleId(), wiring.get(BundleWiringStateMBean.BUNDLE_ID));

                    // Test Provided wires
                    CompositeData [] providedWires = (CompositeData []) wiring.get(BundleWiringStateMBean.PROVIDED_WIRES);
                    assertEquals(1, providedWires.length);
                    assertEquals(testBundle2.getBundleId(), providedWires[0].get(BundleWiringStateMBean.PROVIDER_BUNDLE_ID));

                    boolean isUpdated = isUpdatedRevision(providedWires[0]);
                    if (isUpdated) {
                        foundUpdated = true;

                        assertEquals(updatedTB2RevisionID, wiring.get(BundleWiringStateMBean.BUNDLE_REVISION_ID));
                        assertEquals(updatedTB2RevisionID, providedWires[0].get(BundleWiringStateMBean.PROVIDER_BUNDLE_REVISION_ID));
                        assertEquals(testBundle4.getBundleId(), providedWires[0].get(BundleWiringStateMBean.REQUIRER_BUNDLE_ID));
                        int providerRevision = (Integer) providedWires[0].get(BundleWiringStateMBean.REQUIRER_BUNDLE_REVISION_ID);

                        CompositeData providerData = closure.get(new Object [] {testBundle4.getBundleId(), providerRevision});
                        assertEquals(testBundle4.getBundleId(), providerData.get(BundleWiringStateMBean.BUNDLE_ID));
                        assertEquals(providerRevision, providerData.get(BundleWiringStateMBean.BUNDLE_REVISION_ID));

                        CompositeData provCap = (CompositeData) providedWires[0].get(BundleWiringStateMBean.BUNDLE_CAPABILITY);
                        assertEquals(BundleRevision.PACKAGE_NAMESPACE, provCap.get(BundleWiringStateMBean.NAMESPACE));
                        TabularData attrs = (TabularData) provCap.get(BundleWiringStateMBean.ATTRIBUTES);
                        assertEquals("org.osgi.test.cases.jmx.framework.tb2.api", attrs.get(new Object [] {BundleRevision.PACKAGE_NAMESPACE}).get("Value"));
                        assertEquals(new Version("999"), new Version((String) attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Value")));
                        assertEquals("org.osgi.test.cases.jmx.framework.tb2_updated", attrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Value"));
                        assertEquals(testBundle2.getVersion(), new Version((String) attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Value")));

                        CompositeData provReq = (CompositeData) providedWires[0].get(BundleWiringStateMBean.BUNDLE_REQUIREMENT);
                        assertEquals(BundleRevision.PACKAGE_NAMESPACE, provReq.get(BundleWiringStateMBean.NAMESPACE));
                        TabularData dirs = (TabularData) provReq.get(BundleWiringStateMBean.DIRECTIVES);
                        String filter = (String) dirs.get(new Object [] {"filter"}).get("Value");
						assertTrue(filter
								.matches(".*"
										+ BundleRevision.PACKAGE_NAMESPACE
										+ "[ ]*=[ ]*org[.]osgi[.]test[.]cases[.]jmx[.]framework[.]tb2[.]api.*"));
                        assertTrue(filter.matches(".*[(]version[ ]*>=[ ]*999[.]0[.]0[)].*"));
                        assertTrue(filter.matches(".*[!][ ]*[(]version[ ]*>=[ ]*1000[.]0[.]0[)].*"));
                    } else {
                        foundInitial = true;

                        assertEquals(initialTB2RevisionID, wiring.get(BundleWiringStateMBean.BUNDLE_REVISION_ID));
                        assertEquals(initialTB2RevisionID, providedWires[0].get(BundleWiringStateMBean.PROVIDER_BUNDLE_REVISION_ID));
                        assertEquals(testBundle1.getBundleId(), providedWires[0].get(BundleWiringStateMBean.REQUIRER_BUNDLE_ID));
                        int providerRevision = (Integer) providedWires[0].get(BundleWiringStateMBean.REQUIRER_BUNDLE_REVISION_ID);

                        CompositeData providerData = closure.get(new Object [] {testBundle1.getBundleId(), providerRevision});
                        assertEquals(testBundle1.getBundleId(), providerData.get(BundleWiringStateMBean.BUNDLE_ID));
                        assertEquals(providerRevision, providerData.get(BundleWiringStateMBean.BUNDLE_REVISION_ID));

                        CompositeData provCap = (CompositeData) providedWires[0].get(BundleWiringStateMBean.BUNDLE_CAPABILITY);
                        assertEquals(BundleRevision.PACKAGE_NAMESPACE, provCap.get(BundleWiringStateMBean.NAMESPACE));
                        TabularData attrs = (TabularData) provCap.get(BundleWiringStateMBean.ATTRIBUTES);
                        assertEquals("org.osgi.test.cases.jmx.framework.tb2.api", attrs.get(new Object [] {BundleRevision.PACKAGE_NAMESPACE}).get("Value"));
                        assertEquals("In this case the version of the package is the same as the version of the bundle",
                            testBundle2.getVersion(), new Version((String) attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Value")));
                        assertEquals("org.osgi.test.cases.jmx.framework.tb2", attrs.get(new Object [] {Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE}).get("Value"));
                        assertEquals(testBundle2.getVersion(), new Version((String) attrs.get(new Object [] {Constants.BUNDLE_VERSION_ATTRIBUTE}).get("Value")));

                        CompositeData provReq = (CompositeData) providedWires[0].get(BundleWiringStateMBean.BUNDLE_REQUIREMENT);
                        assertEquals(BundleRevision.PACKAGE_NAMESPACE, provReq.get(BundleWiringStateMBean.NAMESPACE));
                        TabularData dirs = (TabularData) provReq.get(BundleWiringStateMBean.DIRECTIVES);
                        String filter = (String) dirs.get(new Object [] {"filter"}).get("Value");
						assertTrue(filter
								.matches(".*"
										+ BundleRevision.PACKAGE_NAMESPACE
										+ "[ ]*=[ ]*org[.]osgi[.]test[.]cases[.]jmx[.]framework[.]tb2[.]api.*"));
                        assertTrue(filter.matches(".*[(]version[ ]*>=[ ]*4[.]2[.]0[.]\\d+[)].*"));
                    }

                    //Test Required wires
                    CompositeData [] requiredWires = (CompositeData []) wiring.get(BundleWiringStateMBean.REQUIRED_WIRES);
                    for (CompositeData requiredWire : requiredWires) {
                        assertEquals(testBundle2.getBundleId(), requiredWire.get(BundleWiringStateMBean.REQUIRER_BUNDLE_ID));
                        if (isUpdated) {
                            assertEquals(updatedTB2RevisionID, requiredWire.get(BundleWiringStateMBean.REQUIRER_BUNDLE_REVISION_ID));
                        } else {
                            assertEquals(initialTB2RevisionID, requiredWire.get(BundleWiringStateMBean.REQUIRER_BUNDLE_REVISION_ID));
                        }

                        CompositeData req = (CompositeData) requiredWire.get(BundleWiringStateMBean.BUNDLE_REQUIREMENT);
                        assertEquals(BundleRevision.PACKAGE_NAMESPACE, req.get(BundleWiringStateMBean.NAMESPACE));
                    }

                    CompositeData [] capabilities = (CompositeData []) wiring.get(BundleWiringStateMBean.CAPABILITIES);
                    Set<String> expectedCapabilities = new HashSet<String>(Arrays.asList(
                        BundleRevision.BUNDLE_NAMESPACE, BundleRevision.HOST_NAMESPACE, BundleRevision.PACKAGE_NAMESPACE, "osgi.identity"));
                    Set<String> actualCapabilities = new HashSet<String>();
                    for (CompositeData cap : capabilities) {
                        actualCapabilities.add((String) cap.get(BundleWiringStateMBean.NAMESPACE));
                    }
                    assertEquals(expectedCapabilities, actualCapabilities);

                    CompositeData [] requirements = (CompositeData []) wiring.get(BundleWiringStateMBean.REQUIREMENTS);
                    for (CompositeData req : requirements) {
                        assertEquals(BundleRevision.PACKAGE_NAMESPACE, req.get(BundleWiringStateMBean.NAMESPACE));
                    }
                }
            }
            assertTrue(foundInitial);
            assertTrue(foundUpdated);
        } finally {
            if (testBundle4 != null)
                testBundle4.uninstall();
        }
    }

    private boolean isUpdatedRevision(CompositeData providedWires) {
        CompositeData cap = (CompositeData) providedWires.get(BundleWiringStateMBean.BUNDLE_CAPABILITY);
        TabularData attrs = (TabularData) cap.get(BundleWiringStateMBean.ATTRIBUTES);
        String ver = (String) attrs.get(new Object [] {Constants.VERSION_ATTRIBUTE}).get("Value");
        return new Version("999").equals(new Version(ver));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        super.waitForUnRegistering(createObjectName(ServiceStateMBean.OBJECTNAME));
        if (testBundle1 != null) {
            uninstallBundle(testBundle1);
        }
        if (testBundle2 != null) {
            uninstallBundle(testBundle2);
        }
        if (testBundle3 != null) {
            uninstallBundle(testBundle3);
        }
    }

    // Turn a list of capabilities into a set which can be compared to a similar set constructed from CompositeData
    private Set<CapReqData> capabilitiesToSet(List<BundleCapability> capabilities) {
        Set<CapReqData> set = new HashSet<CapReqData>();
        for (BundleCapability cap : capabilities) {
            set.add(new CapReqData(cap.getNamespace(), cap.getAttributes(), cap.getDirectives()));
        }
        return set;
    }

    // Turn a list of requirements into a set which can be compared to a similar set constructed from CompositeData
    private Set<CapReqData> requirementsToSet(List<BundleRequirement> requirements) {
        Set<CapReqData> set = new HashSet<CapReqData>();
        for (BundleRequirement req : requirements) {
            set.add(new CapReqData(req.getNamespace(), req.getAttributes(), req.getDirectives()));
        }
        return set;
    }

    // Turn a composite data capabilities or requirements array into a set that can be compared with ones generated from Bundle data
    private Set<CapReqData> jmxCapReqToSet(CompositeData[] jmxCapabilitiesOrRequirements) {
        Set<CapReqData> set = new HashSet<CapReqData>();
        for (CompositeData jmxCapReq : jmxCapabilitiesOrRequirements) {
            set.add(new CapReqData((String) jmxCapReq.get(BundleWiringStateMBean.NAMESPACE),
                    getJmxAttributes(jmxCapReq), getJmxDirectives(jmxCapReq)));
        }
        return set;
    }

    private Map<String, Object> getJmxAttributes(CompositeData jmxCapReq) {
        TabularData jmxAttributes = (TabularData) jmxCapReq.get(BundleWiringStateMBean.ATTRIBUTES);
        Hashtable<String, Object> attrs = OSGiProperties.propertiesFrom(jmxAttributes);
        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            if (entry.getValue().getClass().isArray()) {
                // Convert array into list
                List<Object> list = Arrays.asList((Object []) entry.getValue());
                entry.setValue(list);
            }
        }
        return attrs;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getJmxDirectives(CompositeData jmxCapReq) {
        TabularData jmxDirectives = (TabularData) jmxCapReq.get(BundleWiringStateMBean.DIRECTIVES);
        Map<String, String> dMap = new HashMap<String, String>();
        for (CompositeData jmxDir : (Collection<CompositeData>) jmxDirectives.values()) {
            dMap.put((String) jmxDir.get(BundleWiringStateMBean.KEY), (String) jmxDir.get(BundleWiringStateMBean.VALUE));
        }
        return dMap;
    }

    // Convert a CompositeData[] into a set that can be compared to a similar set constructed from a BundleWire
    private Set<WiringData> wireDataToSet(CompositeData[] wiresData) {
        Set<WiringData> set = new HashSet<WiringData>();
        for (CompositeData data : wiresData) {
            CompositeData req = (CompositeData) data.get(BundleWiringStateMBean.BUNDLE_REQUIREMENT);
            CapReqData reqData = new CapReqData((String) req.get(BundleWiringStateMBean.NAMESPACE), getJmxAttributes(req), getJmxDirectives(req));
            CompositeData cap = (CompositeData) data.get(BundleWiringStateMBean.BUNDLE_CAPABILITY);
            CapReqData capData = new CapReqData((String) cap.get(BundleWiringStateMBean.NAMESPACE), getJmxAttributes(cap), getJmxDirectives(cap));
            WiringData bwdata = new WiringData(reqData, capData,
                    (Long) data.get(BundleWiringStateMBean.PROVIDER_BUNDLE_ID),
                    (Long) data.get(BundleWiringStateMBean.REQUIRER_BUNDLE_ID));
            set.add(bwdata);
        }
        return set;
    }

    // Convert a List<BundleWire> into a set that can be compared to a similar set constructed from a CompositeData[]
    private Set<WiringData> wiresToSet(List<BundleWire> wires) {
        Set<WiringData> set = new HashSet<WiringData>();
        for (BundleWire wire : wires) {
            BundleRequirement req = wire.getRequirement();
            CapReqData reqData = new CapReqData(req.getNamespace(), req.getAttributes(), req.getDirectives());
            BundleCapability cap = wire.getCapability();
            CapReqData capData = new CapReqData(cap.getNamespace(), cap.getAttributes(), cap.getDirectives());
            WiringData bwdata = new WiringData(reqData, capData, wire.getProviderWiring().getBundle().getBundleId(), wire.getRequirerWiring().getBundle().getBundleId());
            set.add(bwdata);
        }
        return set;
    }

    public static class CapReqData {
		final String						namespace;
		final Map<String,Object>	attrs;
		final Map<String,String>	dirs;

        public CapReqData(String namespace, Map<String, Object> attrs, Map<String, String> dirs) {
            this.namespace = namespace;
            this.attrs = attrs;
            this.dirs = dirs;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((attrs == null) ? 0 : attrs.hashCode());
            result = prime * result + ((dirs == null) ? 0 : dirs.hashCode());
            result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CapReqData other = (CapReqData) obj;
            if (attrs == null) {
                if (other.attrs != null)
                    return false;
            } else if (!attrs.equals(other.attrs))
                return false;
            if (dirs == null) {
                if (other.dirs != null)
                    return false;
            } else if (!dirs.equals(other.dirs))
                return false;
            if (namespace == null) {
                if (other.namespace != null)
                    return false;
            } else if (!namespace.equals(other.namespace))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "CapReqData [namespace=" + namespace + ", attrs=" + attrs + ", dirs=" + dirs + "]";
        }
    }

    public static class WiringData {
        private final CapReqData requirement;
        private final CapReqData capability;
        private final long provider;
        private final long requirer;

        public WiringData(CapReqData req, CapReqData cap, long providerBundleID, long requirerBundleID) {
            requirement = req;
            capability = cap;
            provider = providerBundleID;
            requirer = requirerBundleID;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((capability == null) ? 0 : capability.hashCode());
            result = prime * result + (int) (provider ^ (provider >>> 32));
            result = prime * result + ((requirement == null) ? 0 : requirement.hashCode());
            result = prime * result + (int) (requirer ^ (requirer >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            WiringData other = (WiringData) obj;
            if (capability == null) {
                if (other.capability != null)
                    return false;
            } else if (!capability.equals(other.capability))
                return false;
            if (provider != other.provider)
                return false;
            if (requirement == null) {
                if (other.requirement != null)
                    return false;
            } else if (!requirement.equals(other.requirement))
                return false;
            if (requirer != other.requirer)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "WiringData [requirement=" + requirement + ", capability=" + capability + ", provider=" + provider
                    + ", requirer=" + requirer + "]";
        }
    }
}
