/*
 * Copyright (c) OSGi Alliance 2013.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.repository.ExpressionCombiner;
import org.osgi.service.repository.IdentityExpression;
import org.osgi.service.repository.Repository;
import org.osgi.service.repository.RequirementBuilder;
import org.osgi.service.repository.RequirementExpression;

/**
 * @author David Bosschaert
 */
public class Respository1_1Test extends RepositoryTestBase {
    public void testRequirementBuilderNamespace() throws Exception {
        RequirementBuilder builder = getRepo().newRequirementBuilder("something");
        Requirement req = builder.build();
        assertEquals("something", req.getNamespace());
        assertNull(req.getResource());
        assertEquals(0, req.getAttributes().size());
        assertEquals(0, req.getDirectives().size());
    }

    public void testRequirementBuilderAttrsDirs() throws Exception {
        RequirementBuilder b1 = getRepo().newRequirementBuilder("org.acme.test");
        b1.addAttribute("a", "a1");
        b1.addAttribute("b", "b1");
        b1.addDirective("d", "d1");
        b1.addDirective("e", "e1");
        b1.addDirective("filter", "(a=b)");
        Requirement req1 = b1.build();

        assertEquals(2, req1.getAttributes().size());
        assertEquals("a1", req1.getAttributes().get("a"));
        assertEquals("b1", req1.getAttributes().get("b"));
        assertEquals(3, req1.getDirectives().size());
        assertEquals("d1", req1.getDirectives().get("d"));
        assertEquals("e1", req1.getDirectives().get("e"));
        assertEquals("(a=b)", req1.getDirectives().get("filter"));

        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put("a", "a1");
        attrs.put("b", "b1");
        Map<String, String> dirs = new HashMap<String, String>();
        dirs.put("d", "d1");
        dirs.put("e", "e1");
        dirs.put("filter", "(a=b)");
        RequirementBuilder b2 = getRepo().newRequirementBuilder("org.acme.test");
        b2.addAttribute("a", "xxx"); // will get replaced
        b2.addDirective("f", "yyy"); // will get replaced
        b2.setAttributes(attrs);
        b2.setDirectives(dirs);
        Requirement req2 = b2.build();

        assertEquals(2, req2.getAttributes().size());
        assertEquals("a1", req2.getAttributes().get("a"));
        assertEquals("b1", req2.getAttributes().get("b"));
        assertEquals(3, req2.getDirectives().size());
        assertEquals("d1", req2.getDirectives().get("d"));
        assertEquals("e1", req2.getDirectives().get("e"));
        assertEquals("(a=b)", req2.getDirectives().get("filter"));

        assertEquals(req1, req2);

        Requirement otherReq = getRepo().newRequirementBuilder("org.acme.test").build();
        assertFalse(req1.equals(otherReq));
    }

    public void testRequirementBuilderResource() throws Exception {
        RequirementBuilder builder = getRepo().newRequirementBuilder("osgi.test.namespace");
        Resource tr = new TestResource();
        builder.setResource(tr);
        Requirement req = builder.build();
        assertSame(tr, req.getResource());
    }

    public void testSimpleRequirementExpression() throws Exception {
        RequirementBuilder builder = getRepo().newRequirementBuilder("osgi.wiring.bundle");
        builder.addDirective("filter", "(&(osgi.wiring.bundle=org.osgi.test.cases.repository.tb1)(bundle-version=1.0.0.test))");

        RequirementExpression expr = builder.buildExpression();
        Set<Resource> result = findProvidersAllRepos(expr);
        assertEquals(1, result.size());
        Resource res = result.iterator().next();

        List<Capability> identityCaps = res.getCapabilities("osgi.identity");
        assertEquals(1, identityCaps.size());
        assertEquals("org.osgi.test.cases.repository.tb1", identityCaps.get(0).getAttributes().get("osgi.identity"));
    }

