/*
 * Copyright (c) OSGi Alliance 2012.
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.repository.junit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.repository.Repository;
import org.osgi.service.repository.RepositoryContent;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author David Bosschaert
 */
public class RepositoryTest extends DefaultTestBundleControl {
    public static final String REPOSITORY_XML_KEY = "repository-xml";
    public static final String REPOSITORY_POPULATED_KEY = "repository-populated";

    private ServiceRegistration<String> repositoryXMLService;
    private List<Repository> repositoryServices = new CopyOnWriteArrayList<Repository>();
    private ServiceTracker<Repository, Repository> repositoryServiceTracker;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        String repoXML = getRepoXML();
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(REPOSITORY_XML_KEY, getClass().getName());
        repositoryXMLService = getContext().registerService(String.class, repoXML, props);

        Filter filter = getContext().createFilter("(" + REPOSITORY_POPULATED_KEY + "=" + getClass().getName() + ")");
        ServiceTracker<?,?> populatedST = new ServiceTracker<Object, Object>(getContext(), filter, null);
        populatedST.open();
        Object svc = populatedST.waitForService(30000);
        populatedST.close();

        if (svc == null)
            throw new IllegalStateException("Repository TCK integration code did not report that Repository population is finished. "
                    + "It should should register a service with property: " + REPOSITORY_POPULATED_KEY + "=" + getClass().getName());

        System.err.println("*** Repository TCK integration reports that the Repository has been populated: " + svc);

