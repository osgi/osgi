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

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.namespace.NamespaceHandler;
import org.osgi.service.blueprint.namespace.ParserContext;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Sample handler for a "date" namespace based on the example given here:
 * http://static.springframework.org/spring/docs/2.5.x/reference/extensible-xml.html
 *
 * Handles "dateformat" elements with the following form:
 *
 * <date:dateformat id="dateFormat"
 *   pattern="yyyy-MM-dd HH:mm"
 *   lenient="true"/>
 *
 */
public class DateNamespaceHandler implements NamespaceHandler {

    /**
     * The schema file is packaged in the same bundle as this handler class
     * for convenience, in schemas/date.xsd
     */
    private static final String SCHEMA_LOCATION = "schemas/date.xsd";

    private final BundleContext bundleContext;

    public DateNamespaceHandler(BundleContext context) {
        this.bundleContext = context;
    }

    /**
     * Use the bundleContext to return a URL for accessing the schema definition file
     */
    public URL getSchemaLocation(String namespace)  throws IllegalArgumentException {
        return bundleContext.getBundle().getResource(SCHEMA_LOCATION);
    }

    /**
     * Date elements can never be used nested directly inside a component, so
     * nothing for us to do.
     */
    public ComponentMetadata decorate(Node node, ComponentMetadata component, ParserContext context) {
        return null;
    }


    /**
     * Handle the "dateformat" tag (and others in time...)
     */
    public ComponentMetadata parse(Element element, ParserContext context) {

        if (element.getLocalName().equals("dateformat")) {
            return parseDateFormat(element);
        }
        else {
            throw new IllegalStateException("Asked to parse unknown tag: " + element.getTagName());
        }
    }

    /**
     * A dataformat element defines a component, which could be a top-level named
     * component or an anonymous inner component.
     */
    private ComponentMetadata parseDateFormat(Element element) {
        String name = element.hasAttribute("id") ? element.getAttribute("id") : "";
        LocalComponentMetadataImpl componentMetadata = new LocalComponentMetadataImpl(name);
        componentMetadata.setClassName(SimpleDateFormat.class.getName());

        // required attribute pattern
        String pattern = element.getAttribute("pattern");
        componentMetadata.addConstructorArg(pattern, 0);

        // this however is an optional property
        String lenient = element.getAttribute("lenient");
        if ((lenient != null) && !lenient.equals("")) {
            componentMetadata.addProperty("lenient", lenient);
        }

        return componentMetadata;
    }
}