    public void testAndExpression() throws Exception {
        RequirementBuilder builder1 = getRepo().newRequirementBuilder("osgi.wiring.package");
        builder1.addDirective("filter", "(&(osgi.wiring.package=org.osgi.test.cases.repository.tbX)(version>=7)(!(version>=8)))");
        RequirementExpression wiringReq = builder1.buildExpression();

        RequirementBuilder builder2 = getRepo().newRequirementBuilder("osgi.content");
        builder2.addDirective("filter", "(mime=application/vnd.osgi.bundle)");
        RequirementExpression contentReq = builder2.buildExpression();

        ExpressionCombiner ec = getRepo().getExpressionCombiner();
        RequirementExpression wiringAndContent = ec.and(wiringReq, contentReq);
        Set<Resource> result1 = findProvidersAllRepos(wiringAndContent);
        assertEquals(2, result1.size());

        boolean foundTB3 = false;
        boolean foundTB4 = false;
        for (Resource r : result1) {
            Object id = r.getCapabilities("osgi.identity").iterator().next().getAttributes().get("osgi.identity");
            if ("org.osgi.test.cases.repository.tb3".equals(id)) {
                foundTB3 = true;
            }
            if ("org.osgi.test.cases.repository.tb4".equals(id)) {
                foundTB4 = true;
            }
        }
        assertTrue(foundTB3);
        assertTrue(foundTB4);

        RequirementBuilder builder3 = getRepo().newRequirementBuilder("osgi.identity");
        builder3.addDirective("filter", "(license=http://opensource.org/licenses/EPL*)");
        RequirementExpression licenseReq = builder3.buildExpression();
        RequirementExpression andLicense = ec.and(wiringAndContent, licenseReq);
        Set<Resource> result2 = findProvidersAllRepos(andLicense);
        assertEquals(1, result2.size());
        Resource resource = result2.iterator().next();
        Object id = resource.getCapabilities("osgi.identity").iterator().next().getAttributes().get("osgi.identity");
        assertEquals("org.osgi.test.cases.repository.tb4", id);
    }

    public void testMultiAndExpression() throws Exception {
        RequirementBuilder b1 = getRepo().newRequirementBuilder("osgi.content");
        b1.addDirective("filter", "(mime=application/vnd.osgi.bundle)");
        IdentityExpression re1 = b1.buildExpression();
        RequirementBuilder b2 = getRepo().newRequirementBuilder("osgi.wiring.package");
        b2.addDirective("filter", "(osgi.wiring.package=org.osgi.test.cases.repository.tbX)");
        IdentityExpression re2 = b2.buildExpression();
        RequirementBuilder b3 = getRepo().newRequirementBuilder("osgi.identity");
        b3.addDirective("filter", "(license=http://opensource.org/licenses/Apache-2.0)");
        IdentityExpression re3 = b3.buildExpression();

        Set<Resource> result = findProvidersAllRepos(getRepo().getExpressionCombiner().and(re1, re2, re3));
        assertEquals(1, result.size());
        Resource res = result.iterator().next();
        
        List<Capability> identityCaps = res.getCapabilities("osgi.identity");
        assertEquals(1, identityCaps.size());
        assertEquals("org.osgi.test.cases.repository.tb3", identityCaps.get(0).getAttributes().get("osgi.identity"));
    }

    public void testNotExpression() throws Exception {
        ExpressionCombiner ec = getRepo().getExpressionCombiner();
        RequirementBuilder builder = getRepo().newRequirementBuilder("osgi.foo.bar");
        builder.addDirective("filter", "(myattr=myotherval)");
        RequirementExpression req = builder.buildExpression();
        Set<Resource> result1 = findProvidersAllRepos(req);
        assertEquals(1, result1.size());
        Resource res1 = result1.iterator().next();
        assertEquals("org.osgi.test.cases.repository.tb2", res1.getCapabilities("osgi.identity").iterator().next()
                .getAttributes().get("osgi.identity"));

        Set<Resource> result2 = findProvidersAllRepos(ec.not(req));
        assertTrue(result2.size() > 0);
        for (Resource r : result2) {
            Object id = r.getCapabilities("osgi.identity").iterator().next().getAttributes().get("osgi.identity");
            assertTrue(!"org.osgi.test.cases.repository.tb2".equals(id));
        }
    }

    public void testOrExpression() throws Exception {
        RequirementBuilder builder1 = getRepo().newRequirementBuilder("osgi.test.namespace");
        builder1.addDirective("filter", "(osgi.test.namespace=*)");
        RequirementExpression req1 = builder1.buildExpression();

        RequirementBuilder builder2 = getRepo().newRequirementBuilder("osgi.identity");
        builder2.addDirective("filter", "(type=someresource)");
        RequirementExpression req2 = builder2.buildExpression();

        Set<Resource> result = findProvidersAllRepos(getRepo().getExpressionCombiner().or(req1, req2));
        assertEquals(2, result.size());

        boolean foundTB1 = false;
        boolean foundRes = false;
        for (Resource r : result) {
            Object id = r.getCapabilities("osgi.identity").iterator().next().getAttributes().get("osgi.identity");
            if ("org.osgi.test.cases.repository.tb1".equals(id)) {
                foundTB1 = true;
            }
            if ("org.osgi.test.cases.repository.testresource".equals(id)) {
                foundRes = true;
            }
        }
        assertTrue(foundTB1);
        assertTrue(foundRes);
    }