        repositoryServiceTracker = new ServiceTracker<Repository,Repository>(getContext(), Repository.class, null) {
            @Override
            public Repository addingService(ServiceReference<Repository> reference) {
                Repository service = super.addingService(reference);
                repositoryServices.add(service);
                return service;
            }

            @Override
            public void removedService(ServiceReference<Repository> reference, Repository service) {
                repositoryServices.remove(service);
                super.removedService(reference, service);
            }
        };
        repositoryServiceTracker.open();
    }

    @Override
    protected void tearDown() throws Exception {
        repositoryXMLService.unregister();
        repositoryServiceTracker.close();

        super.tearDown();
    }

    public void testQueryByBundleID() throws Exception {
        Requirement requirement = new RequirementImpl("osgi.wiring.bundle",
                "(&(osgi.wiring.bundle=org.osgi.test.cases.repository.tb1)(bundle-version=1.0.0.test))");

        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(requirement);
        assertEquals(1, result.size());
        assertEquals(requirement, result.keySet().iterator().next());

        assertEquals(1, result.values().size());
        Collection<Capability> matchingCapabilities = result.values().iterator().next();
        assertEquals(1, matchingCapabilities.size());
        Capability capability = matchingCapabilities.iterator().next();

        assertEquals(requirement.getNamespace(), capability.getNamespace());
        assertEquals("org.osgi.test.cases.repository.tb1", capability.getAttributes().get("osgi.wiring.bundle"));
        assertEquals(Version.parseVersion("1.0.0.test"), capability.getAttributes().get("bundle-version"));
    }

    // Fails in RI
    public void testQueryNoMatch() throws Exception {
        Requirement requirement = new RequirementImpl("osgi.wiring.bundle",
                "(&(osgi.wiring.bundle=org.osgi.test.cases.repository.tb1)(foo=bar))");

        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(requirement);
        assertEquals(1, result.size());
        assertEquals(requirement, result.keySet().iterator().next());

        Collection<Capability> matchingCapabilities = result.get(requirement);
        assertEquals(0, matchingCapabilities.size());
    }

    // Fails in RI
    public void testQueryNoFilter() throws Exception {
        Requirement requirement = new RequirementImpl("osgi.wiring.bundle");
        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(requirement);
        assertEquals(1, result.size());
        Collection<Capability> matches = result.get(requirement);
        assertEquals(2, matches.size());

        boolean foundtb1 = false, foundtb2 = false;
        for (Capability cap : matches) {
            if (cap.getAttributes().get("osgi.wiring.bundle").equals("org.osgi.test.cases.repository.tb1")) {
                foundtb1 = true;
            } else if (cap.getAttributes().get("osgi.wiring.bundle").equals("org.osgi.test.cases.repository.tb2")) {
                foundtb2 = true;
            }
        }

        assertTrue(foundtb1);
        assertTrue(foundtb2);
    }

    // Fails in RI
    public void testQueryOnNonMainAttribute() throws Exception {
        Requirement requirement = new RequirementImpl("osgi.identity",
                "(license=http://www.opensource.org/licenses/Apache-2.0)");

        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(requirement);
        assertEquals(1, result.size());
        Collection<Capability> matches = result.get(requirement);
        assertEquals(1, matches.size());
        Capability capability = matches.iterator().next();
        assertEquals("osgi.identity", capability.getNamespace());
        assertEquals("org.osgi.test.cases.repository.tb2", capability.getAttributes().get("osgi.identity"));
    }

    public void testDisconnectedQueries() throws Exception {
        Requirement req1 = new RequirementImpl("osgi.wiring.bundle",
                "(osgi.wiring.bundle=org.osgi.test.cases.repository.tb1)");
        Requirement req2 = new RequirementImpl("osgi.wiring.bundle",
                "(osgi.wiring.bundle=org.osgi.test.cases.repository.tb2)");

        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(req1, req2);
        assertEquals(2, result.size());

        Collection<Capability> match1 = result.get(req1);
        assertEquals(1, match1.size());
        Capability cap1 = match1.iterator().next();
        assertEquals("org.osgi.test.cases.repository.tb1", cap1.getAttributes().get("osgi.wiring.bundle"));

        Collection<Capability> match2 = result.get(req2);
        assertEquals(1, match2.size());
        Capability cap2 = match2.iterator().next();
        assertEquals("org.osgi.test.cases.repository.tb2", cap2.getAttributes().get("osgi.wiring.bundle"));
    }

    // Fails in RI
    public void testComplexQuery() throws Exception {
        Requirement req = new RequirementImpl("osgi.wiring.package",
                "(|(osgi.wiring.package=org.osgi.test.cases.repository.tb1.pkg1)(osgi.wiring.package=org.osgi.test.cases.repository.tb2))");
        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(req);

        assertEquals(1, result.size());
        Collection<Capability> matches = result.get(req);
        assertEquals(2, matches.size());

        boolean foundtb1 = false, foundtb2 = false;
        for (Capability cap : matches) {
            if (cap.getAttributes().get("bundle-symbolic-name").equals("org.osgi.test.cases.repository.tb1")) {
                foundtb1 = true;
            } else if (cap.getAttributes().get("bundle-symbolic-name").equals("org.osgi.test.cases.repository.tb2")) {
                foundtb2 = true;
            }
        }

        assertTrue(foundtb1);
        assertTrue(foundtb2);
    }

    // Fails in RI
    public void testComplexQueryWithCustomAttributeSpecificValue() throws Exception {
        Requirement req = new RequirementImpl("osgi.wiring.package",
                "(&(|(osgi.wiring.package=org.osgi.test.cases.repository.tb1.pkg1)(osgi.wiring.package=org.osgi.test.cases.repository.tb2))(approved=yes))");
        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(req);
        assertEquals(1, result.size());
        Collection<Capability> matches = result.get(req);
        assertEquals(1, matches.size());
        Capability capability = matches.iterator().next();
        assertEquals("org.osgi.test.cases.repository.tb2", capability.getAttributes().get("bundle-symbolic-name"));
    }

    // Fails in RI
    public void testComplexQueryWithCustomAttributeDefined() throws Exception {
        Requirement req = new RequirementImpl("osgi.wiring.package",
                "(&(|(osgi.wiring.package=org.osgi.test.cases.repository.tb1.pkg1)(osgi.wiring.package=org.osgi.test.cases.repository.tb2))(approved=*))");
        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(req);

        assertEquals(1, result.size());
        Collection<Capability> matches = result.get(req);
        assertEquals(2, matches.size());

        boolean foundtb1 = false, foundtb2 = false;
        for (Capability cap : matches) {
            if (cap.getAttributes().get("bundle-symbolic-name").equals("org.osgi.test.cases.repository.tb1")) {
                foundtb1 = true;
            } else if (cap.getAttributes().get("bundle-symbolic-name").equals("org.osgi.test.cases.repository.tb2")) {
                foundtb2 = true;
            }
        }

        assertTrue(foundtb1);
        assertTrue(foundtb2);
    }

    // Fails in RI, requires custom namespace capability to be commented out in content1.xml
    public void testQueryCustomNamespace() throws Exception {
        Requirement req = new RequirementImpl("osgi.foo.bar", "(myattr=myval)");
        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(req);
        assertEquals(1, result.size());
        Collection<Capability> matches = result.get(req);
        assertEquals(1, matches.size());
        Capability capability = matches.iterator().next();
        assertEquals("myval", capability.getAttributes().get("myattr"));
    }

    public void testRepositoryContent2() throws Exception {
        // on tb1 TODO!
        fail();
    }

    // TODO fails sometimes on the SHA computation
    public void testRepositoryContent() throws Exception {
        Requirement req = new RequirementImpl("osgi.identity", "(osgi.identity=org.osgi.test.cases.repository.tb2)");
        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(req);
        assertEquals(1, result.size());
        Collection<Capability> matches = result.get(req);
        assertEquals(1, matches.size());
        Capability capability = matches.iterator().next();
        assertEquals("org.osgi.test.cases.repository.tb2", capability.getAttributes().get("osgi.identity"));

        Resource resource = capability.getResource();

        // test getCapabilities();
        List<Capability> identityCaps = resource.getCapabilities("osgi.identity");
        assertEquals(1, identityCaps.size());
        Capability identityCap = identityCaps.iterator().next();
        assertEquals("org.osgi.test.cases.repository.tb2", identityCap.getAttributes().get("osgi.identity"));
        assertEquals(Version.parseVersion("1"), identityCap.getAttributes().get("version"));
        assertEquals("osgi.bundle", identityCap.getAttributes().get("type"));
        assertEquals("http://www.opensource.org/licenses/Apache-2.0", identityCap.getAttributes().get("license"));

        List<Capability> contentCaps = resource.getCapabilities("osgi.content");
        assertEquals(1, contentCaps.size());
        Capability contentCap = contentCaps.iterator().next();
        // content and SHA is checked below

        assertEquals(1, resource.getCapabilities("osgi.wiring.bundle").size());

        List<Capability> wiringCaps = resource.getCapabilities("osgi.wiring.package");
        assertEquals(1, wiringCaps.size());
        Capability wiringCap = wiringCaps.iterator().next();
        assertEquals("org.osgi.test.cases.repository.tb2", wiringCap.getAttributes().get("osgi.wiring.package"));
        assertEquals(Version.parseVersion("1.2.3.qualified"), wiringCap.getAttributes().get("version"));
        assertEquals(Version.parseVersion("1"), wiringCap.getAttributes().get("bundle-version"));
        assertEquals("org.osgi.test.cases.repository.tb2", wiringCap.getAttributes().get("bundle-symbolic-name"));
        assertEquals("yes", wiringCap.getAttributes().get("approved"));
        assertEquals("org.osgi.test.cases.repository.tb1.pkg1", wiringCap.getDirectives().get("uses"));
        // TODO enable assertEquals(1, resource.getCapabilities("osgi.foo.bar").size());

        // Read the requirements
        assertEquals(0, resource.getRequirements("org.osgi.nonexistent").size());
        List<Requirement> wiringReqs = resource.getRequirements("osgi.wiring.package");
        assertEquals(1, wiringReqs.size());
        Requirement wiringReq = wiringReqs.iterator().next();
        assertEquals(2, wiringReq.getDirectives().size());
        assertEquals("custom directive", wiringReq.getDirectives().get("custom"));
        assertEquals("(&(osgi.wiring.package=org.osgi.test.cases.repository.tb1.pkg1)(version>=1.1)(!(version>=2)))",
                wiringReq.getDirectives().get("filter"));
        assertEquals(1, wiringReq.getAttributes().size());
        assertEquals(new Long(42), wiringReq.getAttributes().get("custom"));

        assertEquals("Only the wiring requirements exist", wiringReqs, resource.getRequirements(null));

        // Check content and SHA
        RepositoryContent repositoryContent = (RepositoryContent) resource;
        byte[] contentBytes = readFully(repositoryContent.getContent());
        assertTrue(contentBytes.length > 0);
        assertEquals(new Long(contentBytes.length), contentCap.getAttributes().get("size"));
        assertEquals(getSHA256(contentBytes), contentCap.getAttributes().get("osgi.content"));
        // of the SHA contains bytes < 16 the leading 0 is not added to the output
        // expected:<04fe1203ebeff5c59c3795d52bff4a10a31a0bdbc4c6ec0334f78104bb1f2485>
        // but was:<4fe123ebeff5c59c3795d52bff4a10a31abdbc4c6ec334f7814bb1f2485>
    }

    private Map<Requirement, Collection<Capability>> findProvidersAllRepos(Requirement ... requirement) {
        Map<Requirement, Collection<Capability>> result = new HashMap<Requirement, Collection<Capability>>();
        for (Repository repository : repositoryServices) {
            Map<Requirement, Collection<Capability>> r =
                     repository.findProviders(Arrays.asList(requirement));

            for (Map.Entry<Requirement, Collection<Capability>> entry : r.entrySet()) {
                Collection<Capability> caps = result.get(entry.getKey());
                if (caps == null) {
                    result.put(entry.getKey(), entry.getValue());
                } else {
                    caps.addAll(entry.getValue());
                }
            }
        }
        return result;
    }

    private String getRepoXML() throws Exception {
        URL url = getContext().getBundle().getResource("/xml/content1.xml");
        String xml = new String(readFully(url.openStream()));

        xml = fillInTemplate(xml, "tb1");
        xml = fillInTemplate(xml, "tb2");

        return xml;
    }

    private String fillInTemplate(String xml, String bundleName) throws IOException, NoSuchAlgorithmException {
        URL url = getContext().getBundle().getResource(bundleName + ".jar");
        byte[] bytes = readFully(url.openStream());

        xml = xml.replaceAll("@@" + bundleName + "SHA256@@", getSHA256(bytes));
        xml = xml.replaceAll("@@" + bundleName + "URL@@", url.toExternalForm());
        xml = xml.replaceAll("@@" + bundleName + "Size@@", "" + bytes.length);
        return xml;
    }

    private static String getSHA256(byte[] bytes) throws NoSuchAlgorithmException {
        StringBuilder builder = new StringBuilder();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        for (byte b : md.digest(bytes)) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static void readFully(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[4096];

        int length = 0;
        int offset = 0;

        while ((length = is.read(bytes, offset, bytes.length - offset)) != -1) {
            offset += length;

            if (offset == bytes.length) {
                os.write(bytes, 0, bytes.length);
                offset = 0;
            }
        }
        if (offset != 0) {
            os.write(bytes, 0, offset);
        }
    }

    public static byte [] readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            readFully(is, baos);
            return baos.toByteArray();
        } finally {
            is.close();
        }
    }
}
