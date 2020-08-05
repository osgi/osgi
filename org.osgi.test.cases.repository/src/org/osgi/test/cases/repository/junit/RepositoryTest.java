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

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.repository.Repository;
import org.osgi.service.repository.RepositoryContent;

/**
 * @author David Bosschaert
 */
public class RepositoryTest extends RepositoryTestBase {
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

    public void testQueryNoMatch() throws Exception {
        Requirement requirement = new RequirementImpl("osgi.wiring.bundle",
                "(&(osgi.wiring.bundle=org.osgi.test.cases.repository.tb1)(foo=bar))");

        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(requirement);
        assertEquals(1, result.size());
        assertEquals(requirement, result.keySet().iterator().next());

        Collection<Capability> matchingCapabilities = result.get(requirement);
        assertEquals(0, matchingCapabilities.size());
    }

    public void testQueryNoFilter() throws Exception {
        Requirement requirement = new RequirementImpl("osgi.wiring.bundle");
        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(requirement);
        assertEquals(1, result.size());
        Collection<Capability> matches = result.get(requirement);
        assertEquals(4, matches.size());

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

    public void testQueryCustomNamespace() throws Exception {
        Requirement req = new RequirementImpl("osgi.foo.bar", "(myattr=myval)");
        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(req);
        assertEquals(1, result.size());
        Collection<Capability> matches = result.get(req);
        assertEquals(1, matches.size());
        Capability capability = matches.iterator().next();
        assertEquals("myval", capability.getAttributes().get("myattr"));
    }

    public void testRepositoryContent() throws Exception {
        Requirement req = new RequirementImpl("osgi.identity", "(osgi.identity=org.osgi.test.cases.repository.tb1)");
        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(req);
        assertEquals(1, result.size());
        Collection<Capability> matches = result.get(req);
        assertEquals(1, matches.size());

        Capability capability = matches.iterator().next();
        assertEquals("osgi.identity", capability.getNamespace());
        assertEquals("org.osgi.test.cases.repository.tb1", capability.getAttributes().get("osgi.identity"));
        assertEquals(new Version("1.0.0.test"), capability.getAttributes().get("version"));
        assertEquals("osgi.bundle", capability.getAttributes().get("type"));
        assertEquals(0, capability.getDirectives().size());

        Resource resource = capability.getResource();
        assertEquals("This resource has no requirements", 0, resource.getRequirements(null).size());

        List<Capability> bundleCaps = resource.getCapabilities("osgi.wiring.bundle");
        assertEquals(1, bundleCaps.size());
        Capability bundleCap = bundleCaps.get(0);
        assertEquals("osgi.wiring.bundle", bundleCap.getNamespace());
        assertEquals("org.osgi.test.cases.repository.tb1", bundleCap.getAttributes().get("osgi.wiring.bundle"));
        assertEquals(new Version("1.0.0.test"), bundleCap.getAttributes().get("bundle-version"));
        assertEquals(0, bundleCap.getDirectives().size());

        boolean foundPkg1 = false;
        boolean foundPkg2 = false;
        List<Capability> packageCaps = resource.getCapabilities("osgi.wiring.package");
        assertEquals(2, packageCaps.size());
        for (Capability packageCap : packageCaps) {
            assertEquals("osgi.wiring.package", packageCap.getNamespace());
            assertEquals(0, packageCap.getDirectives().size());
            Map<String, Object> attrs = packageCap.getAttributes();
            assertEquals("org.osgi.test.cases.repository.tb1", attrs.get("bundle-symbolic-name"));
            assertEquals(new Version("1.0.0.test"), attrs.get("bundle-version"));
            assertEquals("no", attrs.get("approved"));

            if ("org.osgi.test.cases.repository.tb1.pkg1".equals(attrs.get("osgi.wiring.package"))) {
                assertEquals(new Version("0.9"), attrs.get("version"));
                foundPkg1 = true;
            } else if ("org.osgi.test.cases.repository.tb1.pkg2".equals(attrs.get("osgi.wiring.package"))) {
                assertEquals(new Version("0.8"), attrs.get("version"));
                foundPkg2 = true;
            }
        }
        assertTrue(foundPkg1);
        assertTrue(foundPkg2);

        List<Capability> fooBarCaps = resource.getCapabilities("osgi.foo.bar");
        assertEquals(1, fooBarCaps.size());
        Capability fooBarCap = fooBarCaps.iterator().next();
        assertEquals("osgi.foo.bar", fooBarCap.getNamespace());
        assertEquals(0, fooBarCap.getDirectives().size());
        assertEquals("myval", fooBarCap.getAttributes().get("myattr"));

        // test osgi.content
        List<Capability> contentCaps = resource.getCapabilities("osgi.content");
        assertEquals(1, contentCaps.size());
        Capability contentCap = contentCaps.iterator().next();
        assertEquals("osgi.content", contentCap.getNamespace());
        assertEquals(0, contentCap.getDirectives().size());
        assertEquals("application/vnd.osgi.bundle", contentCap.getAttributes().get("mime"));
        String url = (String) contentCap.getAttributes().get("url");
        byte[] contentBytes = readFully(new URL(url).openStream());
        assertTrue(contentBytes.length > 0);
        assertEquals(Long.valueOf(contentBytes.length), contentCap.getAttributes().get("size"));
        assertEquals(getSHA256(contentBytes), contentCap.getAttributes().get("osgi.content"));
    }

    public void testRepositoryContent2() throws Exception {
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
        assertEquals(1, resource.getCapabilities("osgi.foo.bar").size());

        // Read the requirements
        assertEquals(0, resource.getRequirements("org.osgi.nonexistent").size());
        List<Requirement> wiringReqs = resource.getRequirements("osgi.wiring.package");
        assertEquals(1, wiringReqs.size());
        Requirement wiringReq = wiringReqs.iterator().next();
        assertEquals(2, wiringReq.getDirectives().size());
        assertEquals("custom directive", wiringReq.getDirectives().get("custom"));
        assertEquals("(&(osgi.wiring.package=org.osgi.test.cases.repository.tb1.pkg1)(version>=0.9)(!(version>=1)))",
                wiringReq.getDirectives().get("filter"));
        assertEquals(1, wiringReq.getAttributes().size());
        assertEquals(Long.valueOf(42), wiringReq.getAttributes().get("custom"));

        List<Requirement> metaReqs = resource.getRequirements("osgi.identity");
        assertEquals(2, metaReqs.size());

        boolean foundSources = false;
        boolean foundJavaDoc = false;
        for (Requirement metaReq : metaReqs) {
            assertEquals("osgi.identity", metaReq.getNamespace());
            assertEquals(0, metaReq.getAttributes().size());
            Map<String, String> dirs = metaReq.getDirectives();
            assertEquals("meta", dirs.get("effective"));
            assertEquals("optional", dirs.get("resolution"));
            if ("sources".equals(dirs.get("classifier"))) {
                assertEquals("(&(version=1.0)(osgi.identity=org.osgi.test.cases.repository.tb2-src))", dirs.get("filter"));
                foundSources = true;
            } else if ("javadoc".equals(dirs.get("classifier"))) {
                assertEquals("(&(version=1.0)(osgi.identity=org.osgi.test.cases.repository.tb2-javadoc))", dirs.get("filter"));
                foundJavaDoc = true;
            }
        }

        assertTrue(foundSources);
        assertTrue(foundJavaDoc);

        Set<Requirement> allReqs = new HashSet<Requirement>();
        allReqs.addAll(wiringReqs);
        allReqs.addAll(metaReqs);
        assertEquals(allReqs, new HashSet<Requirement>(resource.getRequirements(null)));

        // Check content and SHA
        URL tb1JarURL = getContext().getBundle().getResource("tb2.jar");
        byte[] expectedBytes = readFully(tb1JarURL.openStream());

        RepositoryContent repositoryContent = (RepositoryContent) resource;
        byte[] contentBytes = readFully(repositoryContent.getContent());
        assertTrue(contentBytes.length > 0);
        assertTrue(Arrays.equals(expectedBytes, contentBytes));
        assertEquals(Long.valueOf(contentBytes.length), contentCap.getAttributes().get("size"));
        assertEquals(getSHA256(contentBytes), contentCap.getAttributes().get("osgi.content"));
    }

	public void testAttributeDataTypes() throws Exception {
        Requirement req = new RequirementImpl("osgi.test.namespace", "(osgi.test.namespace=a testing namespace)");
        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(req);
        assertEquals(1, result.size());
        Collection<Capability> matches = result.get(req);
        assertEquals(1, matches.size());
        Capability cap = matches.iterator().next();

        assertEquals(req.getNamespace(), cap.getNamespace());
        assertEquals("a testing namespace", cap.getAttributes().get("osgi.test.namespace"));
        assertEquals("", cap.getAttributes().get("testString"));
        assertEquals(",", cap.getAttributes().get("testString2"));
        assertEquals("a,b", cap.getAttributes().get("testString3"));
        assertEquals(Version.parseVersion("1.2.3.qualifier"), cap.getAttributes().get("testVersion"));
        assertEquals(Long.valueOf(Long.MAX_VALUE), cap.getAttributes().get("testLong"));
        assertEquals(Double.valueOf(Math.PI), cap.getAttributes().get("testDouble"));
        assertEquals(Arrays.asList("a", "b and c", "d"), cap.getAttributes().get("testStringList"));
        assertEquals(Arrays.asList(",", "\\,\\"), cap.getAttributes().get("testStringList2"));
        assertEquals(Arrays.asList(","), cap.getAttributes().get("testStringList3"));
        assertEquals(Arrays.asList(new Version("1.2.3"), new Version("4.5.6")), cap.getAttributes().get("testVersionList"));
        assertEquals(Arrays.asList(Long.MIN_VALUE, 0l, Long.MAX_VALUE), cap.getAttributes().get("testLongList"));
        assertEquals(Arrays.asList(Math.E, Math.E), cap.getAttributes().get("testDoubleList"));
    }

    public void testQueryExpressions() throws Exception {
        Requirement emptyReq = new RequirementImpl("osgi.test.namespace");
        Map<Requirement, Collection<Capability>> result = findProvidersAllRepos(emptyReq);
        assertEquals(1, result.size());
        Collection<Capability> match = result.get(emptyReq);
        assertEquals(2, match.size());

        Capability expected = null;
        for (Capability cap : match) {
            if ("a testing namespace".equals(cap.getAttributes().get("osgi.test.namespace"))) {
                expected = cap;
                break;
            }
        }

        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testString=*)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testString=foo)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testString2=,)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testString2=a,b)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testString3=a,b)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testString3=,)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testVersion=1.2.3.qualifier)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testVersion=2.0)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testVersion>=1.0)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testVersion>=2.0)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testVersion<=1.2.3.qualifier)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(|(testVersion<=1.0)(testVersion>=2.0))"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(&(testVersion>=1.0)(!(testVersion>=2.0)))"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(&(testVersion>=1.0)(!(testVersion>=1.1)))"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testStringList=d)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testStringList=e)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testStringList=b*c)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testStringList=b*d)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testStringList~=D)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testStringList~=E)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testStringList2=,)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testStringList2=\\\\,\\\\)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testStringList2=\\\\)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testStringList3=,)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testStringList3=\\\\)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testLong=" + Long.MAX_VALUE + ")"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testLong=" + Long.MIN_VALUE + ")"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testDouble>=3)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testDouble>=3.2)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testVersionList=*)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testVersionList=1.2.3)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testVersionList=4.5.6)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testVersionList=1.0)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testLongList<=0)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testLongList>=0)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testLongList=0)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testLongList=1)"));
        assertEquals(expected, findSingleCapSingleReq("osgi.test.namespace", "(testDoubleList=2.718281828459045)"));
        assertNull(findSingleCapSingleReq("osgi.test.namespace", "(testDoubleList=0)"));
    }

    public void testMultiContent() throws Exception {
        Resource res = findSingleCapSingleReq("osgi.identity", "(osgi.identity=org.osgi.test.cases.repository.testresource)").getResource();
        List<Capability> caps = res.getCapabilities("osgi.content");
        assertEquals(2, caps.size());

        boolean foundZip = false;
        boolean foundTgz = false;
        for (Capability cap : caps) {
            Map<String, Object> attrs = cap.getAttributes();

            URL url = null;
            if ("application/vnd.osgi.test.zip".equals(attrs.get("mime"))) {
                url = getContext().getBundle().getResource("/testresource.zip");
                foundZip = true;
            } else if ("application/vnd.osgi.test.tar.gz".equals(attrs.get("mime"))) {
                url = getContext().getBundle().getResource("/testresource.tar.gz");
                foundTgz = true;
            }

            byte[] expectedBytes = readFully(url.openStream());

            byte[] actualBytes = readFully(new URL((String) attrs.get("url")).openStream());
            assertTrue(Arrays.equals(expectedBytes, actualBytes));
            assertEquals(Long.valueOf(expectedBytes.length), attrs.get("size"));
            assertEquals(getSHA256(expectedBytes), attrs.get("osgi.content"));
        }
        assertTrue(foundZip);
        assertTrue(foundTgz);
    }

    public void testSHA256ComputationSanityCheck() throws Exception {
        // This test validates the SHA256 computation in this test class with the example SHA256 serializations
        // as listed here: http://en.wikipedia.org/wiki/SHA-2
        // SHA256("")
        // e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855

        // SHA256("The quick brown fox jumps over the lazy dog")
        // d7a8fbb307d7809469ca9abcb0082e4f8d5651e46d3cdb762d02d0bf37c9e592

        // SHA256("The quick brown fox jumps over the lazy dog.")
        // ef537f25c895bfa782526529a9b63d97aa631564d5d789c2b765448c8635fb6c

        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                getSHA256("".getBytes()));
        assertEquals("d7a8fbb307d7809469ca9abcb0082e4f8d5651e46d3cdb762d02d0bf37c9e592",
                getSHA256("The quick brown fox jumps over the lazy dog".getBytes()));
        assertEquals("ef537f25c895bfa782526529a9b63d97aa631564d5d789c2b765448c8635fb6c",
                getSHA256("The quick brown fox jumps over the lazy dog.".getBytes()));
    }

    private Capability findSingleCapSingleReq(String ns, String filter) {
        Requirement req = new RequirementImpl(ns, filter);
        Map<Requirement, Collection<Capability>> res = findProvidersAllRepos(req);
        assertEquals(1, res.size());
        Collection<Capability> caps = res.get(req);
        if (caps.size() == 0)
            return null;

        assertEquals(1, caps.size());
        return caps.iterator().next();
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
}