    public void testMultiOrExpression() throws Exception {
        RequirementBuilder b1 = getRepo().newRequirementBuilder("osgi.identity");
        b1.addDirective("filter", "(osgi.identity=org.osgi.test.cases.repository.tb1)");
        IdentityExpression re1 = b1.buildExpression();
        RequirementBuilder b2 = getRepo().newRequirementBuilder("osgi.foo.bar");
        b2.addDirective("filter", "(myattr=myotherval)");
        IdentityExpression re2 = b2.buildExpression();
        RequirementBuilder b3 = getRepo().newRequirementBuilder("osgi.wiring.bundle");
        b3.addDirective("filter", "(osgi.wiring.bundle=org.osgi.test.cases.repository.tb4)");
        IdentityExpression re3 = b3.buildExpression();

        Set<Resource> result = findProvidersAllRepos(getRepo().getExpressionCombiner().or(re1, re2, re3));
        assertEquals(3, result.size());

        boolean foundTB1 = false;
        boolean foundTB2 = false;
        boolean foundTB4 = false;
        for (Resource r : result) {
            Object id = r.getCapabilities("osgi.identity").iterator().next().getAttributes().get("osgi.identity");
            if ("org.osgi.test.cases.repository.tb1".equals(id)) {
                foundTB1 = true;
            }
            if ("org.osgi.test.cases.repository.tb2".equals(id)) {
                foundTB2 = true;
            }
            if ("org.osgi.test.cases.repository.tb4".equals(id)) {
                foundTB4 = true;
            }
        }
        assertTrue(foundTB1);
        assertTrue(foundTB2);
        assertTrue(foundTB4);
    }

    public void testCombinedExpression() throws Exception {
        RequirementBuilder builder1 = getRepo().newRequirementBuilder("osgi.wiring.package");
        builder1.addDirective("filter", "(osgi.wiring.package=org.osgi.test.cases.repository.tbX)");
        RequirementExpression req1 = builder1.buildExpression();
        RequirementBuilder builder2 = getRepo().newRequirementBuilder("osgi.wiring.bundle");
        builder2.addDirective("filter", "(osgi.wiring.bundle=org.osgi.test.cases.repository.tb4)");
        RequirementExpression req2 = builder2.buildExpression();

        ExpressionCombiner ec = getRepo().getExpressionCombiner();
        RequirementExpression andReq = ec.and(req1, req2);
        RequirementExpression notReq = ec.not(andReq);
        RequirementExpression andNotReq = ec.and(req1, notReq);

        RequirementExpression req3 = getRepo().newRequirementBuilder("osgi.test.namespace").buildExpression();
        RequirementExpression complexReq = ec.or(req3, andNotReq);

        Set<Resource> result = findProvidersAllRepos(complexReq);
        assertEquals(2, result.size());

        boolean foundTB1 = false;
        boolean foundTB3 = false;
        for (Resource r : result) {
            Object id = r.getCapabilities("osgi.identity").iterator().next().getAttributes().get("osgi.identity");
            if ("org.osgi.test.cases.repository.tb1".equals(id)) {
                foundTB1 = true;
            }
            if ("org.osgi.test.cases.repository.tb3".equals(id)) {
                foundTB3 = true;
            }
        }
        assertTrue(foundTB1);
        assertTrue(foundTB3);
    }

    private Set<Resource> findProvidersAllRepos(RequirementExpression expr) throws Exception {
        Set<Resource> resources = new HashSet<Resource>();
        for (Repository repository : repositoryServices) {
            resources.addAll(repository.findProviders(expr).getValue());
        }
        return resources;
    }

    private Repository getRepo() {
        return repositoryServices.get(0);
    }

    public static final class TestResource implements Resource {
        public List<Capability> getCapabilities(String namespace) {
            return null;
        }

        public List<Requirement> getRequirements(String namespace) {
            return null;
        }
    }
}
