package org.osgi.test.cases.repository.junit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

public class RepositoryTest extends DefaultTestBundleControl {
    public static final String REPOSITORY_XML_KEY = "repository-xml";
    public static final String REPOSITORY_POPULATED_KEY = "repository-populated";

    private ServiceRegistration<String> repositoryXMLService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        String repoXML = getRepoXML();
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(REPOSITORY_XML_KEY, getClass().getName());
        repositoryXMLService = getContext().registerService(String.class, repoXML, props);

        Filter filter = getContext().createFilter("(" + REPOSITORY_POPULATED_KEY + "=" + getClass().getName() + ")");
        ServiceTracker<?,?> st = new ServiceTracker<Object, Object>(getContext(), filter, null);
        st.open();
        Object svc = st.waitForService(30000);
        st.close();

        if (svc == null)
            throw new IllegalStateException("Repository TCK integration code did not report that Repository population is finished. "
                    + "It should should register a service with property: " + REPOSITORY_POPULATED_KEY + "=" + getClass().getName());

        System.err.println("*** Repository TCK integration reports that the Repository has been populated: " + svc);
    }

    @Override
    protected void tearDown() throws Exception {
        repositoryXMLService.unregister();

        super.tearDown();
    }

    public void testFooXX() throws Exception {
        for (Bundle b : getContext().getBundles()) {
            System.err.println("@@@@ " + b.getSymbolicName() + "#" + b.getLocation());
        }

        for (ServiceReference<?> sr : getContext().getAllServiceReferences(null, null)) {
            System.err.println("**** " + Arrays.toString((String []) sr.getProperty(Constants.OBJECTCLASS)));
        }

        printResources("/xml/content1.xml");
        printResources("tb1.jar");
        printResources("tb2.jar");

    }

    private void printResources(String res) throws IOException {
        Enumeration<URL> entries = getContext().getBundle().getResources(res);
        while (entries.hasMoreElements()) {
            System.err.println("$$$ " + entries.nextElement());
        }
    }

    private String getRepoXML() throws Exception {
        URL url = getContext().getBundle().getResource("/xml/content1.xml");
        String xml = new String(readFully(url.openStream()));

        URL tb1Url = getContext().getBundle().getResource("tb1.jar");
        byte[] tb1Bytes = readFully(tb1Url.openStream());

        xml = xml.replaceAll("@@tb1SHA256@@", getSHA256(tb1Bytes));
        xml = xml.replaceAll("@@tb1URL@@", tb1Url.toExternalForm());
        xml = xml.replaceAll("@@tb1Size@@", "" + tb1Bytes.length);

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
        byte[] bytes = new byte[8192];

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
