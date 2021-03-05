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
package org.osgi.test.cases.repository.junit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.repository.Repository;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author David Bosschaert
 */
public abstract class RepositoryTestBase extends DefaultTestBundleControl {
    public static final String REPOSITORY_XML_KEY = "repository-xml";
    public static final String REPOSITORY_POPULATED_KEY = "repository-populated";

    private ServiceRegistration<String> repositoryXMLService;
    private ServiceTracker<Repository, Repository> repositoryServiceTracker;
    protected List<Repository> repositoryServices = new CopyOnWriteArrayList<Repository>();

    /**
     * As the Repository spec doesn't specify how the Repository is primed with
     * information, a Repository implementation must supply an integration
     * bundle which primes the repository with information as expected by this
     * test case. This process works as follows:
     * <ul>
     * <li>The test registers a String service which holds the Repository XML
     * that contains the expected content.</li>
     * <li>The service has the property REPOSITORY_XML_KEY set to the name of
     * this class.</li>
     * <li>The integration bundle must listen to this service and register one
     * or more Repository service implementations that serve the information as
     * specified in the XML.</li>
     * <li>When the integration bundle is finished with its setup it must
     * register a service with the property REPOSITORY_POPULATED_KEY set to the
     * name of this test class. The service object or registration class are
     * ignored by the test.</li>
     * <li>The test waits for this service to appear and runs the tests when it
     * does.</li>
     * </ul>
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        String repoXML = getRepoXML();
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(REPOSITORY_XML_KEY, RepositoryTest.class.getName());
        repositoryXMLService = getContext().registerService(String.class, repoXML, props);

        Filter filter = getContext().createFilter("(" + REPOSITORY_POPULATED_KEY + "=" + RepositoryTest.class.getName() + ")");
        ServiceTracker<?, ?> populatedST = new ServiceTracker<Object, Object>(getContext(), filter, null);
        populatedST.open();
        Object svc = populatedST.waitForService(30000);
        populatedST.close();

        if (svc == null)
            throw new IllegalStateException(
                    "Repository TCK integration code did not report that Repository population is finished. "
                            + "It should should register a service with property: " + REPOSITORY_POPULATED_KEY + "="
                            + RepositoryTest.class.getName());

        repositoryServiceTracker = new ServiceTracker<Repository, Repository>(getContext(), Repository.class, null) {
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

    private String getRepoXML() throws Exception {
        URL url = getContext().getBundle().getResource("/xml/content1.xml");
        String xml = new String(readFully(url.openStream()));

        xml = fillInTemplate(xml, "tb1");
        xml = fillInTemplate(xml, "tb2");
        xml = fillInTemplate(xml, "tb3");
        xml = fillInTemplate(xml, "tb4");
        xml = fillInResourceTemplate(xml, "testresource.zip", "trzip");
        xml = fillInResourceTemplate(xml, "testresource.tar.gz", "trtgz");

        return xml;
    }

    private String fillInTemplate(String xml, String bundleName) throws IOException, NoSuchAlgorithmException {
        return fillInResourceTemplate(xml, bundleName + ".jar", bundleName);
    }

    private String fillInResourceTemplate(String xml, String resource, String name) throws IOException, NoSuchAlgorithmException {
        URL url = getContext().getBundle().getResource(resource);
        byte[] bytes = readFully(url.openStream());

        xml = xml.replaceAll("@@" + name + "SHA256@@", getSHA256(bytes));
        xml = xml.replaceAll("@@" + name + "URL@@", url.toExternalForm());
        xml = xml.replaceAll("@@" + name + "Size@@", "" + bytes.length);
        return xml;
    }

    protected static String getSHA256(byte[] bytes) throws NoSuchAlgorithmException {
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

    public static byte[] readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            readFully(is, baos);
            return baos.toByteArray();
        } finally {
            is.close();
        }
    }
}
