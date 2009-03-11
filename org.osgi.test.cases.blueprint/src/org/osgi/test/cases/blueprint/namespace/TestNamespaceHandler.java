/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.blueprint.namespace;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.HashMap;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.namespace.NamespaceHandler;
import org.osgi.service.blueprint.namespace.ParserContext;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * Simple handler for testing the processing of
 * multiple namespaces by a single handler.  This handler
 * will only process decorate events, as the elements
 * and attributes it defines are extensions to existing
 * elements.
 */
public class TestNamespaceHandler implements NamespaceHandler {

    /**
     * The schema file is packaged in the same bundle as this handler class
     * for convenience, in schemas/date.xsd
     */
    private static final String SCHEMA_LOCATION = "schemas/date.xsd";

    private final Map schemas = new HashMap();

    private final BundleContext bundleContext;

    public TestNamespaceHandler(BundleContext context) {
        this.bundleContext = context;
        // create a map of the schemas we handle
        schemas.put("http://www.osgi.org/schema/blueprint/test/test1/v1.0.0",
            bundleContext.getBundle().getResource("schemas/test1.xsd"));
        schemas.put("http://www.osgi.org/schema/blueprint/test/test1",
            bundleContext.getBundle().getResource("schemas/test1.xsd"));
        schemas.put("http://www.osgi.org/schema/blueprint/test/test2/v1.0.0",
            bundleContext.getBundle().getResource("schemas/test2.xsd"));
        schemas.put("http://www.osgi.org/schema/blueprint/test/test2",
            bundleContext.getBundle().getResource("schemas/test2.xsd"));
    }

    /**
     * Use the bundleContext to return a URL for accessing the schema definition file
     */
    public URL getSchemaLocation(String namespace)  throws IllegalArgumentException {
        URL url = (URL)schemas.get(namespace);
        if (url == null) {
            throw new IllegalArgumentException("Unknown schema: " + namespace);
        }
        return url;
    }

    /**
     * Date elements can never be used nested directly inside a component, so
     * nothing for us to do.
     */
    public ComponentMetadata decorate(Node node, ComponentMetadata component, ParserContext context) {
        if (node instanceof Attr) {
            Attr attribute = (Attr)node;
            if (attribute.getName().equals("sleepy")) {
            }
        }
        return null;
    }


    /**
     * Handle the "dateformat" tag (and others in time...)
     */
    public ComponentMetadata parse(Element element, ParserContext context) {
        // we don't recognize anything that would be called for parse...different test case
        return null;
    }
}

